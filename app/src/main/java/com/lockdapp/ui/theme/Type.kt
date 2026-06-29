package com.lockdapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// TODO: Replace with actual font files in res/font/ once downloaded:
//   Space Grotesk  → https://fonts.google.com/specimen/Space+Grotesk
//   Inter          → https://fonts.google.com/specimen/Inter
//   JetBrains Mono → https://fonts.google.com/specimen/JetBrains+Mono
//
// Usage after adding font resources:
//   val SpaceGroteskFamily = FontFamily(
//       Font(R.font.space_grotesk_semibold, FontWeight.SemiBold)
//   )
val SpaceGroteskFamily: FontFamily = FontFamily.SansSerif
val InterFamily: FontFamily        = FontFamily.SansSerif
val JetBrainsMonoFamily: FontFamily = FontFamily.Monospace

// Scale: Display 28/22 · Title 18 · Body 15 · Caption 13 · Mono 13
val LockAppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
    // Mono style for package names and times — use labelMedium slot
    labelMedium = TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
)
