package com.example.incometracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GradientCard(
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = modifier
        .shadow(8.dp, RoundedCornerShape(16.dp))
        .clip(RoundedCornerShape(16.dp))
        .background(gradient)
    
    if (onClick != null) {
        PressableCard(
            onClick = onClick,
            modifier = cardModifier
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                content = content
            )
        }
    } else {
        Column(
            modifier = cardModifier.padding(20.dp),
            content = content
        )
    }
}

@Composable
fun ElevatedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: Int = 4,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.shadow(elevation.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (onClick != null) {
            PressableCard(onClick = onClick) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    content = content
                )
            }
        } else {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        color = backgroundColor,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}
