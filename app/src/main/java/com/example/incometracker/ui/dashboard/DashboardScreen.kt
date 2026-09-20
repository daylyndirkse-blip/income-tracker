package com.example.incometracker.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.incometracker.ui.charts.LineChartWithLabels
import com.example.incometracker.ui.components.*
import com.example.incometracker.ui.profile.ProfileViewModel
import com.example.incometracker.ui.theme.*
import com.example.incometracker.util.PeriodType
import com.example.incometracker.util.formatCents

@Composable
fun DashboardScreen(
    onAdd: () -> Unit,
    vm: DashboardViewModel = viewModel(),
    profileVm: ProfileViewModel = viewModel()
) {
    val st by vm.state.collectAsState()
    val name by profileVm.userName.collectAsState()

    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(800)
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

                // Header
                SlideInContainer(0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            InitialsAvatar(name = name.ifEmpty { "User" }, size = 48.dp)
                            Column {
                                Text("Hello 👋", color = TextMed, fontSize = 13.sp)
                                Text(
                                    name.ifEmpty { "Set your name" },
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(CardBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = TextMed)
                        }
                    }
                }

                // Total income card
                SlideInContainer(1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF2D1B69), Color(0xFF1B1B19))
                                )
                            )
                            .border(
                                1.dp,
                                Purple.copy(alpha = 0.3f),
                                RoundedCornerShape(28.dp)
                            )
                            .padding(24.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                when (st.periodType) {
                                    PeriodType.DAILY -> "Today's Income"
                                    PeriodType.WEEKLY -> "This Week's Income"
                                    PeriodType.MONTHLY -> "This Month's Income"
                                },
                                color = TextMed,
                                fontSize = 14.sp
                            )
                            AnimatedAmount(
                                targetCents = st.currentTotalCents,
                                currencyCode = st.currencyCode,
                                fontSize = 42.sp
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                PercentBadge(st.percent)
                                Text(
                                    "vs previous period",
                                    color = TextLow,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // Period selector
                SlideInContainer(2) {
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

                // Chart
                SlideInContainer(3) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(CardBg)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Income Trend", color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(
                                formatCents(st.previousTotalCents, st.currencyCode),
                                color = TextMed,
                                fontSize = 12.sp
                            )
                        }
                        LineChartWithLabels(points = st.chart)
                    }
                }

                // Quick stats
                SlideInContainer(4) {
                    SectionHeader("Quick Stats")
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            label = "Previous",
                            value = formatCents(st.previousTotalCents, st.currencyCode)
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            label = "Change",
                            value = st.percent?.let {
                                "${if (it >= 0) "+" else ""}${String.format("%.1f", it)}%"
                            } ?: "—",
                            valueColor = when {
                                st.percent == null -> TextMed
                                st.percent!! >= 0 -> Success
                                else -> Danger
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    valueColor: Color = Color.White
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(label, color = TextMed, fontSize = 12.sp)
        Text(value, color = valueColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
