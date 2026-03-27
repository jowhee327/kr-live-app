package com.koreatv.live.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// === Premium Dark Color Palette ===
// Background
val PureBlack = Color(0xFF000000)
val DeepBlack = Color(0xFF0A0A0A)

// Card / Container
val CardBackground = Color(0xFF1A1A1A)
val CardBorder = Color(0xFF2A2A2A)

// Accent Gradient
val AccentPurple = Color(0xFF7B61FF)
val AccentCyan = Color(0xFF00D2FF)

// Text hierarchy
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA0A0A0)
val TextTertiary = Color(0xFF666666)

// Favorites
val FavoriteGold = Color(0xFFFFD700)

private val DarkColorScheme = darkColorScheme(
    primary = AccentPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2A1F5E),
    onPrimaryContainer = Color(0xFFD6D0FF),
    secondary = AccentCyan,
    onSecondary = Color(0xFF003544),
    background = PureBlack,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

@Immutable
data class PremiumColors(
    val accentGradient: Brush = Brush.linearGradient(listOf(AccentPurple, AccentCyan)),
    val accentGradientHorizontal: Brush = Brush.horizontalGradient(listOf(AccentPurple, AccentCyan)),
    val cardBorder: Color = CardBorder,
    val textSecondary: Color = TextSecondary,
    val textTertiary: Color = TextTertiary,
    val glowPurple: Color = AccentPurple.copy(alpha = 0.5f),
    val glowCyan: Color = AccentCyan.copy(alpha = 0.5f),
    val overlayGradientTop: Brush = Brush.verticalGradient(
        listOf(PureBlack.copy(alpha = 0.8f), Color.Transparent)
    ),
    val overlayGradientBottom: Brush = Brush.verticalGradient(
        listOf(Color.Transparent, PureBlack.copy(alpha = 0.9f))
    ),
)

val LocalPremiumColors = staticCompositionLocalOf { PremiumColors() }

@Composable
fun KoreaTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
