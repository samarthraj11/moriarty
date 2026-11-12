package com.moriartystudio.moriarty.domain.progress

import com.moriartystudio.moriarty.core.model.PuzzleCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressRulesTest {
    @Test fun `rank follows the thresholds`() {
        assertEquals(DetectiveRank.Rookie, Ranks.rankFor(0))
        assertEquals(DetectiveRank.Rookie, Ranks.rankFor(59))
        assertEquals(DetectiveRank.Sleuth, Ranks.rankFor(60))
        assertEquals(DetectiveRank.Moriarty, Ranks.rankFor(900))
        assertEquals(DetectiveRank.Rookie, Ranks.rankFor(-20))
    }

    @Test fun `progress reports points to the next rank`() {
        val p = Ranks.progress(140)
        assertEquals(DetectiveRank.Sleuth, p.rank)
        assertEquals(DetectiveRank.Inspector, p.next)
        assertEquals(10, p.pointsToNext)
        val top = Ranks.progress(500)
        assertNull(top.next)
        assertEquals(0, top.pointsToNext)
        assertEquals(1f, top.ladderFraction, 0f)
    }

    @Test fun `badges unlock from the summary`() {
        val none = Badges.unlocked(ProgressSummary())
        assertTrue(none.isEmpty())
        val s = ProgressSummary(
            points = 120, solved = 12, answered = 12, firstTryCorrect = 12, bestCorrectRun = 6, bestDailyStreak = 3,
            sections = listOf(SectionProgress(PuzzleCategory.Spatial, total = 3, solved = 3, answered = 3, firstTryCorrect = 3)),
        )
        val got = Badges.unlocked(s)
        assertTrue(Badge.FirstCase in got && Badge.SharpEye in got && Badge.Regular in got && Badge.Century in got)
        assertTrue(Badge.SpatialMaster in got && Badge.Flawless in got)
        assertFalse(Badge.OnARoll in got || Badge.Devoted in got || Badge.PatternMaster in got)
    }

    @Test fun `weakest section needs two tried sections`() {
        fun sec(c: PuzzleCategory, right: Int, of: Int) = SectionProgress(c, 10, of, of, right)
        assertNull(ProgressSummary(sections = listOf(sec(PuzzleCategory.Logic, 1, 2))).weakestSection)
        val s = ProgressSummary(sections = listOf(sec(PuzzleCategory.Logic, 4, 4), sec(PuzzleCategory.Deduction, 1, 4), sec(PuzzleCategory.Pattern, 0, 0)))
        assertEquals(PuzzleCategory.Deduction, s.weakestSection?.category)
    }
}
