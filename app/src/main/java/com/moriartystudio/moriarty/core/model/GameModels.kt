package com.moriartystudio.moriarty.core.model

enum class PuzzleCategory(val label: String) {
    Pattern("Patterns"),
    Spatial("Spatial"),
    Deduction("Deduction"),
    Logic("Logic"),
}

enum class Difficulty { Easy, Medium, Hard }

enum class ShapeKind { Circle, Triangle, Square, Diamond, Arrow }

data class VisualToken(
    val shape: ShapeKind,
    val rotationDegrees: Float = 0f,
    val accentIndex: Int = 0,
)

sealed interface PuzzleAnswerVisual {
    data class Token(val token: VisualToken) : PuzzleAnswerVisual
    data class TokenGroup(val tokens: List<VisualToken>) : PuzzleAnswerVisual
}

sealed interface PuzzleContent {
    data class ShapeSequence(val tokens: List<VisualToken?>) : PuzzleContent
    data class NumberSequence(val values: List<String>) : PuzzleContent
    data class Matrix(val rows: Int, val columns: Int, val tokens: List<VisualToken?>) : PuzzleContent
    data class RotationSequence(val rotations: List<Float?>) : PuzzleContent
    data class ClueList(val clues: List<String>) : PuzzleContent
    data class Equations(val rows: List<String>) : PuzzleContent
    data class OddOneOut(val groups: List<List<VisualToken>>) : PuzzleContent
    data class RouteGrid(
        val rows: Int,
        val columns: Int,
        val blockedCells: Set<Int>,
        val startCell: Int,
        val endCell: Int,
    ) : PuzzleContent

    data class Statements(val statements: List<String>) : PuzzleContent
}

data class Level(
    val number: Int,
    val title: String,
    val category: PuzzleCategory,
    val difficulty: Difficulty,
    val isSolved: Boolean,
    val isUnlocked: Boolean,
    val points: Int? = null,
    val difficultyRating: Int = 1,
    val isSkipped: Boolean = false,
    /** 1-based position among the levels of the same category; what players see as "Level N". */
    val categoryLevel: Int = 1,
)

data class Puzzle(
    val level: Int,
    val title: String,
    val prompt: String,
    val category: PuzzleCategory,
    val difficulty: Difficulty,
    val content: PuzzleContent,
    val options: List<String>,
    val correctOption: String,
    val hint: String,
    val explanation: String,
    val optionVisuals: Map<String, PuzzleAnswerVisual> = emptyMap(),
    val difficultyRating: Int = 1,
)

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val solved: Int,
    val points: Int,
    val isCurrentUser: Boolean = false,
)

data class PlayerProfile(
    val name: String,
    val handle: String,
    val solved: Int,
    val streak: Int,
    val points: Int,
    val accuracy: Int,
)
