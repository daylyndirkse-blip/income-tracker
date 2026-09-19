package com.example.incometracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Core palette
val Purple = Color(0xFF7C3AED)
val PurpleLight = Color(0xFF9F67FF)
val PurpleDim = Color(0xFF3D1F78)
val Bg = Color(0xFF060309)
val CardBg = Color(0xFF1B1B19)
val CardBg2 = Color(0xFF232320)
val TextHigh = Color(0xFFFFFFFF)
val TextMed = Color(0xFFAAAAAA)
val TextLow = Color(0xFF666666)
val Success = Color(0xFF22C55E)
val Danger = Color(0xFFEF4444)

private val Scheme = darkColorScheme(
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

@Composable
fun IncomeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Scheme,
        typography = Typography,
        content = content
    )
}
