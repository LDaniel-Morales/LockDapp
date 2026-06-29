package com.lockdapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// ── Mástil — náutico, azul medianoche + latón ────────────────────────────────
object MastilColors {
    val bg              = Color(0xFF0B1426)
    val surface         = Color(0xFF111E33)
    val surfaceElevated = Color(0xFF18283F)
    val border          = Color(0xFF24344D)
    val textPrimary     = Color(0xFFEAF0F7)
    val textSecondary   = Color(0xFF9DB0C7)
    val textMuted       = Color(0xFF5F7290)
    val accent          = Color(0xFFD4A24E)
    val accentMuted     = Color(0xFF8A6A33)
    val blockActive     = Color(0xFFE0653C)
    val success         = Color(0xFF4FB286)
}

val MastilColorScheme = darkColorScheme(
    background          = MastilColors.bg,
    surface             = MastilColors.surface,
    surfaceContainer    = MastilColors.surfaceElevated,
    outline             = MastilColors.border,
    onBackground        = MastilColors.textPrimary,
    onSurface           = MastilColors.textPrimary,
    onSurfaceVariant    = MastilColors.textSecondary,
    primary             = MastilColors.accent,
    onPrimary           = MastilColors.bg,
    primaryContainer    = MastilColors.accentMuted,
    onPrimaryContainer  = MastilColors.textPrimary,
    error               = MastilColors.blockActive,
    tertiary            = MastilColors.success,
)

// ── Dique — ingeniería, pizarra + teal ───────────────────────────────────────
object DiqueColors {
    val bg              = Color(0xFF0D1117)
    val surface         = Color(0xFF161B22)
    val surfaceElevated = Color(0xFF1C232D)
    val border          = Color(0xFF2A323D)
    val textPrimary     = Color(0xFFE6EDF3)
    val textSecondary   = Color(0xFF9AA7B4)
    val textMuted       = Color(0xFF5C6773)
    val accent          = Color(0xFF2DD4BF)
    val accentMuted     = Color(0xFF1B7A6F)
    val blockActive     = Color(0xFFF4A93C)
    val success         = Color(0xFF3DD68C)
}

val DiqueColorScheme = darkColorScheme(
    background          = DiqueColors.bg,
    surface             = DiqueColors.surface,
    surfaceContainer    = DiqueColors.surfaceElevated,
    outline             = DiqueColors.border,
    onBackground        = DiqueColors.textPrimary,
    onSurface           = DiqueColors.textPrimary,
    onSurfaceVariant    = DiqueColors.textSecondary,
    primary             = DiqueColors.accent,
    onPrimary           = DiqueColors.bg,
    primaryContainer    = DiqueColors.accentMuted,
    onPrimaryContainer  = DiqueColors.textPrimary,
    error               = DiqueColors.blockActive,
    tertiary            = DiqueColors.success,
)

// ── LockD — daemon Linux, negro OLED + violeta ───────────────────────────────
object LockDColors {
    val bg              = Color(0xFF0A0A0F)
    val surface         = Color(0xFF14121C)
    val surfaceElevated = Color(0xFF1E1B2E)
    val border          = Color(0xFF2C2740)
    val textPrimary     = Color(0xFFECE8F5)
    val textSecondary   = Color(0xFFA39DB8)
    val textMuted       = Color(0xFF6B6485)
    val accent          = Color(0xFFA855F7)
    val accentMuted     = Color(0xFF6B3FA0)
    val blockActive     = Color(0xFFF43F5E)
    val success         = Color(0xFF34D399)
}

val LockDColorScheme = darkColorScheme(
    background          = LockDColors.bg,
    surface             = LockDColors.surface,
    surfaceContainer    = LockDColors.surfaceElevated,
    outline             = LockDColors.border,
    onBackground        = LockDColors.textPrimary,
    onSurface           = LockDColors.textPrimary,
    onSurfaceVariant    = LockDColors.textSecondary,
    primary             = LockDColors.accent,
    onPrimary           = LockDColors.bg,
    primaryContainer    = LockDColors.accentMuted,
    onPrimaryContainer  = LockDColors.textPrimary,
    error               = LockDColors.blockActive,
    tertiary            = LockDColors.success,
)
