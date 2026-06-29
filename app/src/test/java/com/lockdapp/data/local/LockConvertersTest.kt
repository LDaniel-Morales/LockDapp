package com.lockdapp.data.local

import com.lockdapp.domain.model.TimeWindow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class LockConvertersTest {

    private val c = LockConverters()

    @Test
    fun `List of String round-trips`() {
        val original = listOf("com.instagram.android", "com.twitter.android", "com.google.android.apps.maps")
        assertEquals(original, c.jsonToListString(c.listStringToJson(original)))
    }

    @Test
    fun `empty List of String round-trips`() {
        val empty = emptyList<String>()
        assertEquals(empty, c.jsonToListString(c.listStringToJson(empty)))
    }

    @Test
    fun `List of Long round-trips`() {
        val ids = listOf(1L, 42L, 999L)
        assertEquals(ids, c.jsonToListLong(c.listLongToJson(ids)))
    }

    @Test
    fun `List of TimeWindow round-trips`() {
        val windows = listOf(TimeWindow(540, 720), TimeWindow(840, 1080))
        assertEquals(windows, c.jsonToListWindow(c.listWindowToJson(windows)))
    }

    @Test
    fun `single TimeWindow round-trips`() {
        val w = listOf(TimeWindow(0, 1440))
        assertEquals(w, c.jsonToListWindow(c.listWindowToJson(w)))
    }

    @Test
    fun `Set of DayOfWeek round-trips — full week`() {
        val days = DayOfWeek.entries.toSet()
        assertEquals(days, c.jsonToSetDayOfWeek(c.setDayOfWeekToJson(days)))
    }

    @Test
    fun `Set of DayOfWeek round-trips — weekdays only`() {
        val days = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                         DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
        assertEquals(days, c.jsonToSetDayOfWeek(c.setDayOfWeekToJson(days)))
    }

    @Test
    fun `LocalDate non-null round-trips`() {
        val date = LocalDate.of(2026, 6, 28)
        assertEquals(date, c.stringToLocalDate(c.localDateToString(date)))
    }

    @Test
    fun `LocalDate null round-trips`() {
        assertNull(c.stringToLocalDate(c.localDateToString(null)))
    }
}
