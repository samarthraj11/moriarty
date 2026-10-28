package com.moriartystudio.moriarty.domain.progression

import com.moriartystudio.moriarty.core.model.Difficulty
import com.moriartystudio.moriarty.core.model.Level
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressionRulesTest {
    @Test
    fun `fresh install unlocks only level one`() {
        assertTrue(ProgressionRules.isUnlocked(1, emptySet()))
        assertFalse(ProgressionRules.isUnlocked(2, emptySet()))
        assertFalse(ProgressionRules.isUnlocked(10, emptySet()))
    }

    @Test
    fun `solving a level unlocks exactly the next level`() {
        val solved = setOf(1, 2)
        assertTrue(ProgressionRules.isUnlocked(2, solved))
        assertTrue(ProgressionRules.isUnlocked(3, solved))
        assertFalse(ProgressionRules.isUnlocked(4, solved))
    }

    @Test
    fun `current level is first unlocked unsolved level`() {
        val levels = (1..6).map { number ->
            Level(
                number = number,
                title = "Level $number",
                category = PuzzleCategory.Pattern,
                difficulty = Difficulty.Easy,
                isSolved = number <= 2,
                isUnlocked = number <= 3,
            )
        }

        assertEquals(3, ProgressionRules.currentLevel(levels)?.number)
        assertEquals(listOf(1, 2, 3, 4, 5), ProgressionRules.nearbyLevels(levels, levels[2]).map(Level::number))
    }
}
