package com.moriartystudio.moriarty.core.model

enum class PuzzleCategory(val label: String) { Reflection("Reflection"), Sequence("Sequence"), Rotation("Rotation"), Deduction("Deduction"), Shadow("Shadow") }
enum class Difficulty { Easy, Medium, Hard }

data class Level(
    val number: Int,
    val title: String,
    val category: PuzzleCategory,
    val difficulty: Difficulty,
    val isSolved: Boolean,
    val isUnlocked: Boolean,
    val bestTimeSeconds: Int? = null,
)

data class Puzzle(
    val level: Int,
    val title: String,
    val prompt: String,
    val category: PuzzleCategory,
    val difficulty: Difficulty,
    val options: List<String>,
    val correctOption: String,
    val hint: String,
    val explanation: String,
)

data class LeaderboardEntry(val rank: Int, val name: String, val solved: Int, val totalSeconds: Int, val isCurrentUser: Boolean = false)
data class PlayerProfile(val name: String, val handle: String, val solved: Int, val streak: Int, val averageSeconds: Int, val accuracy: Int)
