package com.moriartystudio.moriarty.data.repository

import java.time.LocalDate
import com.moriartystudio.moriarty.domain.daily.DailyStreak
import com.moriartystudio.moriarty.domain.daily.DailyPuzzlePicker
import android.content.Context
import com.moriartystudio.moriarty.core.model.LeaderboardEntry
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PlayerProfile
import com.moriartystudio.moriarty.core.model.Puzzle
import com.moriartystudio.moriarty.data.local.AssetPuzzleLocalDataSource
import com.moriartystudio.moriarty.data.local.PuzzleLocalDataSource
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
        preferences.edit().putInt(pointsKey(level), points).apply()
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

    override fun leaderboard(): List<LeaderboardEntry> = listOf(
        LeaderboardEntry(1, "Anika", 50, 500), LeaderboardEntry(2, "Ravi", 48, 480),
        LeaderboardEntry(3, "Mira", 47, 465), LeaderboardEntry(4, "Leo", 45, 440),
        LeaderboardEntry(5, "Sofia", 44, 430), LeaderboardEntry(6, "Kai", 42, 410),
        LeaderboardEntry(7, "Noah", 40, 395), LeaderboardEntry(8, "Priya", 38, 370),
        LeaderboardEntry(428, "You", levels().count(Level::isSolved), totalPoints(), true),
    )

    override fun profile(): PlayerProfile {
        val levels = levels()
        val answered = levels.filter { it.points != null }
        val firstTry = answered.count { it.points == Scoring.CORRECT_POINTS }
        val accuracy = if (answered.isEmpty()) 0 else firstTry * 100 / answered.size
        return PlayerProfile("Moriarty", "Quiet Observer", levels.count(Level::isSolved), DailyStreak.current(dailyPlayedDays(), LocalDate.now()), totalPoints(), accuracy)
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
    }
}
