package com.lockdapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.lockdapp.domain.model.AppTheme

// Corner radius per theme (DESIGN.md §7)
private fun shapesFor(theme: AppTheme): Shapes {
    val radius = when (theme) {
        AppTheme.MASTIL -> 16.dp
        AppTheme.LOCKD  -> 12.dp
        AppTheme.DIQUE  -> 8.dp
    }
    return materialShapesWithRadius(radius)
}

@Composable
fun LockAppTheme(
    theme: AppTheme = AppTheme.MASTIL,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (theme) {
        AppTheme.MASTIL -> MastilColorScheme
        AppTheme.DIQUE  -> DiqueColorScheme
        AppTheme.LOCKD  -> LockDColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = LockAppTypography,
        shapes      = shapesFor(theme),
        content     = content,
    )
}
