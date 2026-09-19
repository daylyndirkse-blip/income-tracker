package com.example.incometracker.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.incometracker.data.IncomeEntryWithSource
import com.example.incometracker.ui.components.*.AnimatedAmount
import com.example.incometracker.ui.theme.*
import com.example.incometracker.util.formatCents
import com.example.incometracker.util.toDayOfWeek
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onAddForDate: (LocalDate) -> Unit,
    onEditEntry: (Long) -> Unit,
    vm: CalendarViewModel = viewModel()
) {
    val st by vm.state.collectAsState()
    val scope = rememberCoroutineScope()

    val startMonth = remember { YearMonth.now().minusMonths(24) }
    val endMonth = remember { YearMonth.now().plusMonths(24) }
    val firstDow = st.weekStart.toDayOfWeek()

    val calState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = st.visibleMonth,
        firstDayOfWeek = firstDow
    )

    LaunchedEffect(calState.firstVisibleMonth.yearMonth) {
        vm.setVisibleMonth(calState.firstVisibleMonth.yearMonth)
    }

    var sheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (sheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { sheetOpen = false },
            sheetState = sheetState,
            containerColor = CardBg,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            DayDetailsSheet(
                date = st.selectedDate,
                currencyCode = st.currencyCode,
                entries = st.entriesForSelectedDate,
                onAdd = { onAddForDate(st.selectedDate) },
                onEditEntry = onEditEntry,
                onDeleteEntry = { vm.deleteEntry(it) }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        SlideInContainer(0) {
            Text("Calendar", style = MaterialTheme.typography.headlineLarge, color = Color.White)
        }

        // Month header
        SlideInContainer(1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardBg)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    scope.launch { calState.animateScrollToMonth(st.visibleMonth.minusMonths(1)) }
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = TextMed)
                }
                Text(
                    "${st.visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${st.visibleMonth.year}",
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                IconButton(onClick = {
                    scope.launch { calState.animateScrollToMonth(st.visibleMonth.plusMonths(1)) }
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextMed)
                }
            }
        }

        // Weekday labels
        Row(Modifier.fillMaxWidth()) {
            val all = DayOfWeek.entries
            val idx = all.indexOf(firstDow)
            (0..6).map { all[(idx + it) % 7] }.forEach { dow ->
                Text(
                    dow.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    modifier = Modifier.weight(1f),
                    color = TextLow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Calendar grid
        HorizontalCalendar(
            state = calState,
            dayContent = { day ->
                val isInMonth = day.position == DayPosition.MonthDate
                val isSelected = isInMonth && day.date == st.selectedDate
                val isToday = isInMonth && day.date == LocalDate.now()
                val total = if (isInMonth) st.totalsByDate[day.date] ?: 0L else 0L

                Column(
                    modifier = Modifier
                        .padding(3.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            when {
                                isSelected -> Purple
                                isToday -> Purple.copy(alpha = 0.2f)
                                else -> CardBg
                            }
                        )
                        .then(if (isInMonth) Modifier.clickable {
                            vm.selectDate(day.date)
                            sheetOpen = true
                        } else Modifier)
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = day.date.dayOfMonth.toString(),
                        color = when {
                            !isInMonth -> TextLow
                            isSelected -> Color.White
                            else -> Color.White
                        },
                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                    if (isInMonth && total > 0) {
                        Spacer(Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White else Purple)
                        )
                    }
                }
            }
        )

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun DayDetailsSheet(
    date: LocalDate,
    currencyCode: String,
    entries: List<IncomeEntryWithSource>,
    onAdd: () -> Unit,
    onEditEntry: (Long) -> Unit,
    onDeleteEntry: (Long) -> Unit
) {
    val total = entries.sumOf { it.amountCents }
    var confirmDeleteId by remember { mutableStateOf<Long?>(null) }

    if (confirmDeleteId != null) {
        AlertDialog(
            onDismissRequest = { confirmDeleteId = null },
            containerColor = CardBg,
            title = { Text("Delete entry?", color = Color.White) },
            text = { Text("This income entry will be permanently deleted.", color = TextMed) },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteEntry(confirmDeleteId!!)
                    confirmDeleteId = null
                }) { Text("Delete", color = Danger) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteId = null }) {
                    Text("Cancel", color = TextMed)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Date + total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    date.format(java.time.format.DateTimeFormatter.ofPattern("EEEE")),
                    color = TextMed,
                    fontSize = 13.sp
                )
                Text(
                    date.format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy")),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            AnimatedAmount(targetCents = total, currencyCode = currencyCode, fontSize = 22.sp)
        }

        // Add button
        Button(
            onClick = onAdd,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Add Income", fontWeight = FontWeight.SemiBold)
        }

        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBg2)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No income entries for this day", color = TextMed, fontSize = 14.sp)
            }
        } else {
            Text("Entries", color = TextMed, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                items(entries, key = { it.id }) { e ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardBg2)
                            .padding(14.dp),
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
                                e.sourceName.take(1).uppercase(),
                                color = Purple,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(e.sourceName, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            if (!e.note.isNullOrBlank()) {
                                Text(e.note!!, color = TextMed, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        Text(formatCents(e.amountCents, currencyCode), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        IconButton(onClick = { onEditEntry(e.id) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextMed, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = { confirmDeleteId = e.id }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Danger, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
