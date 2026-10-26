package com.moriartystudio.moriarty.data.repository

import com.moriartystudio.moriarty.core.model.*
import com.moriartystudio.moriarty.domain.repository.GameRepository

class LocalGameRepository : GameRepository {
    override fun levels(): List<Level> = listOf(
        Level(1, "Mirror time", PuzzleCategory.Reflection, Difficulty.Easy, true, true, 42),
        Level(2, "Missing tile", PuzzleCategory.Sequence, Difficulty.Easy, true, true, 36),
        Level(3, "Turn the shape", PuzzleCategory.Rotation, Difficulty.Easy, true, true, 51),
        Level(4, "The odd shadow", PuzzleCategory.Shadow, Difficulty.Easy, true, true, 47),
        Level(5, "Balance the symbols", PuzzleCategory.Deduction, Difficulty.Easy, true, true, 58),
        Level(6, "Reflected route", PuzzleCategory.Reflection, Difficulty.Easy, true, true, 63),
        Level(7, "Behind the glass", PuzzleCategory.Reflection, Difficulty.Easy, false, true),
        Level(8, "Silent sequence", PuzzleCategory.Sequence, Difficulty.Medium, false, false),
        Level(9, "Three rotations", PuzzleCategory.Rotation, Difficulty.Medium, false, false),
        Level(10, "Light and form", PuzzleCategory.Shadow, Difficulty.Medium, false, false),
        Level(11, "The missing rule", PuzzleCategory.Deduction, Difficulty.Hard, false, false),
        Level(12, "Double reflection", PuzzleCategory.Reflection, Difficulty.Hard, false, false),
    )

    override fun puzzle(level: Int): Puzzle = Puzzle(
        level = level,
        title = if (level == 1) "Mirror time" else "Behind the glass",
        prompt = "What time is the clock actually showing?",
        category = PuzzleCategory.Reflection,
        difficulty = if (level <= 7) Difficulty.Easy else Difficulty.Medium,
        options = listOf("03:40", "04:20", "08:20", "09:40"),
        correctOption = "03:40",
        hint = "A mirror reverses left and right. Imagine the hands on the opposite side of 12:00.",
        explanation = "For an analogue clock in a mirror, subtract the mirrored time from 11:60. 11:60 − 08:20 gives 03:40.",
    )

    override fun leaderboard(): List<LeaderboardEntry> = listOf(
        LeaderboardEntry(1, "Anika", 1, 24), LeaderboardEntry(2, "Ravi", 1, 29),
        LeaderboardEntry(3, "Mira", 1, 31), LeaderboardEntry(4, "Leo", 1, 33),
        LeaderboardEntry(5, "Sofia", 1, 34), LeaderboardEntry(6, "Kai", 1, 35),
        LeaderboardEntry(7, "Noah", 1, 36), LeaderboardEntry(8, "Priya", 1, 37),
        LeaderboardEntry(428, "You", 1, 104, true),
    )

    override fun profile() = PlayerProfile("Samarth", "@samarth", 46, 4, 44, 88)
}
