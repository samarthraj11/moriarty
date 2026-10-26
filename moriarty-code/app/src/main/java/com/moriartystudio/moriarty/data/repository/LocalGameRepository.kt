package com.moriartystudio.moriarty.data.repository

import android.content.Context
import com.moriartystudio.moriarty.core.model.Difficulty
import com.moriartystudio.moriarty.core.model.LeaderboardEntry
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PlayerProfile
import com.moriartystudio.moriarty.core.model.Puzzle
import com.moriartystudio.moriarty.core.model.PuzzleAnswerVisual
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import com.moriartystudio.moriarty.core.model.PuzzleContent
import com.moriartystudio.moriarty.core.model.ShapeKind
import com.moriartystudio.moriarty.core.model.VisualToken
import com.moriartystudio.moriarty.domain.repository.GameRepository
import com.moriartystudio.moriarty.domain.progression.ProgressionRules

class LocalGameRepository(context: Context) : GameRepository {
    private val preferences = context.getSharedPreferences("moriarty_progress", Context.MODE_PRIVATE)

    private val puzzles = listOf(
        Puzzle(
            level = 1,
            title = "Alternating shapes",
            prompt = "Which shape comes next?",
            category = PuzzleCategory.Pattern,
            difficulty = Difficulty.Easy,
            content = PuzzleContent.ShapeSequence(listOf(token(ShapeKind.Circle), token(ShapeKind.Triangle, accent = 1), token(ShapeKind.Circle), token(ShapeKind.Triangle, accent = 1), null)),
            options = listOf("Circle", "Triangle", "Square", "Diamond"),
            correctOption = "Circle",
            hint = "Compare positions one, three and five.",
            explanation = "The sequence alternates circle, triangle, circle, triangle. The next item is a circle.",
            optionVisuals = shapeOptions(),
        ),
        Puzzle(
            2, "Growing numbers", "What number completes the sequence?", PuzzleCategory.Pattern, Difficulty.Easy,
            PuzzleContent.NumberSequence(listOf("2", "4", "8", "16", "?")),
            listOf("18", "24", "30", "32"), "32",
            "Each number is created from the number immediately before it.",
            "Every value doubles: 2, 4, 8, 16, then 32.",
        ),
        Puzzle(
            3, "Missing grid", "Which shape belongs in the empty cell?", PuzzleCategory.Pattern, Difficulty.Easy,
            PuzzleContent.Matrix(3, 3, listOf(
                token(ShapeKind.Circle), token(ShapeKind.Triangle, accent = 1), token(ShapeKind.Square, accent = 2),
                token(ShapeKind.Triangle, accent = 1), token(ShapeKind.Square, accent = 2), token(ShapeKind.Circle),
                token(ShapeKind.Square, accent = 2), token(ShapeKind.Circle), null,
            )),
            listOf("Circle", "Triangle", "Square", "Diamond"), "Triangle",
            "Each row contains one of every shape.",
            "Every row and column contains one circle, one triangle and one square. The missing shape is a triangle.",
            optionVisuals = shapeOptions(),
        ),
        Puzzle(
            4, "Quarter turn", "Which rotation comes next?", PuzzleCategory.Spatial, Difficulty.Medium,
            PuzzleContent.RotationSequence(listOf(0f, 90f, 180f, null)),
            listOf("0°", "90°", "180°", "270°"), "270°",
            "The arrow turns by the same amount each time.",
            "The arrow rotates 90 degrees clockwise at every step, so the fourth position is 270 degrees.",
            optionVisuals = mapOf(
                "0°" to arrowOption(0f),
                "90°" to arrowOption(90f),
                "180°" to arrowOption(180f),
                "270°" to arrowOption(270f),
            ),
        ),
        Puzzle(
            5, "First in line", "Who must be first?", PuzzleCategory.Deduction, Difficulty.Medium,
            PuzzleContent.ClueList(listOf("Leo is before Kai.", "Kai is before Mira.", "Noor is after Mira.")),
            listOf("Leo", "Kai", "Mira", "Noor"), "Leo",
            "Connect the clues into one ordered chain.",
            "The clues form Leo → Kai → Mira → Noor. Leo must be first.",
        ),
        Puzzle(
            6, "Shape values", "What is the value of the triangle?", PuzzleCategory.Deduction, Difficulty.Medium,
            PuzzleContent.Equations(listOf("● + ● = 8", "● + ▲ = 11", "▲ = ?")),
            listOf("3", "4", "7", "8"), "7",
            "Find the circle value before solving for the triangle.",
            "Two circles equal 8, so each circle is 4. Therefore the triangle is 11 − 4 = 7.",
        ),
        Puzzle(
            7, "Broken group", "Which group breaks the rule?", PuzzleCategory.Pattern, Difficulty.Medium,
            PuzzleContent.OddOneOut(listOf(
                listOf(token(ShapeKind.Circle), token(ShapeKind.Triangle, accent = 1), token(ShapeKind.Square, accent = 2)),
                listOf(token(ShapeKind.Triangle, accent = 1), token(ShapeKind.Square, accent = 2), token(ShapeKind.Circle)),
                listOf(token(ShapeKind.Square, accent = 2), token(ShapeKind.Circle), token(ShapeKind.Triangle, accent = 1)),
                listOf(token(ShapeKind.Circle), token(ShapeKind.Square, accent = 2), token(ShapeKind.Square, accent = 2)),
            )),
            listOf("A", "B", "C", "D"), "D",
            "Count how many times each kind of shape appears in a group.",
            "Groups A, B and C each contain one circle, one triangle and one square. Group D repeats the square.",
            optionVisuals = mapOf(
                "A" to groupOption(ShapeKind.Circle, ShapeKind.Triangle, ShapeKind.Square),
                "B" to groupOption(ShapeKind.Triangle, ShapeKind.Square, ShapeKind.Circle),
                "C" to groupOption(ShapeKind.Square, ShapeKind.Circle, ShapeKind.Triangle),
                "D" to groupOption(ShapeKind.Circle, ShapeKind.Square, ShapeKind.Square),
            ),
        ),
        Puzzle(
            8, "Shortest route", "What is the fewest number of moves to reach the goal?", PuzzleCategory.Spatial, Difficulty.Hard,
            PuzzleContent.RouteGrid(4, 4, setOf(5, 6, 9), 12, 3),
            listOf("5", "6", "7", "8"), "6",
            "A move can go one cell up, down, left or right.",
            "The shortest open route travels three cells up and three cells right, for 6 moves.",
        ),
        Puzzle(
            9, "Only one truth", "Exactly one statement is true. Which one?", PuzzleCategory.Logic, Difficulty.Hard,
            PuzzleContent.Statements(listOf("A says: The answer is C.", "B says: The answer is not A.", "C says: B is false.")),
            listOf("A", "B", "C", "Impossible"), "B",
            "Test each option and count how many statements become true.",
            "If B is the answer, A is false, B is true and C is false. That is the only case with exactly one truth.",
        ),
        Puzzle(
            10, "Turning matrix", "Which direction completes the matrix?", PuzzleCategory.Logic, Difficulty.Hard,
            PuzzleContent.Matrix(3, 3, listOf(
                token(ShapeKind.Arrow, 0f), token(ShapeKind.Arrow, 90f), token(ShapeKind.Arrow, 180f),
                token(ShapeKind.Arrow, 90f), token(ShapeKind.Arrow, 180f), token(ShapeKind.Arrow, 270f),
                token(ShapeKind.Arrow, 180f), token(ShapeKind.Arrow, 270f), null,
            )),
            listOf("Up", "Right", "Down", "Left"), "Up",
            "Moving right or down applies the same quarter turn.",
            "Every step right or down rotates the arrow 90 degrees clockwise. The missing arrow points up.",
            optionVisuals = mapOf(
                "Up" to arrowOption(0f),
                "Right" to arrowOption(90f),
                "Down" to arrowOption(180f),
                "Left" to arrowOption(270f),
            ),
        ),
    )

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

        fun token(shape: ShapeKind, rotation: Float = 0f, accent: Int = 0) = VisualToken(shape, rotation, accent)

        fun shapeOptions() = mapOf(
            "Circle" to PuzzleAnswerVisual.Token(token(ShapeKind.Circle)),
            "Triangle" to PuzzleAnswerVisual.Token(token(ShapeKind.Triangle, accent = 1)),
            "Square" to PuzzleAnswerVisual.Token(token(ShapeKind.Square, accent = 2)),
            "Diamond" to PuzzleAnswerVisual.Token(token(ShapeKind.Diamond, accent = 3)),
        )

        fun arrowOption(rotation: Float) = PuzzleAnswerVisual.Token(token(ShapeKind.Arrow, rotation))

        fun groupOption(vararg shapes: ShapeKind) = PuzzleAnswerVisual.TokenGroup(
            shapes.mapIndexed { index, shape -> token(shape, accent = index) },
        )
    }
}
