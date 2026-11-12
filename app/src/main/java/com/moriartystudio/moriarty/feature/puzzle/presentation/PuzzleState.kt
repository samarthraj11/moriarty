package com.moriartystudio.moriarty.feature.puzzle.presentation

import com.moriartystudio.moriarty.core.model.Puzzle

enum class AnswerStatus { Unanswered, Incorrect, Correct, Skipped }

data class PuzzleState(
    val puzzle: Puzzle? = null,
    val correctAnswer: String? = null,
    val wrongOptions: Set<String> = emptySet(),
    val status: AnswerStatus = AnswerStatus.Unanswered,
    val elapsedSeconds: Int = 0,
    val showHint: Boolean = false,
    val totalPoints: Int = 0,
    /** Playing today's daily puzzle: solving it counts the day and "Done" returns to the calendar. */
    val isDaily: Boolean = false,
    /** Position of this puzzle within its category, shown as "Level N". */
    val categoryLevel: Int = 1,
    /** Points change from the answer just given: 10, -5, or 0 when the puzzle was already scored. */
    val lastDelta: Int? = null,
)
