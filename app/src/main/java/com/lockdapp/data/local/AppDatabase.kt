package com.lockdapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [AppGroupEntity::class, LockScheduleEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(LockConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appGroupDao(): AppGroupDao
    abstract fun lockScheduleDao(): LockScheduleDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lockdapp.db",
                ).build().also { instance = it }
            }
    }
}
