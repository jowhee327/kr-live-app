package com.koreatv.live.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D1B2A),
    primaryContainer = Color(0xFF1A237E),
    onPrimaryContainer = Color(0xFFD6E4FF),
    secondary = Color(0xFFCE93D8),
    onSecondary = Color(0xFF1B0021),
    background = Color(0xFF0D1117),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF161B22),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF1E2530),
    onSurfaceVariant = Color(0xFFC4C6CF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

@Composable
fun KoreaTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
