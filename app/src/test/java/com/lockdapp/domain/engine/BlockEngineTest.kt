package com.lockdapp.domain.engine

import com.lockdapp.domain.model.AppGroup
import com.lockdapp.domain.model.LockSchedule
import com.lockdapp.domain.model.TimeWindow
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

class BlockEngineTest {

    // ── shared fixtures ────────────────────────────────────────────────────────
    private val instagram = "com.instagram.android"
    private val twitter   = "com.twitter.android"
    private val maps      = "com.google.android.apps.maps"

    private val socialGroup = AppGroup(
        id       = 1L,
        name     = "Redes sociales",
        packages = listOf(instagram, twitter),
    )

    // Mon–Fri 09:00–18:00 window
    private val workdayWindow = TimeWindow(startMinute = 540, endMinute = 1080)

    // ── Case 1: Indefinite recurring schedule (null dates) ─────────────────────
    @Test
    fun `indefinite recurring schedule blocks during window on matching day`() {
        val schedule = LockSchedule(
            id             = 1L,
            name           = "Trabajo",
            targetGroupIds = listOf(1L),
            windows        = listOf(workdayWindow),
            daysOfWeek     = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                                   DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
            validFrom      = null,
            validUntil     = null,
            enabled        = true,
        )
        val engine = BlockEngine(listOf(schedule), listOf(socialGroup))

        // Monday 10:00 — inside window
        val monday10 = LocalDateTime.of(2026, 6, 29, 10, 0)  // known Monday
        assertTrue(engine.isBlocked(instagram, monday10))
    }

    @Test
    fun `indefinite recurring schedule does NOT block on weekend`() {
        val schedule = LockSchedule(
            id             = 1L,
            name           = "Trabajo",
            targetGroupIds = listOf(1L),
            windows        = listOf(workdayWindow),
            daysOfWeek     = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                                   DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
            validFrom      = null,
            validUntil     = null,
            enabled        = true,
        )
        val engine = BlockEngine(listOf(schedule), listOf(socialGroup))

        // Saturday 10:00
        val saturday10 = LocalDateTime.of(2026, 6, 27, 10, 0)  // known Saturday
        assertFalse(engine.isBlocked(instagram, saturday10))
    }

    @Test
    fun `indefinite recurring schedule does NOT block apps outside the group`() {
        val schedule = LockSchedule(
            id             = 1L,
            name           = "Trabajo",
            targetGroupIds = listOf(1L),
            windows        = listOf(workdayWindow),
            daysOfWeek     = setOf(DayOfWeek.MONDAY),
            validFrom      = null,
            validUntil     = null,
            enabled        = true,
        )
        val engine = BlockEngine(listOf(schedule), listOf(socialGroup))

        val monday10 = LocalDateTime.of(2026, 6, 29, 10, 0)
        assertFalse(engine.isBlocked(maps, monday10))
    }

    @Test
    fun `disabled schedule never blocks`() {
        val schedule = LockSchedule(
            id             = 1L,
            name           = "Trabajo",
            targetGroupIds = listOf(1L),
            windows        = listOf(workdayWindow),
            daysOfWeek     = DayOfWeek.entries.toSet(),
            validFrom      = null,
            validUntil     = null,
            enabled        = false,
        )
        val engine = BlockEngine(listOf(schedule), listOf(socialGroup))

        val monday10 = LocalDateTime.of(2026, 6, 29, 10, 0)
        assertFalse(engine.isBlocked(instagram, monday10))
    }

    // ── Case 2: Monthly range (validFrom + validUntil) ─────────────────────────
    @Test
    fun `monthly schedule blocks within date range`() {
        val june = LockSchedule(
            id             = 2L,
            name           = "Junio sin redes",
            targetGroupIds = listOf(1L),
            windows        = listOf(TimeWindow(0, 1440)),  // all day: 00:00–24:00 (exclusive)
            daysOfWeek     = DayOfWeek.entries.toSet(),
            validFrom      = LocalDate.of(2026, 6, 1),
            validUntil     = LocalDate.of(2026, 6, 30),
            enabled        = true,
        )
        val engine = BlockEngine(listOf(june), listOf(socialGroup))

        val midJune = LocalDateTime.of(2026, 6, 15, 14, 0)
        assertTrue(engine.isBlocked(twitter, midJune))
    }

    @Test
    fun `monthly schedule does NOT block before validFrom`() {
        val june = LockSchedule(
            id             = 2L,
            name           = "Junio sin redes",
            targetGroupIds = listOf(1L),
            windows        = listOf(TimeWindow(0, 1440)),
            daysOfWeek     = DayOfWeek.entries.toSet(),
            validFrom      = LocalDate.of(2026, 6, 1),
            validUntil     = LocalDate.of(2026, 6, 30),
            enabled        = true,
        )
        val engine = BlockEngine(listOf(june), listOf(socialGroup))

        val mayLast = LocalDateTime.of(2026, 5, 31, 23, 59)
        assertFalse(engine.isBlocked(twitter, mayLast))
    }

