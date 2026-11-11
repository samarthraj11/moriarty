package com.moriartystudio.moriarty.domain.daily

import java.time.Duration
import java.time.ZonedDateTime

/** The daily challenge resets at local midnight. */
object DailyClock {
    fun secondsUntilReset(now: ZonedDateTime): Long {
        val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(now.zone)
        return Duration.between(now, nextMidnight).seconds
    }

    fun format(seconds: Long): String = "%02d:%02d:%02d".format(seconds / 3600, seconds % 3600 / 60, seconds % 60)
}
