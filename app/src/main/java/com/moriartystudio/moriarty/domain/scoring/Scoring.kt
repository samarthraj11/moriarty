package com.moriartystudio.moriarty.domain.scoring

/**
 * Only the first answer on a puzzle scores: a right answer earns [CORRECT_POINTS], a wrong one
 * costs [WRONG_PENALTY]. Every answer after that, right or wrong, neither earns nor costs anything.
 */
object Scoring {
    const val CORRECT_POINTS = 10
    const val WRONG_PENALTY = 5

    fun pointsForFirstAnswer(correct: Boolean): Int = if (correct) CORRECT_POINTS else -WRONG_PENALTY
}
