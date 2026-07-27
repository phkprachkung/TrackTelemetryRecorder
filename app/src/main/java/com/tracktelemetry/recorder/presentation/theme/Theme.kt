package com.tracktelemetry.recorder.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MotorsportRed,
    secondary = AccentOrange,
    tertiary = DialWhite,
    background = DarkAsphalt,
    surface = DarkGrayPanel,
    onPrimary = DialWhite,
    onSecondary = DarkAsphalt,
    onTertiary = DarkAsphalt,
    onBackground = DialWhite,
    onSurface = DialWhite
)

private val LightColorScheme = lightColorScheme(
    primary = MotorsportRed,
    secondary = AccentOrange,
    tertiary = DarkGrayPanel,
    background = DialWhite,
    surface = DialWhite,
    onPrimary = DialWhite,
    onSecondary = DialWhite,
    onTertiary = DialWhite,
    onBackground = DarkAsphalt,
    onSurface = DarkAsphalt
)

@Composable
fun TrackTelemetryRecorderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Classic Motorsport style defaults to Dark scheme for high contrast racing HUD
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
