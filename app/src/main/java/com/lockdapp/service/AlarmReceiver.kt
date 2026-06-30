package com.lockdapp.service

import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lockdapp.LockdApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != AlarmScheduler.ACTION_WINDOW_BOUNDARY) return

        val app = context.applicationContext as LockdApplication
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val engine = app.scheduleRepository.observeBlockEngine().first()
                val pkg = currentForegroundPackage(context) ?: return@launch
                if (pkg == context.packageName) return@launch
                if (engine.isBlocked(pkg, LocalDateTime.now())) {
                    sendToHome(context)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun currentForegroundPackage(context: Context): String? {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val end = System.currentTimeMillis()
        return usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, end - 10_000, end)
            ?.maxByOrNull { it.lastTimeUsed }
            ?.packageName
    }

    private fun sendToHome(context: Context) {
        context.startActivity(
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }
}
