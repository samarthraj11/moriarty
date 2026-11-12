package com.moriartystudio.moriarty.feature.daily.presentation

import com.moriartystudio.moriarty.domain.daily.DailyStreak
import com.moriartystudio.moriarty.core.model.PuzzleCategory
import java.time.LocalDate
import java.time.YearMonth

data class DailyState(
    val today: LocalDate = LocalDate.now(),
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val playedDays: Set<LocalDate> = emptySet(),
    /** The first day the daily screen was opened; the lapsed notice only appears from here, so new players aren't nagged. */
    val startDate: LocalDate = LocalDate.now(),
    val puzzleLevel: Int = 1,
    val category: PuzzleCategory = PuzzleCategory.Pattern,
) {
    val month: YearMonth get() = YearMonth.from(today)
    val playedToday: Boolean get() = today in playedDays

    val playedThisMonth: Int get() = playedDays.count { YearMonth.from(it) == month }

    /** Every earlier day of this month without a daily played. */
    val lapsedThisMonth: Int get() = generateSequence(month.atDay(1)) { it.plusDays(1) }.takeWhile { it < today }.count { it !in playedDays }

    /** Yesterday was missed and today isn't played yet, so the streak has just lapsed. */
    val lapsedYesterday: Boolean get() {
        val yesterday = today.minusDays(1)
        return !playedToday && yesterday >= startDate && yesterday !in playedDays
    }

    /** Length of the streak that ended with that miss (0 if there wasn't one). */
    val endedStreak: Int get() = DailyStreak.current(playedDays, today.minusDays(1))
}