    @Test
    fun `monthly schedule does NOT block after validUntil`() {
        val june = LockSchedule(
            id             = 2L,
            name           = "Junio sin redes",
            targetGroupIds = listOf(1L),
            windows        = listOf(TimeWindow(0, 1440)),
            daysOfWeek     = DayOfWeek.entries.toSet(),
            validFrom      = LocalDate.of(2026, 6, 1),
            validUntil     = LocalDate.of(2026, 6, 30),
            enabled        = true,
        )
        val engine = BlockEngine(listOf(june), listOf(socialGroup))

        val julyFirst = LocalDateTime.of(2026, 7, 1, 0, 0)
        assertFalse(engine.isBlocked(twitter, julyFirst))
    }

    // ── Case 3: Weekly range (validFrom + validUntil spanning weeks) ───────────
    @Test
    fun `weekly range schedule blocks during specified weeks`() {
        // Block the last two weeks of June (Mon–Fri only)
        val twoWeeks = LockSchedule(
            id             = 3L,
            name           = "Sprint final",
            targetGroupIds = listOf(1L),
            windows        = listOf(workdayWindow),
            daysOfWeek     = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                                   DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
            validFrom      = LocalDate.of(2026, 6, 15),
            validUntil     = LocalDate.of(2026, 6, 28),
            enabled        = true,
        )
        val engine = BlockEngine(listOf(twoWeeks), listOf(socialGroup))

        // Monday 22 June 10:00 — inside range and window
        val mon22 = LocalDateTime.of(2026, 6, 22, 10, 0)
        assertTrue(engine.isBlocked(instagram, mon22))
    }

    @Test
    fun `weekly range schedule does NOT block outside the specified weeks`() {
        val twoWeeks = LockSchedule(
            id             = 3L,
            name           = "Sprint final",
            targetGroupIds = listOf(1L),
            windows        = listOf(workdayWindow),
            daysOfWeek     = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                                   DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
            validFrom      = LocalDate.of(2026, 6, 15),
            validUntil     = LocalDate.of(2026, 6, 28),
            enabled        = true,
        )
        val engine = BlockEngine(listOf(twoWeeks), listOf(socialGroup))

        // Monday 29 June — one day after validUntil (2026-06-28)
        val mon29 = LocalDateTime.of(2026, 6, 29, 10, 0)
        assertFalse(engine.isBlocked(instagram, mon29))
    }

    // ── Window boundary exactness ──────────────────────────────────────────────
    @Test
    fun `window startMinute is inclusive`() {
        val schedule = LockSchedule(
            id             = 4L,
            name           = "Test",
            targetGroupIds = listOf(1L),
            windows        = listOf(TimeWindow(startMinute = 540, endMinute = 600)),
            daysOfWeek     = DayOfWeek.entries.toSet(),
            validFrom      = null,
            validUntil     = null,
            enabled        = true,
        )
        val engine = BlockEngine(listOf(schedule), listOf(socialGroup))

        // Exactly 09:00 = minute 540
        val at0900 = LocalDateTime.of(2026, 6, 29, 9, 0)
        assertTrue(engine.isBlocked(instagram, at0900))
    }

    @Test
    fun `window endMinute is exclusive`() {
        val schedule = LockSchedule(
            id             = 4L,
            name           = "Test",
            targetGroupIds = listOf(1L),
            windows        = listOf(TimeWindow(startMinute = 540, endMinute = 600)),
            daysOfWeek     = DayOfWeek.entries.toSet(),
            validFrom      = null,
            validUntil     = null,
            enabled        = true,
        )
        val engine = BlockEngine(listOf(schedule), listOf(socialGroup))

        // Exactly 10:00 = minute 600 — NOT inside [540, 600)
        val at1000 = LocalDateTime.of(2026, 6, 29, 10, 0)
        assertFalse(engine.isBlocked(instagram, at1000))
    }

    @Test
    fun `block is NOT active one minute before window opens`() {
        val schedule = LockSchedule(
            id             = 4L,
            name           = "Test",
            targetGroupIds = listOf(1L),
            windows        = listOf(TimeWindow(startMinute = 540, endMinute = 600)),
            daysOfWeek     = DayOfWeek.entries.toSet(),
            validFrom      = null,
            validUntil     = null,
            enabled        = true,
        )
        val engine = BlockEngine(listOf(schedule), listOf(socialGroup))

        // 08:59 = minute 539
        val at0859 = LocalDateTime.of(2026, 6, 29, 8, 59)
        assertFalse(engine.isBlocked(instagram, at0859))
    }

    @Test
    fun `multiple windows in same schedule — blocked in any matching window`() {
        val schedule = LockSchedule(
            id             = 5L,
            name           = "Partido partido",
            targetGroupIds = listOf(1L),
            windows        = listOf(
                TimeWindow(540, 720),   // 09:00–12:00
                TimeWindow(840, 1080),  // 14:00–18:00
            ),
            daysOfWeek     = DayOfWeek.entries.toSet(),
            validFrom      = null,
            validUntil     = null,
            enabled        = true,
        )
        val engine = BlockEngine(listOf(schedule), listOf(socialGroup))

        val at1500 = LocalDateTime.of(2026, 6, 29, 15, 0)
        assertTrue(engine.isBlocked(instagram, at1500))

        // 13:00 — between the two windows, not blocked
        val at1300 = LocalDateTime.of(2026, 6, 29, 13, 0)
        assertFalse(engine.isBlocked(instagram, at1300))
    }
}
