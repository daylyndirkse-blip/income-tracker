package com.example.incometracker.ui.analytics

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.incometracker.ui.charts.BarChartWithLabels
import com.example.incometracker.ui.charts.LineChartWithLabels
import com.example.incometracker.ui.components.*
import com.example.incometracker.ui.theme.*
import com.example.incometracker.util.PeriodType
import com.example.incometracker.util.formatCents

@Composable
fun AnalyticsScreen(vm: AnalyticsViewModel = viewModel()) {
    val st by vm.state.collectAsState()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(600)
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
    ) {
        if (isLoading) {
            DashboardShimmer()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                SlideInContainer(0) {
                    Text("Analytics", style = MaterialTheme.typography.headlineLarge, color = Color.White)
                }

                // Period selector
                SlideInContainer(1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(PeriodType.DAILY, PeriodType.WEEKLY, PeriodType.MONTHLY).forEach { p ->
                            val selected = st.periodType == p
                            val label = when (p) {
                                PeriodType.DAILY -> "Daily"
                                PeriodType.WEEKLY -> "Weekly"
                                PeriodType.MONTHLY -> "Monthly"
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (selected) Purple else CardBg)
                                    .clickable { vm.setPeriod(p) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    label,
                                    color = if (selected) Color.White else TextMed,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Total card
                SlideInContainer(2) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(CardBg)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Total Income", color = TextMed, fontSize = 13.sp)
                        AnimatedAmount(
                            targetCents = st.currentTotalCents,
                            currencyCode = st.currencyCode,
                            fontSize = 36.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PercentBadge(st.percent)
                            Text("vs ${formatCents(st.previousTotalCents, st.currencyCode)}", color = TextLow, fontSize = 12.sp)
                        }
                    }
                }

                // Chart type toggle
                SlideInContainer(3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(ChartType.LINE to "Line", ChartType.BAR to "Bar").forEach { (type, label) ->
                            val selected = st.chartType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (selected) Purple.copy(alpha = 0.2f) else CardBg)
                                    .border(
                                        1.dp,
                                        if (selected) Purple else Color.Transparent,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { vm.setChartType(type) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    label,
                                    color = if (selected) Purple else TextMed,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Chart
                SlideInContainer(4) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(CardBg)
                            .padding(20.dp)
                    ) {
                        Text("Trend", color = TextMed, fontSize = 13.sp)
                        Spacer(Modifier.height(12.dp))
                        when (st.chartType) {
                            ChartType.LINE -> LineChartWithLabels(points = st.chart)
                            ChartType.BAR -> BarChartWithLabels(points = st.chart)
                        }
                    }
                }

                // By source
                SlideInContainer(5) {
                    SectionHeader("By Source")
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (st.bySource.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(CardBg)
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No data for this period", color = TextMed)
                            }
                        } else {
                            val total = st.bySource.sumOf { it.totalCents }.coerceAtLeast(1L)
                            st.bySource.forEachIndexed { index, row ->
                                SlideInContainer(6 + index) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(CardBg)
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Purple.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                row.sourceName.take(1).uppercase(),
                                                color = Purple,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(row.sourceName, color = Color.White, fontWeight = FontWeight.Medium)
                                            val pct = (row.totalCents * 100f / total)
                                            LinearProgressIndicator(
                                                progress = { pct / 100f },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 6.dp)
                                                    .height(4.dp)
                                                    .clip(RoundedCornerShape(2.dp)),
                                                color = Purple,
                                                trackColor = Color(0xFF2A2A27)
                                            )
                                        }
                                        Text(
                                            formatCents(row.totalCents, st.currencyCode),
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
