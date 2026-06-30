package com.lockdapp.ui.block

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * UI pura de la pantalla de bloqueo — la "firma" de LockDapp.
 *
 * Sin dependencias de Activity ni de repositorios: recibe datos + callbacks,
 * para que sea testeable y previsualizable con @Preview. La aloja BlockActivity.
 *
 * El tono es el de un pacto, no un castigo (ver DESIGN.md): sereno, en presente.
 *
 * @param message         Frase serena del tema activo (p.ej. "Te ataste al mástil.").
 * @param appLabel        Nombre legible de la app bloqueada.
 * @param untilText       Texto ya formateado, p.ej. "Disponible de nuevo a las 18:00".
 * @param scheduleName    Nombre del horario que la bloquea (opcional).
 * @param escapeEnabled   Si se muestra la válvula de escape (configurable en Ajustes).
 * @param escapeSeconds   Segundos de espera del escape (default 30).
 * @param onDismiss       "Volver" → mandar a Home.
 * @param onEscapeGranted Se invoca cuando la cuenta regresiva del escape termina.
 */
@Composable
fun BlockScreen(
    message: String,
    appLabel: String,
    untilText: String,
    scheduleName: String?,
    escapeEnabled: Boolean,
    escapeSeconds: Int,
    onDismiss: () -> Unit,
    onEscapeGranted: () -> Unit,
) {
    var counting by remember { mutableStateOf(false) }
    var remaining by remember { mutableIntStateOf(escapeSeconds) }

    // Cuenta regresiva: fricción deliberada en vez de un bloqueo imposible.
    LaunchedEffect(counting) {
        if (counting) {
            remaining = escapeSeconds
            while (remaining > 0) {
                delay(1_000)
                remaining--
            }
            onEscapeGranted()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Marca de identidad del tema: un acento sobrio, sin agresividad.
            Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "•",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = appLabel,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Text(
                text = untilText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (scheduleName != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = scheduleName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(48.dp))

            if (!counting) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text("Volver")
                }

                if (escapeEnabled) {
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = { counting = true }) {
                        Text(
                            text = "Necesito acceso ahora",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                Text(
                    text = remaining.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.error,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Espera un momento antes de continuar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(24.dp))
                TextButton(onClick = { counting = false }) {
                    Text("Cancelar")
                }
            }
        }
    }
}