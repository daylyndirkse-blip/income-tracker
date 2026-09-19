package com.example.incometracker.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun LineChartWithLabels(
    points: List<Pair<String, Long>>,
    modifier: Modifier = Modifier,
) {
    ChartWithLabels(points = points, modifier = modifier) { values ->
        LineChart(values = values)
    }
}

@Composable
fun BarChartWithLabels(
    points: List<Pair<String, Long>>,
    modifier: Modifier = Modifier,
) {
    ChartWithLabels(points = points, modifier = modifier) { values ->
        BarChart(values = values)
    }
}

@Composable
private fun ChartWithLabels(
    points: List<Pair<String, Long>>,
    modifier: Modifier = Modifier,
    chart: @Composable (List<Float>) -> Unit
) {
    val safe = if (points.isEmpty()) listOf("" to 0L) else points
    val labels = safe.map { it.first }
    val values = safe.map { it.second / 100f } // cents -> currency units

    val labelStep = when {
        labels.size <= 12 -> 1
        labels.size <= 18 -> 2
        else -> 5
    }

    Column(modifier = modifier.fillMaxWidth()) {
        chart(values)

        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth()) {
            labels.forEachIndexed { index, text ->
                val show = (index == 0) || (index == labels.lastIndex) || (index % labelStep == 0)
                Text(
                    text = if (show) text else "",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LineChart(values: List<Float>) {
    val primary = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
    ) {
        val n = max(values.size, 2)
        val maxY = max(values.maxOrNull() ?: 0f, 1f)
        val w = size.width
        val h = size.height

        val stepX = if (n <= 1) 0f else w / (n - 1)

        fun yOf(v: Float): Float = h - (v / maxY) * (h * 0.92f) - (h * 0.04f)

        // subtle baseline
        drawLine(
            color = onSurfaceVariant.copy(alpha = 0.25f),
            start = Offset(0f, h - 1f),
            end = Offset(w, h - 1f),
            strokeWidth = 1f
        )

        val path = Path()
        val area = Path()

        values.forEachIndexed { i, v ->
            val x = stepX * i
            val y = yOf(v)
            if (i == 0) {
                path.moveTo(x, y)
                area.moveTo(x, h)
                area.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                area.lineTo(x, y)
            }
        }
        area.lineTo(stepX * (values.lastIndex), h)
        area.close()

        drawPath(
            path = area,
            brush = Brush.verticalGradient(
                colors = listOf(primary.copy(alpha = 0.35f), primary.copy(alpha = 0.02f))
            )
        )

        drawPath(
            path = path,
            color = primary,
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun BarChart(values: List<Float>) {
    val primary = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
    ) {
        val n = max(values.size, 1)
        val maxY = max(values.maxOrNull() ?: 0f, 1f)

        val w = size.width
        val h = size.height
        val slot = w / n
        val barW = slot * 0.58f

        // baseline
        drawLine(
            color = onSurfaceVariant.copy(alpha = 0.25f),
            start = Offset(0f, h - 1f),
            end = Offset(w, h - 1f),
            strokeWidth = 1f
        )

        values.forEachIndexed { i, v ->
            val x = i * slot + (slot - barW) / 2f
            val barH = (v / maxY) * (h * 0.92f)
            val top = h - barH - (h * 0.04f)

            drawRoundRect(
                color = primary.copy(alpha = 0.9f),
                topLeft = Offset(x, top),
                size = Size(barW, barH),
                cornerRadius = CornerRadius(14f, 14f)
            )
        }
    }
}
