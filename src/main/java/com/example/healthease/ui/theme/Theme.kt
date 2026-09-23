package com.example.healthease.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = PrimaryGreenLight,
    onPrimaryContainer = PrimaryGreenDark,
    secondary = SecondaryYellow,
    onSecondary = TextPrimary,
    secondaryContainer = SecondaryYellowLight,
    onSecondaryContainer = SecondaryYellowDark,
    tertiary = AccentTeal,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = BorderLight,
    onSurfaceVariant = TextSecondary,
    error = DangerRed,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = PrimaryGreenLight,
    onPrimary = PrimaryGreenDark,
    primaryContainer = PrimaryGreenDark,
    onPrimaryContainer = PrimaryGreenLight,
    secondary = SecondaryYellow,
    onSecondary = TextPrimary,
    secondaryContainer = SecondaryYellowDark,
    onSecondaryContainer = SecondaryYellowLight,
    tertiary = AccentTeal,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = DarkTextSecondary,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun HealthEaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = HealthEaseTypography,
        content = content
    )
}