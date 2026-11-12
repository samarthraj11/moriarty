package com.moriartystudio.moriarty.domain.progress

import com.moriartystudio.moriarty.core.model.PuzzleCategory
import java.time.LocalDate

/** How one section is going: solved puzzles out of the total, and first-answer accuracy. */
data class SectionProgress(
    val category: PuzzleCategory,
    val total: Int,
    val solved: Int,
    val answered: Int,
    val firstTryCorrect: Int,
) {
    /** Share of first answers that were right, or null before the first answer. */
    val accuracy: Int? get() = if (answered == 0) null else firstTryCorrect * 100 / answered
}

/** Points earned and puzzles first-answered on one calendar day. */
data class DayActivity(val date: LocalDate, val points: Int, val played: Int)

/** Everything the Progress screen shows, computed from what the player has done on this device. */
data class ProgressSummary(
    val points: Int = 0,
    val solved: Int = 0,
    val totalPuzzles: Int = 0,
    val answered: Int = 0,
    val firstTryCorrect: Int = 0,
    val bestDailyStreak: Int = 0,
    val bestCorrectRun: Int = 0,
    val dailiesPlayed: Int = 0,
    val sections: List<SectionProgress> = emptyList(),
    /** The last seven days, oldest first, ending today. */
    val lastSevenDays: List<DayActivity> = emptyList(),
    val playedThisWeek: Int = 0,
) {
    val accuracy: Int get() = if (answered == 0) 0 else firstTryCorrect * 100 / answered

    /** The section with the lowest accuracy, once at least two sections have been tried. */
    val weakestSection: SectionProgress?
        get() = sections.filter { it.accuracy != null }.takeIf { it.size >= 2 }?.minByOrNull { it.accuracy!! }

    companion object {
        const val WEEKLY_GOAL = 10
    }
}
