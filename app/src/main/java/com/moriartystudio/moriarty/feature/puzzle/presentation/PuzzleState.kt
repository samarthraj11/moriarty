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
    val hintUsed: Boolean = false,
)
