package com.lockdapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.Dp

fun materialShapesWithRadius(radius: Dp) = Shapes(
    extraSmall = RoundedCornerShape(radius / 4),
    small      = RoundedCornerShape(radius / 2),
    medium     = RoundedCornerShape(radius),
    large      = RoundedCornerShape(radius),
    extraLarge = RoundedCornerShape(radius),
)
