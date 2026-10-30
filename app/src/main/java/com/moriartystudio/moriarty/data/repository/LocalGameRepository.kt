package com.moriartystudio.moriarty.data.repository

import android.content.Context
import com.moriartystudio.moriarty.core.model.LeaderboardEntry
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PlayerProfile
import com.moriartystudio.moriarty.core.model.Puzzle
import com.moriartystudio.moriarty.data.local.AssetPuzzleLocalDataSource
import com.moriartystudio.moriarty.data.local.PuzzleLocalDataSource
import com.moriartystudio.moriarty.domain.progression.ProgressionRules
import com.moriartystudio.moriarty.domain.repository.GameRepository

class LocalGameRepository(
    context: Context,
    puzzleDataSource: PuzzleLocalDataSource = AssetPuzzleLocalDataSource(context.assets),
) : GameRepository {
    private val preferences = context.getSharedPreferences("moriarty_progress", Context.MODE_PRIVATE)
    private val puzzles = puzzleDataSource.puzzles()

    override fun levels(): List<Level> {
        val solved = solvedLevels()
        return puzzles.map { puzzle ->
            Level(
                number = puzzle.level,
                title = puzzle.title,
                category = puzzle.category,
                difficulty = puzzle.difficulty,
                isSolved = puzzle.level in solved,
                isUnlocked = ProgressionRules.isUnlocked(puzzle.level, solved),
                bestTimeSeconds = preferences.getInt(bestTimeKey(puzzle.level), -1).takeIf { it >= 0 },
            )
        }
    }

    override fun puzzle(level: Int): Puzzle = puzzles.firstOrNull { it.level == level } ?: puzzles.first()

    override fun completeLevel(level: Int, rankedSeconds: Int) {
        val solved = solvedLevels() + level
        val previousBest = preferences.getInt(bestTimeKey(level), Int.MAX_VALUE)
        preferences.edit()
            .putStringSet(SOLVED_LEVELS, solved.map(Int::toString).toSet())
            .putInt(bestTimeKey(level), minOf(previousBest, rankedSeconds))
            .apply()
    }

    override fun leaderboard(): List<LeaderboardEntry> = listOf(
        LeaderboardEntry(1, "Anika", 1, 24), LeaderboardEntry(2, "Ravi", 1, 29),
        LeaderboardEntry(3, "Mira", 1, 31), LeaderboardEntry(4, "Leo", 1, 33),
        LeaderboardEntry(5, "Sofia", 1, 34), LeaderboardEntry(6, "Kai", 1, 35),
        LeaderboardEntry(7, "Noah", 1, 36), LeaderboardEntry(8, "Priya", 1, 37),
        LeaderboardEntry(428, "You", 1, 104, true),
    )

    override fun profile(): PlayerProfile {
        val levels = levels()
        val solved = levels.filter(Level::isSolved)
        val average = solved.mapNotNull(Level::bestTimeSeconds).average().takeIf { !it.isNaN() }?.toInt() ?: 0
        return PlayerProfile("Moriarty", "Quiet Observer", solved.size, 0, average, if (solved.isEmpty()) 0 else 100)
    }

    private fun solvedLevels(): Set<Int> = preferences.getStringSet(SOLVED_LEVELS, emptySet())
        .orEmpty()
        .mapNotNull(String::toIntOrNull)
        .toSet()

    private fun bestTimeKey(level: Int) = "best_time_$level"

    private companion object {
        const val SOLVED_LEVELS = "solved_levels"
    }
}
