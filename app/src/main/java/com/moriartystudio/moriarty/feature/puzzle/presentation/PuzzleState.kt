package com.moriartystudio.moriarty.feature.puzzle.presentation

import com.moriartystudio.moriarty.core.model.Puzzle

enum class AnswerStatus { Unanswered, Selected, Incorrect, Correct }

data class PuzzleState(
    val puzzle: Puzzle? = null,
    val selectedAnswer: String? = null,
    val status: AnswerStatus = AnswerStatus.Unanswered,
    val elapsedSeconds: Int = 0,
    val showHint: Boolean = false,
    val hintUsed: Boolean = false,
    val wrongAnswers: Int = 0,
)
