package com.lockdapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lockdapp.domain.model.AppTheme
import com.lockdapp.ui.onboarding.OnboardingScreen
import com.lockdapp.ui.theme.LockAppTheme
import com.lockdapp.util.PermissionStates
import com.lockdapp.util.PermissionsHelper

class MainActivity : ComponentActivity() {

    private val permStates = mutableStateOf(PermissionStates())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LockAppTheme(theme = AppTheme.MASTIL) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val states = permStates.value
                    if (states.allCriticalGranted) {
                        ServiceActiveScreen()
                    } else {
                        OnboardingScreen(
                            permStates = states,
                            onGrantAccessibility = { PermissionsHelper.openAccessibilitySettings(this) },
                            onGrantUsageStats    = { PermissionsHelper.openUsageStatsSettings(this) },
                            onGrantOverlay       = { PermissionsHelper.openOverlaySettings(this) },
                            onGrantBattery       = { PermissionsHelper.openBatteryOptimizationSettings(this) },
                            onContinue           = { /* already gated by allCriticalGranted */ },
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        permStates.value = PermissionsHelper.checkAll(this)
    }
}

// Placeholder shown once all critical permissions are granted.
// Will be replaced by the dashboard in a later phase.
@androidx.compose.runtime.Composable
private fun ServiceActiveScreen() {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Servicio activo",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}
