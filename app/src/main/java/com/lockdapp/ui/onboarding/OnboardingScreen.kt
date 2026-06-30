package com.lockdapp.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lockdapp.util.PermissionStates

@Composable
fun OnboardingScreen(
    permStates: PermissionStates,
    onGrantAccessibility: () -> Unit,
    onGrantUsageStats: () -> Unit,
    onGrantOverlay: () -> Unit,
    onGrantBattery: () -> Unit,
    onContinue: () -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Primeros pasos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Para cumplir el pacto necesito acceso a unos ajustes del sistema. Cada uno está ahí por una razón concreta.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(4.dp))

            PermissionCard(
                title = "Servicio de accesibilidad",
                description = "Detecta qué aplicación está en primer plano para aplicar el bloqueo en el momento exacto.",
                isCritical = true,
                isGranted = permStates.accessibility,
                onGrant = onGrantAccessibility,
            )

            PermissionCard(
                title = "Acceso al uso de apps",
                description = "Comprueba la app activa cuando una ventana de bloqueo entra en vigor y el usuario ya estaba dentro.",
                isCritical = true,
                isGranted = permStates.usageStats,
                onGrant = onGrantUsageStats,
            )

            PermissionCard(
                title = "Mostrar sobre otras apps",
                description = "Reservado para la pantalla de bloqueo que aparece en fases posteriores.",
                isCritical = false,
                isGranted = permStates.overlay,
                onGrant = onGrantOverlay,
            )

            PermissionCard(
                title = "Sin restricción de batería",
                description = "Mantiene el servicio activo aunque el teléfono entre en reposo prolongado.",
                isCritical = false,
                isGranted = permStates.batteryExempt,
                onGrant = onGrantBattery,
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!permStates.allCriticalGranted) {
                Text(
                    text = "Los dos primeros permisos son necesarios para que el servicio funcione.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Button(
                onClick = onContinue,
                enabled = permStates.allCriticalGranted,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Continuar")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    isCritical: Boolean,
    isGranted: Boolean,
    onGrant: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted)
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                StatusLabel(granted = isGranted, critical = isCritical)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (!isGranted) {
                Spacer(modifier = Modifier.height(12.dp))
                FilledTonalButton(
                    onClick = onGrant,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text("Conceder")
                }
            }
        }
    }
}

@Composable
private fun StatusLabel(granted: Boolean, critical: Boolean) {
    val text = if (granted) "Concedido" else if (critical) "Necesario" else "Recomendado"
    val color = when {
        granted   -> MaterialTheme.colorScheme.primary
        critical  -> MaterialTheme.colorScheme.error
        else      -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Medium,
    )
}
