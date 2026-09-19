package com.example.incometracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NeonGreen = Color(0xFFB6F35C)
private val Bg = Color(0xFF0B0E10)
private val Surface = Color(0xFF11161A)
private val Surface2 = Color(0xFF161D22)
private val TextHigh = Color(0xFFEAF0F6)
private val TextMed = Color(0xFFB8C2CC)

private val Scheme = darkColorScheme(
    primary = NeonGreen,
    background = Bg,
    surface = Surface,
    surfaceContainer = Surface2,
    onPrimary = Color(0xFF071008),
    onBackground = TextHigh,
    onSurface = TextHigh,
    onSurfaceVariant = TextMed
)

@Composable
fun IncomeTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, typography = Typography, content = content)
}
