package com.lockdapp.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import com.lockdapp.service.BlockAccessibilityService

data class PermissionStates(
    val accessibility: Boolean = false,
    val usageStats: Boolean = false,
    val overlay: Boolean = false,
    val batteryExempt: Boolean = false,
) {
    val allCriticalGranted: Boolean get() = accessibility && usageStats
}

object PermissionsHelper {

    fun checkAll(context: Context) = PermissionStates(
        accessibility = isAccessibilityEnabled(context),
        usageStats    = isUsageStatsEnabled(context),
        overlay       = isOverlayEnabled(context),
        batteryExempt = isBatteryOptimizationExempt(context),
    )

    fun isAccessibilityEnabled(context: Context): Boolean {
        val enabled = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
        ) ?: return false
        val target = ComponentName(context, BlockAccessibilityService::class.java).flattenToString()
        return enabled.split(':').any { it.equals(target, ignoreCase = true) }
    }

    fun isUsageStatsEnabled(context: Context): Boolean {
        // Querying usage stats returns non-empty results only when the permission is granted,
        // regardless of actual recent usage over a 3-day window.
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as android.app.usage.UsageStatsManager
        val end = System.currentTimeMillis()
        val begin = end - 3 * 24 * 60 * 60 * 1000L
        val stats = usm.queryUsageStats(android.app.usage.UsageStatsManager.INTERVAL_DAILY, begin, end)
        return stats != null && stats.isNotEmpty()
    }

    fun isOverlayEnabled(context: Context): Boolean = Settings.canDrawOverlays(context)

    fun isBatteryOptimizationExempt(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }

    fun openAccessibilitySettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).withNewTask()
        )
    }

    fun openUsageStatsSettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).withNewTask()
        )
    }

    fun openOverlaySettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                .setData(Uri.parse("package:${context.packageName}"))
                .withNewTask()
        )
    }

    fun openBatteryOptimizationSettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                .setData(Uri.parse("package:${context.packageName}"))
                .withNewTask()
        )
    }

    private fun Intent.withNewTask() = apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
}
