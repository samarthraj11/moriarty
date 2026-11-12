package com.moriartystudio.moriarty.data.repository

import java.time.DayOfWeek
import java.time.LocalDate
import com.moriartystudio.moriarty.domain.daily.DailyStreak
import com.moriartystudio.moriarty.domain.daily.DailyPuzzlePicker
import android.content.Context
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PlayerProfile
import com.moriartystudio.moriarty.core.model.Puzzle
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import com.moriartystudio.moriarty.data.local.AssetPuzzleLocalDataSource
import com.moriartystudio.moriarty.data.local.PuzzleLocalDataSource
import com.moriartystudio.moriarty.domain.progress.DayActivity
import com.moriartystudio.moriarty.domain.progress.ProgressSummary
import com.moriartystudio.moriarty.domain.progress.SectionProgress
import com.moriartystudio.moriarty.domain.progression.ProgressionRules
import com.moriartystudio.moriarty.domain.repository.GameRepository
import com.moriartystudio.moriarty.domain.scoring.Scoring

class LocalGameRepository(
    context: Context,
    puzzleDataSource: PuzzleLocalDataSource = AssetPuzzleLocalDataSource(context.assets),
) : GameRepository {
    private val preferences = context.getSharedPreferences("moriarty_progress", Context.MODE_PRIVATE)
    private val puzzles = puzzleDataSource.puzzles()

    override fun levels(): List<Level> {
        val solved = solvedLevels()
        val skipped = skippedLevels()
        val passed = solved + skipped
        val byCategory = puzzles.groupBy { it.category }.mapValues { (_, list) -> list.sortedBy { it.level } }
        return puzzles.map { puzzle ->
            val inCategory = byCategory.getValue(puzzle.category)
            val index = inCategory.indexOfFirst { it.level == puzzle.level }
            Level(
                number = puzzle.level,
                title = puzzle.title,
                category = puzzle.category,
                difficulty = puzzle.difficulty,
                isSolved = puzzle.level in solved,
                isUnlocked = ProgressionRules.isUnlockedInCategory(inCategory.getOrNull(index - 1)?.level, passed),
                points = preferences.takeIf { it.contains(pointsKey(puzzle.level)) }?.getInt(pointsKey(puzzle.level), 0),
                difficultyRating = puzzle.difficultyRating,
                isSkipped = puzzle.level in skipped && puzzle.level !in solved,
                categoryLevel = index + 1,
            )
        }
    }

    override fun puzzle(level: Int): Puzzle = puzzles.firstOrNull { it.level == level } ?: puzzles.first()

    override fun completeLevel(level: Int) {
        preferences.edit()
            .putStringSet(SOLVED_LEVELS, (solvedLevels() + level).map(Int::toString).toSet())
            .apply()
    }

    override fun recordFirstAnswer(level: Int, correct: Boolean): Int {
        if (preferences.contains(pointsKey(level))) return 0
        val points = Scoring.pointsForFirstAnswer(correct)
        val today = LocalDate.now().toString()
        val run = if (correct) preferences.getInt(CORRECT_RUN, 0) + 1 else 0
        preferences.edit()
            .putInt(pointsKey(level), points)
            .putInt(CORRECT_RUN, run)
            .putInt(BEST_CORRECT_RUN, maxOf(run, preferences.getInt(BEST_CORRECT_RUN, 0)))
            .putInt("$DAY_POINTS$today", preferences.getInt("$DAY_POINTS$today", 0) + points)
            .putInt("$DAY_PLAYED$today", preferences.getInt("$DAY_PLAYED$today", 0) + 1)
            .apply()
        return points
    }

    override fun totalPoints(): Int = puzzles.sumOf { preferences.getInt(pointsKey(it.level), 0) }

    // A skipped level unlocks the next one but is not counted as solved.
    override fun skipLevel(level: Int) {
        if (level in solvedLevels()) return
        preferences.edit()
            .putStringSet(SKIPPED_LEVELS, (skippedLevels() + level).map(Int::toString).toSet())
            .apply()
    }

    override fun dailyPuzzle(date: LocalDate): Puzzle {
        val ordered = puzzles.sortedBy { it.level }
        return ordered[DailyPuzzlePicker.indexFor(date, ordered.size)]
    }

    override fun dailyPlayedDays(): Set<LocalDate> = preferences.getStringSet(DAILY_PLAYED, emptySet())
        .orEmpty()
        .mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }
        .toSet()

    override fun recordDailyPlayed(date: LocalDate) {
        preferences.edit()
            .putStringSet(DAILY_PLAYED, (dailyPlayedDays() + date).map(LocalDate::toString).toSet())
            .apply()
    }

    override fun dailyStartDate(today: LocalDate): LocalDate {
        preferences.getString(DAILY_START, null)?.let { saved -> runCatching { LocalDate.parse(saved) }.getOrNull()?.let { return it } }
        preferences.edit().putString(DAILY_START, today.toString()).apply()
        return today
    }

    override fun progress(today: LocalDate): ProgressSummary {
        val levels = levels()
        val answered = levels.filter { it.points != null }
        val played = dailyPlayedDays()
        val sections = PuzzleCategory.entries.map { category ->
            val inCategory = levels.filter { it.category == category }
            val answeredHere = inCategory.filter { it.points != null }
            SectionProgress(
                category = category,
                total = inCategory.size,
                solved = inCategory.count(Level::isSolved),
                answered = answeredHere.size,
                firstTryCorrect = answeredHere.count { it.points == Scoring.CORRECT_POINTS },
            )
        }
        val week = (6 downTo 0).map { back ->
            val day = today.minusDays(back.toLong())
            DayActivity(day, preferences.getInt("$DAY_POINTS$day", 0), preferences.getInt("$DAY_PLAYED$day", 0))
        }
        val monday = today.with(DayOfWeek.MONDAY)
        val playedThisWeek = (0..6).sumOf { preferences.getInt("$DAY_PLAYED${monday.plusDays(it.toLong())}", 0) }
        return ProgressSummary(
            points = totalPoints(),
            solved = levels.count(Level::isSolved),
            totalPuzzles = levels.size,
            answered = answered.size,
            firstTryCorrect = answered.count { it.points == Scoring.CORRECT_POINTS },
            bestDailyStreak = DailyStreak.best(played),
            bestCorrectRun = preferences.getInt(BEST_CORRECT_RUN, 0),
            dailiesPlayed = played.size,
            sections = sections,
            lastSevenDays = week,
            playedThisWeek = playedThisWeek,
        )
    }

    override fun profile(): PlayerProfile {
        val levels = levels()
        val answered = levels.filter { it.points != null }
        val firstTry = answered.count { it.points == Scoring.CORRECT_POINTS }
        val accuracy = if (answered.isEmpty()) 0 else firstTry * 100 / answered.size
        return PlayerProfile("Moriarty", "Quiet Observer", levels.count(Level::isSolved), DailyStreak.current(dailyPlayedDays(), LocalDate.now()), totalPoints(), accuracy)
    }

    override fun resetProgress() {
        preferences.edit().clear().apply()
    }

    private fun solvedLevels(): Set<Int> = preferences.getStringSet(SOLVED_LEVELS, emptySet())
        .orEmpty()
        .mapNotNull(String::toIntOrNull)
        .toSet()

    private fun skippedLevels(): Set<Int> = preferences.getStringSet(SKIPPED_LEVELS, emptySet())
        .orEmpty()
        .mapNotNull(String::toIntOrNull)
        .toSet()

    private fun pointsKey(level: Int) = "level_points_$level"

    private companion object {
        const val SOLVED_LEVELS = "solved_levels"
        const val SKIPPED_LEVELS = "skipped_levels"
        const val DAILY_PLAYED = "daily_played_days"
        const val DAILY_START = "daily_start_date"
        const val CORRECT_RUN = "correct_run"
        const val BEST_CORRECT_RUN = "best_correct_run"
        const val DAY_POINTS = "day_points_"
        const val DAY_PLAYED = "day_played_"
    }
}
