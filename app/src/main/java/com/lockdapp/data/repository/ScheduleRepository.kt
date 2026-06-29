package com.lockdapp.data.repository

import com.lockdapp.data.local.AppDatabase
import com.lockdapp.data.local.toDomain
import com.lockdapp.data.local.toEntity
import com.lockdapp.domain.engine.BlockEngine
import com.lockdapp.domain.model.AppGroup
import com.lockdapp.domain.model.LockSchedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ScheduleRepository(private val db: AppDatabase) {

    // ── Schedules ─────────────────────────────────────────────────────────────

    fun observeSchedules(): Flow<List<LockSchedule>> =
        db.lockScheduleDao().observeAll().map { it.map { e -> e.toDomain() } }

    fun observeEnabledSchedules(): Flow<List<LockSchedule>> =
        db.lockScheduleDao().observeEnabled().map { it.map { e -> e.toDomain() } }

    suspend fun findSchedule(id: Long): LockSchedule? =
        db.lockScheduleDao().findById(id)?.toDomain()

    suspend fun saveSchedule(schedule: LockSchedule): Long =
        db.lockScheduleDao().upsert(schedule.toEntity())

    suspend fun deleteSchedule(id: Long) =
        db.lockScheduleDao().deleteById(id)

    // ── Groups ────────────────────────────────────────────────────────────────

    fun observeGroups(): Flow<List<AppGroup>> =
        db.appGroupDao().observeAll().map { it.map { e -> e.toDomain() } }

    suspend fun findGroup(id: Long): AppGroup? =
        db.appGroupDao().findById(id)?.toDomain()

    suspend fun saveGroup(group: AppGroup): Long =
        db.appGroupDao().upsert(group.toEntity())

    suspend fun deleteGroup(id: Long) =
        db.appGroupDao().deleteById(id)

    // ── Live BlockEngine backed by real data ──────────────────────────────────
    // Combines enabled schedules + all groups into a ready-to-query BlockEngine.
    // Collectors (ForegroundService, periodic tick) get a fresh instance on every
    // DB change without any extra wiring.
    fun observeBlockEngine(): Flow<BlockEngine> = combine(
        db.lockScheduleDao().observeEnabled().map { it.map { e -> e.toDomain() } },
        db.appGroupDao().observeAll().map { it.map { e -> e.toDomain() } },
    ) { schedules, groups -> BlockEngine(schedules, groups) }
}
