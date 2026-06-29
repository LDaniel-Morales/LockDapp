package com.lockdapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AppGroupDao {

    @Query("SELECT * FROM app_groups ORDER BY name")
    fun observeAll(): Flow<List<AppGroupEntity>>

    @Query("SELECT * FROM app_groups WHERE id = :id")
    suspend fun findById(id: Long): AppGroupEntity?

    @Upsert
    suspend fun upsert(entity: AppGroupEntity): Long

    @Delete
    suspend fun delete(entity: AppGroupEntity)

    @Query("DELETE FROM app_groups WHERE id = :id")
    suspend fun deleteById(id: Long)
}
