package com.moriartystudio.moriarty.domain.daily

import java.time.LocalDate

object DailyStreak {
    /** Consecutive played days ending today, or ending yesterday if today isn't played yet. */
    fun current(played: Set<LocalDate>, today: LocalDate): Int {
        var day = if (today in played) today else today.minusDays(1)
        var count = 0
        while (day in played) {
            count++
            day = day.minusDays(1)
        }
        return count
    }

    /** Longest run of consecutive played days. */
    fun best(played: Set<LocalDate>): Int {
        var best = 0
        var run = 0
        var previous: LocalDate? = null
        for (day in played.sorted()) {
            run = if (previous != null && previous.plusDays(1) == day) run + 1 else 1
            best = maxOf(best, run)
            previous = day
        }
        return best
    }
}

object DailyPuzzlePicker {
    /** The same puzzle for everyone on a given date, cycling through the catalogue day by day. */
    fun indexFor(date: LocalDate, puzzleCount: Int): Int = Math.floorMod(date.toEpochDay(), puzzleCount.toLong()).toInt()
}
