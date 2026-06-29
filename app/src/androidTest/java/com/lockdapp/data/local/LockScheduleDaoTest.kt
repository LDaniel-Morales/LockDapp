package com.lockdapp.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lockdapp.domain.model.TimeWindow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.DayOfWeek
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class LockScheduleDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: LockScheduleDao

    private val workdaySchedule = LockScheduleEntity(
        name           = "Trabajo",
        targetGroupIds = listOf(1L),
        windows        = listOf(TimeWindow(540, 1080)),
        daysOfWeek     = setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
        validFrom      = null,
        validUntil     = null,
        enabled        = true,
    )

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = db.lockScheduleDao()
    }

    @After
    fun teardown() { db.close() }

    @Test
    fun upsert_returns_id_and_findById_retrieves_entity() = runTest {
        val id = dao.upsert(workdaySchedule)
        val e  = dao.findById(id)
        assertEquals("Trabajo", e?.name)
        assertEquals(listOf(TimeWindow(540, 1080)), e?.windows)
        assertEquals(setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY), e?.daysOfWeek)
    }

    @Test
    fun observeAll_emits_all_schedules() = runTest {
        dao.upsert(workdaySchedule)
        dao.upsert(workdaySchedule.copy(name = "Fin de semana", enabled = false))
        val list = dao.observeAll().first()
        assertEquals(2, list.size)
    }

    @Test
    fun observeEnabled_filters_disabled() = runTest {
        dao.upsert(workdaySchedule.copy(name = "Activo", enabled = true))
        dao.upsert(workdaySchedule.copy(name = "Inactivo", enabled = false))
        val enabled = dao.observeEnabled().first()
        assertEquals(1, enabled.size)
        assertEquals("Activo", enabled[0].name)
    }

    @Test
    fun upsert_updates_preserving_id() = runTest {
        val id = dao.upsert(workdaySchedule)
        dao.upsert(workdaySchedule.copy(id = id, name = "Modificado", enabled = false))
        val e = dao.findById(id)
        assertEquals("Modificado", e?.name)
        assertEquals(false, e?.enabled)
    }

    @Test
    fun deleteById_removes_entity() = runTest {
        val id = dao.upsert(workdaySchedule)
        dao.deleteById(id)
        assertNull(dao.findById(id))
    }

    @Test
    fun validFrom_validUntil_persist_correctly() = runTest {
        val withDates = workdaySchedule.copy(
            validFrom  = LocalDate.of(2026, 6, 1),
            validUntil = LocalDate.of(2026, 6, 30),
        )
        val id = dao.upsert(withDates)
        val e  = dao.findById(id)
        assertEquals(LocalDate.of(2026, 6, 1), e?.validFrom)
        assertEquals(LocalDate.of(2026, 6, 30), e?.validUntil)
    }

    @Test
    fun observeEnabled_is_empty_when_all_disabled() = runTest {
        dao.upsert(workdaySchedule.copy(enabled = false))
        assertTrue(dao.observeEnabled().first().isEmpty())
    }
}
