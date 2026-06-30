package com.lockdapp.service

import android.accessibilityservice.AccessibilityService
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.lockdapp.LockdApplication
import com.lockdapp.domain.engine.BlockEngine
import com.lockdapp.ui.block.BlockActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class BlockAccessibilityService : AccessibilityService() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    @Volatile private var engine: BlockEngine = BlockEngine(emptyList(), emptyList())

    override fun onServiceConnected() {
        val app = application as LockdApplication

        scope.launch {
            app.scheduleRepository.observeBlockEngine().collect { fresh ->
                engine = fresh
            }
        }

        scope.launch(Dispatchers.IO) {
            app.scheduleRepository.observeEnabledSchedules().collectLatest { schedules ->
                AlarmScheduler.scheduleWindowAlarms(this@BlockAccessibilityService, schedules)
            }
        }

        scope.launch(Dispatchers.IO) {
            while (true) {
                delay(TICK_MS)
                checkCurrentApp()
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return
        if (pkg == packageName) return
        showBlockScreen(pkg)
    }

    private fun checkCurrentApp() {
        val usm = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val end = System.currentTimeMillis()
        val pkg = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, end - TICK_MS * 2, end)
            ?.maxByOrNull { it.lastTimeUsed }?.packageName ?: return
        if (pkg == packageName) return
        showBlockScreen(pkg)
    }

    private fun showBlockScreen(pkg: String) {
        if (EscapeVault.isExempt(pkg)) return

        val now  = LocalDateTime.now()
        val info = engine.blockInfo(pkg, now) ?: return

        val label = runCatching {
            packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(pkg, 0)
            ).toString()
        }.getOrDefault(pkg)

        val untilText = if (info.windowEndMinute in 1 until 1440) {
            "Disponible de nuevo a las %02d:%02d".format(
                info.windowEndMinute / 60, info.windowEndMinute % 60
            )
        } else {
            "Bloqueada por ahora"
        }

        startActivity(
            BlockActivity.newIntent(
                context      = this,
                packageName  = pkg,
                appLabel     = label,
                untilText    = untilText,
                scheduleName = info.groupName,
            )
        )
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    companion object {
        private const val TICK_MS = 12_000L
    }
}
