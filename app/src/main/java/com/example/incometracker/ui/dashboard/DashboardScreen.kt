package com.example.incometracker.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.incometracker.util.PeriodType
import com.example.incometracker.util.formatCents
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.LineChart
import com.patrykandpatrick.vico.compose.m3.rememberM3ChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(onAdd: () -> Unit, vm: DashboardViewModel = viewModel()) {
    val st by vm.state.collectAsState()

    val totalText = formatCents(st.currentTotalCents, st.currencyCode)
    val prevText = formatCents(st.previousTotalCents, st.currencyCode)
    val pctText = st.percent?.let { "${if (it >= 0) "+" else ""}${(it * 10).roundToInt() / 10.0}%" } ?: "—"

    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = onAdd) { Text("+") } }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Dashboard", style = MaterialTheme.typography.titleLarge)

            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = st.periodType == PeriodType.DAILY,
                    onClick = { vm.setPeriod(PeriodType.DAILY) },
                    shape = SegmentedButtonDefaults.itemShape(0, 3)
                ) { Text("Daily") }
                SegmentedButton(
                    selected = st.periodType == PeriodType.WEEKLY,
                    onClick = { vm.setPeriod(PeriodType.WEEKLY) },
                    shape = SegmentedButtonDefaults.itemShape(1, 3)
                ) { Text("Weekly") }
                SegmentedButton(
                    selected = st.periodType == PeriodType.MONTHLY,
                    onClick = { vm.setPeriod(PeriodType.MONTHLY) },
                    shape = SegmentedButtonDefaults.itemShape(2, 3)
                ) { Text("Monthly") }
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Total income", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(totalText, style = MaterialTheme.typography.headlineMedium)
                    Text("Previous: $prevText   ($pctText)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Trend", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LabeledLineChart(points = st.chart)
                }
            }
        }
    }
}

@Composable
private fun LabeledLineChart(points: List<Pair<String, Long>>) {
    val safe = if (points.isEmpty()) listOf("" to 0L) else points
    val labels = safe.map { it.first }
    val y = safe.map { it.second / 100f }.toTypedArray()
    val model = entryModelOf(*y)

    val labelStep = when {
        labels.size <= 12 -> 1
        labels.size <= 18 -> 2
        else -> 5
    }

    val bottomAxis = rememberBottomAxis(
        valueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            val i = value.roundToInt().coerceIn(0, labels.lastIndex)
            val keyTick = (i == 0) || (i == labels.lastIndex) || (i % labelStep == 0)
            if (keyTick) labels[i] else ""
        }
    )

    Chart(
        chart = LineChart(),
        model = model,
        chartStyle = rememberM3ChartStyle(),
        startAxis = rememberStartAxis(),
        bottomAxis = bottomAxis
    )
}
