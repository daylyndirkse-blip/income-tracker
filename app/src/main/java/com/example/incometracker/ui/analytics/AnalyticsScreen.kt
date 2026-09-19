package com.example.incometracker.ui.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.incometracker.ui.charts.BarChartWithLabels
import com.example.incometracker.ui.charts.LineChartWithLabels
import com.example.incometracker.util.PeriodType
import com.example.incometracker.util.formatCents
import kotlin.math.roundToInt

@Composable
fun AnalyticsScreen(vm: AnalyticsViewModel = viewModel()) {
    val st by vm.state.collectAsState()

    val totalText = formatCents(st.currentTotalCents, st.currencyCode)
    val prevText = formatCents(st.previousTotalCents, st.currencyCode)
    val pctText = st.percent?.let { "${if (it >= 0) "+" else ""}${(it * 10).roundToInt() / 10.0}%" } ?: "—"

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Analytics", style = MaterialTheme.typography.titleLarge)

        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            SegmentedButton(st.periodType == PeriodType.DAILY, { vm.setPeriod(PeriodType.DAILY) }, shape = SegmentedButtonDefaults.itemShape(0,3)) { Text("Daily") }
            SegmentedButton(st.periodType == PeriodType.WEEKLY, { vm.setPeriod(PeriodType.WEEKLY) }, shape = SegmentedButtonDefaults.itemShape(1,3)) { Text("Weekly") }
            SegmentedButton(st.periodType == PeriodType.MONTHLY, { vm.setPeriod(PeriodType.MONTHLY) }, shape = SegmentedButtonDefaults.itemShape(2,3)) { Text("Monthly") }
        }

        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            SegmentedButton(st.chartType == ChartType.LINE, { vm.setChartType(ChartType.LINE) }, shape = SegmentedButtonDefaults.itemShape(0,2)) { Text("Line") }
            SegmentedButton(st.chartType == ChartType.BAR, { vm.setChartType(ChartType.BAR) }, shape = SegmentedButtonDefaults.itemShape(1,2)) { Text("Bar") }
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
                when (st.chartType) {
                    ChartType.LINE -> LineChartWithLabels(points = st.chart)
                    ChartType.BAR -> BarChartWithLabels(points = st.chart)
                }
            }
        }

        Text("By source", style = MaterialTheme.typography.titleMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(st.bySource, key = { it.sourceName }) { row ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                    Row(Modifier.fillMaxWidth().padding(14.dp)) {
                        Text(row.sourceName, modifier = Modifier.weight(1f))
                        Text(formatCents(row.totalCents, st.currencyCode))
                    }
                }
            }
        }
    }
}
