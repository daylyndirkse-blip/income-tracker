package com.example.incometracker.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object Gradients {
    val primaryGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF6750A4),
            Color(0xFF4C1D95)
        )
    )
    
    val successGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF4CAF50),
            Color(0xFF2E7D32)
        )
    )
    
    val warningGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFF9800),
            Color(0xFFE65100)
        )
    )
    
    val accentGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF2196F3),
            Color(0xFF1565C0)
        )
    )
    
    val shimmerGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE0E0E0),
            Color(0xFFF5F5F5),
            Color(0xFFE0E0E0)
        )
    )
}
