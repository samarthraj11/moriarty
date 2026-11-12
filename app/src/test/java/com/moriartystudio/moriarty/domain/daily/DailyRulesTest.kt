package com.moriartystudio.moriarty.domain.daily

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class DailyRulesTest {
    private val today = LocalDate.of(2026, 11, 11)
    private fun days(vararg d: Int) = d.map { LocalDate.of(2026, 11, it) }.toSet()

    @Test
    fun `streak counts back from today when today is played`() {
        assertEquals(3, DailyStreak.current(days(9, 10, 11), today))
    }

    @Test
    fun `streak survives until today is over`() {
        assertEquals(2, DailyStreak.current(days(9, 10), today))
    }

    @Test
    fun `a missed day breaks the streak`() {
        assertEquals(0, DailyStreak.current(days(8, 9), today))
        assertEquals(1, DailyStreak.current(days(8, 9, 11), today))
    }

    @Test
    fun `best streak is the longest run`() {
        assertEquals(3, DailyStreak.best(days(1, 2, 3, 5, 6, 9)))
        assertEquals(0, DailyStreak.best(emptySet()))
    }

    @Test
    fun `daily puzzle is stable for a date and changes the next day`() {
        val a = DailyPuzzlePicker.indexFor(today, 50)
        assertEquals(a, DailyPuzzlePicker.indexFor(today, 50))
        assertNotEquals(a, DailyPuzzlePicker.indexFor(today.plusDays(1), 50))
        assertEquals(0, DailyPuzzlePicker.indexFor(LocalDate.ofEpochDay(100), 50))
    }
}
