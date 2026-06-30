package com.lockdapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lockdapp.LockdApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val app = context.applicationContext as LockdApplication
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val schedules = app.scheduleRepository.observeEnabledSchedules().first()
                AlarmScheduler.scheduleWindowAlarms(context, schedules)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
