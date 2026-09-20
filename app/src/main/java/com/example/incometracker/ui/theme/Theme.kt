package com.example.incometracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Custom color definitions
val Purple = Color(0xFF6750A4)
val PurpleLight = Color(0xFF9575CD)
val PurpleDim = Color(0xFF2D2640)
val Bg = Color(0xFFF5F5F5)
val BgDark = Color(0xFF121212)
val CardBg = Color(0xFFFFFFFF)
val CardBgDark = Color(0xFF1E1E1E)
val CardBg2 = Color(0xFFF8F8F8)
val CardBg2Dark = Color(0xFF2A2A2A)
val TextHigh = Color(0xFF1C1C1C)
val TextHighDark = Color(0xFFE0E0E0)
val TextMed = Color(0xFF666666)
val TextMedDark = Color(0xFFAAAAAA)
val TextLow = Color(0xFF999999)
val TextLowDark = Color(0xFF888888)
val Danger = Color(0xFFE53935)
val Success = Color(0xFF4CAF50)

// Light theme colors
private val LightColorScheme = lightColorScheme(
    primary = Purple,
    primaryContainer = PurpleDim,
    onPrimary = Color.White,
    background = Bg,
    surface = CardBg,
    surfaceContainer = CardBg2,
    onBackground = TextHigh,
    onSurface = TextHigh,
    onSurfaceVariant = TextMed,
    secondary = PurpleLight,
    error = Danger
)

// Dark theme colors
private val DarkColorScheme = darkColorScheme(
    primary = PurpleLight,
    primaryContainer = Purple,
    onPrimary = Color.Black,
    background = BgDark,
    surface = CardBgDark,
    surfaceContainer = CardBg2Dark,
    onBackground = TextHighDark,
    onSurface = TextHighDark,
    onSurfaceVariant = TextMedDark,
    secondary = PurpleLight,
    error = Danger
)

@Composable
fun IncomeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
