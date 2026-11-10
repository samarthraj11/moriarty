package com.moriartystudio.moriarty.domain.scoring

import org.junit.Assert.assertEquals
import org.junit.Test

class ScoringTest {
    @Test
    fun firstCorrectAnswerEarnsTenPoints() {
        assertEquals(10, Scoring.pointsForFirstAnswer(correct = true))
    }

    @Test
    fun firstWrongAnswerCostsThePenalty() {
        assertEquals(-Scoring.WRONG_PENALTY, Scoring.pointsForFirstAnswer(correct = false))
    }
}
