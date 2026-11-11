package com.moriartystudio.moriarty.domain.daily

import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class DailyClockTest {
    private val utc = ZoneId.of("UTC")

    @Test
    fun `one second before midnight leaves one second`() {
        assertEquals(1L, DailyClock.secondsUntilReset(ZonedDateTime.of(2026, 11, 11, 23, 59, 59, 0, utc)))
    }

    @Test
    fun `just after midnight leaves a full day`() {
        assertEquals(86_400L, DailyClock.secondsUntilReset(ZonedDateTime.of(2026, 11, 12, 0, 0, 0, 0, utc)))
    }

    @Test
    fun `counts down to local midnight across a daylight saving change`() {
        val newYork = ZoneId.of("America/New_York")
        // Clocks go back on 2026-11-01, so that day has 25 hours: one hour after 00:00 there are 24 left.
        assertEquals(24L * 3600, DailyClock.secondsUntilReset(ZonedDateTime.of(2026, 11, 1, 0, 0, 0, 0, newYork).plusHours(1)))
    }

    @Test
    fun `formats as hours minutes seconds`() {
        assertEquals("08:24:07", DailyClock.format(8 * 3600L + 24 * 60 + 7))
    }
}
