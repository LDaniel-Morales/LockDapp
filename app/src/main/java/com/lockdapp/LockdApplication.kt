package com.lockdapp

import android.app.Application
import com.lockdapp.data.local.AppDatabase
import com.lockdapp.data.prefs.SettingsRepository
import com.lockdapp.data.repository.AppRepository
import com.lockdapp.data.repository.ScheduleRepository
import com.lockdapp.domain.model.AppGroup
import com.lockdapp.domain.model.LockSchedule
import com.lockdapp.domain.model.TimeWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LockdApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val scheduleRepository: ScheduleRepository by lazy { ScheduleRepository(database) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(this) }
    val appRepository: AppRepository by lazy { AppRepository(this) }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch { seedTestDataIfNeeded() } // TODO REMOVE
    }

    // TODO REMOVE — seed temporal para verificar el bloqueo en el dispositivo (Fase 3).
    // Inserta un grupo y un horario 24/7 la primera vez que arranca la app.
    // Borra este bloque completo una vez verificado el runtime de bloqueo.
    private suspend fun seedTestDataIfNeeded() {
        val alreadyDone = settingsRepository.isFirstRunDone.first()
        if (alreadyDone) return

        val groupId = scheduleRepository.saveGroup(
            AppGroup(
                id = 0,
                name = "Prueba bloqueo",
                packages = listOf(
                    "com.google.android.youtube",   // YouTube
                    "com.instagram.android",        // Instagram
                ),
            )
        )

        scheduleRepository.saveSchedule(
            LockSchedule(
                id = 0,
                name = "Bloqueo 24/7 (prueba)",
                targetGroupIds = listOf(groupId),
                windows = listOf(TimeWindow(startMinute = 0, endMinute = 1440)),
                daysOfWeek = java.time.DayOfWeek.values().toSet(),
                validFrom = null,
                validUntil = null,
                enabled = true,
            )
        )

        settingsRepository.markFirstRunDone()
    }
}
