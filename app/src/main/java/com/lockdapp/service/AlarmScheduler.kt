package com.lockdapp.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.lockdapp.domain.model.LockSchedule
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object AlarmScheduler {

    const val ACTION_WINDOW_BOUNDARY = "com.lockdapp.ACTION_WINDOW_BOUNDARY"

    // Fixed pool of request codes; cancelled and re-used on every schedule change.
    private const val BASE_REQUEST_CODE = 1000
    private const val MAX_SLOTS = 100

    fun scheduleWindowAlarms(context: Context, schedules: List<LockSchedule>) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Cancel all slots so stale alarms don't fire.
        repeat(MAX_SLOTS) { i ->
            am.cancel(buildPendingIntent(context, BASE_REQUEST_CODE + i))
        }

        val now = System.currentTimeMillis()
        val today = LocalDate.now()
        var slot = 0

        for (schedule in schedules) {
            if (!schedule.enabled) continue
            if (schedule.validFrom != null && today.isBefore(schedule.validFrom)) continue
            if (schedule.validUntil != null && today.isAfter(schedule.validUntil)) continue
            if (today.dayOfWeek !in schedule.daysOfWeek) continue

            for (window in schedule.windows) {
                for (minute in listOf(window.startMinute, window.endMinute)) {
                    val triggerMs = minuteToEpochMilli(minute)
                    if (triggerMs > now && slot < MAX_SLOTS) {
                        setExact(am, triggerMs, buildPendingIntent(context, BASE_REQUEST_CODE + slot))
                        slot++
                    }
                }
            }
        }
    }

    private fun minuteToEpochMilli(minute: Int): Long =
        LocalDate.now()
            .atTime(LocalTime.of(minute / 60, minute % 60))
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    private fun setExact(am: AlarmManager, triggerMs: Long, pi: PendingIntent) {
        // Use exact alarm if permission is available; fall back to inexact otherwise.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pi)
        } else {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pi)
        }
    }

    private fun buildPendingIntent(context: Context, requestCode: Int): PendingIntent {
        val intent = Intent(ACTION_WINDOW_BOUNDARY).setPackage(context.packageName)
        return PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
