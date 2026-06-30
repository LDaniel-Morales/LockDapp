package com.lockdapp.ui.block

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.lockdapp.LockdApplication
import com.lockdapp.domain.model.AppTheme
import com.lockdapp.service.EscapeVault
import com.lockdapp.ui.theme.LockAppTheme

/**
 * Activity a pantalla completa que aloja BlockScreen. La lanza
 * BlockAccessibilityService cuando detecta una app bloqueada.
 *
 * SUPUESTOS sobre tu código (ajusta si tus firmas difieren):
 *  - LockdApplication expone `settingsRepository`.
 *  - SettingsRepository expone Flows: observeTheme(): Flow<AppTheme>,
 *    observeEscapeEnabled(): Flow<Boolean>, observeEscapeSeconds(): Flow<Int>.
 *  - LockdTheme(theme) { } envuelve MaterialTheme con el ColorScheme del tema.
 *  - EscapeVault concede acceso temporal (ver TODO en onEscapeGranted).
 */
class BlockActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val packageNameArg = intent.getStringExtra(EXTRA_PACKAGE).orEmpty()
        val appLabel = intent.getStringExtra(EXTRA_APP_LABEL).orEmpty()
        val untilText = intent.getStringExtra(EXTRA_UNTIL_TEXT).orEmpty()
        val scheduleName = intent.getStringExtra(EXTRA_SCHEDULE_NAME)

        // El botón "atrás" NO debe devolverte a la app bloqueada → ir a Home.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = goHome()
        })

        val app = application as LockdApplication
        val settings = app.settingsRepository

        setContent {
            val theme by settings.activeTheme.collectAsState(initial = AppTheme.MASTIL)
            val escapeEnabled by settings.escapeEnabled.collectAsState(initial = true)
            val escapeSeconds by settings.escapeDelaySeconds.collectAsState(initial = 30)

            LockAppTheme(theme = theme) {
                BlockScreen(
                    message = blockMessageFor(theme),
                    appLabel = appLabel,
                    untilText = untilText,
                    scheduleName = scheduleName,
                    escapeEnabled = escapeEnabled,
                    escapeSeconds = escapeSeconds,
                    onDismiss = ::goHome,
                    onEscapeGranted = {
                        EscapeVault.grant(packageNameArg)
                        finish()
                    },
                )
            }
        }
    }

    private fun goHome() {
        startActivity(
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            },
        )
        finish()
    }

    companion object {
        private const val EXTRA_PACKAGE = "extra_package"
        private const val EXTRA_APP_LABEL = "extra_app_label"
        private const val EXTRA_UNTIL_TEXT = "extra_until_text"
        private const val EXTRA_SCHEDULE_NAME = "extra_schedule_name"

        /** Mensaje por tema (copy de DESIGN.md). */
        fun blockMessageFor(theme: AppTheme): String = when (theme) {
            AppTheme.MASTIL -> "Te ataste al mástil."
            AppTheme.DIQUE -> "El dique está cerrado."
            AppTheme.LOCKD -> "lockd: acceso bloqueado."
        }

        /** Fábrica para que el AccessibilityService lance la pantalla. */
        fun newIntent(
            context: Context,
            packageName: String,
            appLabel: String,
            untilText: String,
            scheduleName: String?,
        ): Intent = Intent(context, BlockActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(EXTRA_PACKAGE, packageName)
            putExtra(EXTRA_APP_LABEL, appLabel)
            putExtra(EXTRA_UNTIL_TEXT, untilText)
            putExtra(EXTRA_SCHEDULE_NAME, scheduleName)
        }
    }
}