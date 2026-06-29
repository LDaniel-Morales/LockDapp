package com.lockdapp

import android.app.Application
import com.lockdapp.data.local.AppDatabase
import com.lockdapp.data.prefs.SettingsRepository
import com.lockdapp.data.repository.AppRepository
import com.lockdapp.data.repository.ScheduleRepository

class LockdApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    val scheduleRepository: ScheduleRepository by lazy { ScheduleRepository(database) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(this) }
    val appRepository: AppRepository by lazy { AppRepository(this) }

    override fun onCreate() {
        super.onCreate()
    }
}
