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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.incometracker.ui.theme.Purple
import com.example.incometracker.ui.theme.TextLow
import kotlin.math.max

@Composable
fun LineChartWithLabels(
    points: List<Pair<String, Long>>,
    modifier: Modifier = Modifier
) {
    ChartWithLabels(points = points, modifier = modifier) { values ->
        LineChart(values = values)
    }
}

@Composable
fun BarChartWithLabels(
    points: List<Pair<String, Long>>,
    modifier: Modifier = Modifier
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
    val values = safe.map { it.second / 100f }

    val labelStep = when {
        labels.size <= 8 -> 1
        labels.size <= 16 -> 2
        else -> 5
    }

    Column(modifier = modifier.fillMaxWidth()) {
        chart(values)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth()) {
            labels.forEachIndexed { index, text ->
                val show = (index == 0) || (index == labels.lastIndex) || (index % labelStep == 0)
                Text(
                    text = if (show) text else "",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextLow
                )
            }
        }
    }
}

@Composable
private fun LineChart(values: List<Float>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        val n = max(values.size, 2)
        val maxY = max(values.maxOrNull() ?: 0f, 1f)
        val w = size.width
        val h = size.height
        val stepX = if (n <= 1) 0f else w / (n - 1)
        val padBottom = h * 0.06f
        val padTop = h * 0.06f

        fun yOf(v: Float): Float = h - padBottom - (v / maxY) * (h - padTop - padBottom)

        // Grid lines (subtle)
        val gridColor = Color(0xFF2A2A27)
        for (i in 1..3) {
            val y = h - padBottom - (i / 4f) * (h - padTop - padBottom)
            drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
        }

        val path = Path()
        val area = Path()

        values.forEachIndexed { i, v ->
            val x = stepX * i
            val y = yOf(v)
            if (i == 0) {
                path.moveTo(x, y)
                area.moveTo(x, h - padBottom)
                area.lineTo(x, y)
            } else {
                // Smooth bezier
                val prevX = stepX * (i - 1)
                val prevY = yOf(values[i - 1])
                val cx1 = prevX + (x - prevX) * 0.5f
                val cx2 = prevX + (x - prevX) * 0.5f
                path.cubicTo(cx1, prevY, cx2, y, x, y)
                area.cubicTo(cx1, prevY, cx2, y, x, y)
            }
        }

        area.lineTo(stepX * (values.lastIndex), h - padBottom)
        area.close()

        // Gradient fill
        drawPath(
            path = area,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Purple.copy(alpha = 0.4f),
                    Purple.copy(alpha = 0.05f)
                )
            )
        )

        // Line
        drawPath(
            path = path,
            color = Purple,
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )

        // Dots on data points (only if few points)
        if (values.size <= 12) {
            values.forEachIndexed { i, v ->
                val x = stepX * i
                val y = yOf(v)
                drawCircle(color = Color.White, radius = 5f, center = Offset(x, y))
                drawCircle(color = Purple, radius = 3.5f, center = Offset(x, y))
            }
        }
    }
}

@Composable
private fun BarChart(values: List<Float>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        val n = max(values.size, 1)
        val maxY = max(values.maxOrNull() ?: 0f, 1f)
        val w = size.width
        val h = size.height
        val slot = w / n
        val barW = slot * 0.55f
        val padBottom = h * 0.06f

        // Grid
        val gridColor = Color(0xFF2A2A27)
        for (i in 1..3) {
            val y = h - padBottom - (i / 4f) * (h - padBottom)
            drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
        }

        values.forEachIndexed { i, v ->
            val x = i * slot + (slot - barW) / 2f
            val barH = (v / maxY) * (h - padBottom - h * 0.06f)
            val top = h - padBottom - barH

            // Shadow
            drawRoundRect(
                color = Purple.copy(alpha = 0.15f),
                topLeft = Offset(x + 3f, top + 3f),
                size = Size(barW, barH),
                cornerRadius = CornerRadius(16f, 16f)
            )

            // Bar with gradient
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(PurpleLight, Purple),
                    startY = top,
                    endY = top + barH
                ),
                topLeft = Offset(x, top),
                size = Size(barW, barH),
                cornerRadius = CornerRadius(16f, 16f)
            )
        }
    }
}

private val PurpleLight = Color(0xFF9F67FF)
