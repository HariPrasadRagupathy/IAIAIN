package com.hp.iaiain.design.system

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val darkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = AccentOrange,
    tertiary = AccentPurple,
    background = BackgroundDark,
    surface = BackgroundMedium,
    surfaceVariant = SurfaceLight,
    onPrimary = TextWhite,
    onSecondary = PrimaryDark,
    onTertiary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite,
    onSurfaceVariant = TextGray,
    error = Error,
    onError = Color.White,
    outline = TextDarkGray,
)

@Composable
fun IAIAINTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme,
        typography = AppTypography,
        content = content
    )
}

