package com.lockdapp.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.lockdapp.domain.model.InstalledApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {

    // Icon loading is intentionally excluded here (CLAUDE.md: "carga perezosa").
    // Callers request icons individually via PackageManager on demand.
    suspend fun getInstalledApps(): List<InstalledApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val launcherIntent = Intent(Intent.ACTION_MAIN, null)
            .addCategory(Intent.CATEGORY_LAUNCHER)
        @Suppress("DEPRECATION")
        pm.queryIntentActivities(launcherIntent, PackageManager.GET_META_DATA)
            .map { ri ->
                InstalledApp(
                    packageName = ri.activityInfo.packageName,
                    label       = ri.loadLabel(pm).toString(),
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }
}
