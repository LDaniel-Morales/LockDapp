package com.lockdapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface LockScheduleDao {

    @Query("SELECT * FROM lock_schedules ORDER BY name")
    fun observeAll(): Flow<List<LockScheduleEntity>>

    @Query("SELECT * FROM lock_schedules WHERE enabled = 1")
    fun observeEnabled(): Flow<List<LockScheduleEntity>>

    @Query("SELECT * FROM lock_schedules WHERE id = :id")
    suspend fun findById(id: Long): LockScheduleEntity?

    @Upsert
    suspend fun upsert(entity: LockScheduleEntity): Long

    @Delete
    suspend fun delete(entity: LockScheduleEntity)

    @Query("DELETE FROM lock_schedules WHERE id = :id")
    suspend fun deleteById(id: Long)
}
