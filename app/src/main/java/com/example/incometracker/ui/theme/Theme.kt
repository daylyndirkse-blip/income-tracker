package com.example.incometracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NeonGreen = Color(0xFFB6F35C)
private val Bg = Color(0xFF0B0E10)
private val Surface = Color(0xFF11161A)
private val TextHigh = Color(0xFFEAF0F6)

private val Scheme = darkColorScheme(
  primary = NeonGreen,
  background = Bg,
  surface = Surface,
  onBackground = TextHigh,
  onSurface = TextHigh
)

@Composable
fun IncomeTheme(content: @Composable () -> Unit) {
  MaterialTheme(colorScheme = Scheme, content = content)
}
