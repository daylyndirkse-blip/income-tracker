package com.example.incometracker.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.incometracker.data.IncomeEntryWithSource
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
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            DayDetailsSheet(
                date = st.selectedDate,
                currencyCode = st.currencyCode,
                entries = st.entriesForSelectedDate,
                onAdd = { onAddForDate(st.selectedDate) },
                onEditEntry = onEditEntry,
                onDeleteEntry = { vm.deleteEntry(it) },
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Calendar", style = MaterialTheme.typography.titleLarge)

        val monthTitle = remember(st.visibleMonth) {
            val m = st.visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
            "$m ${st.visibleMonth.year}"
        }

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { scope.launch { calState.animateScrollToMonth(st.visibleMonth.minusMonths(1)) } }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
            }
            Text(monthTitle, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            IconButton(onClick = { scope.launch { calState.animateScrollToMonth(st.visibleMonth.plusMonths(1)) } }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
            }
        }

        WeekDaysRow(firstDow)

        HorizontalCalendar(
            state = calState,
            dayContent = { day ->
                val isInMonth = day.position == DayPosition.MonthDate
                val isSelected = isInMonth && day.date == st.selectedDate
                val total = if (isInMonth) st.totalsByDate[day.date] ?: 0L else 0L

                DayCell(
                    date = day.date,
                    inMonth = isInMonth,
                    selected = isSelected,
                    hasIncome = total > 0,
                    onClick = {
                        if (isInMonth) {
                            vm.selectDate(day.date)
                            sheetOpen = true
                        }
                    }
                )
            }
        )
    }
}

@Composable
private fun WeekDaysRow(weekStart: DayOfWeek) {
    val all = DayOfWeek.entries
    val idx = all.indexOf(weekStart)
    val days = (0..6).map { all[(idx + it) % 7] }

    Row(Modifier.fillMaxWidth()) {
        days.forEach { dow ->
            Text(
                dow.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    inMonth: Boolean,
    selected: Boolean,
    hasIncome: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .clip(shape)
            .background(
                when {
                    selected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                    else -> MaterialTheme.colorScheme.surfaceContainer
                }
            )
            .then(if (inMonth) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(8.dp)
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = if (!inMonth) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.onSurface
        )

        if (inMonth && hasIncome) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun DayDetailsSheet(
    date: LocalDate,
    currencyCode: String,
    entries: List<IncomeEntryWithSource>,
    onAdd: () -> Unit,
    onEditEntry: (Long) -> Unit,
    onDeleteEntry: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val total = entries.sumOf { it.amountCents }
    var confirmDeleteId by remember { mutableStateOf<Long?>(null) }

    if (confirmDeleteId != null) {
        AlertDialog(
            onDismissRequest = { confirmDeleteId = null },
            title = { Text("Delete entry?") },
            text = { Text("This will permanently delete the income entry.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteEntry(confirmDeleteId!!)
                    confirmDeleteId = null
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { confirmDeleteId = null }) { Text("Cancel") } }
        )
    }

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(date.toString(), style = MaterialTheme.typography.titleLarge)

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Total income", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(formatCents(total, currencyCode), style = MaterialTheme.typography.headlineSmall)
            }
        }

        Button(onClick = onAdd, modifier = Modifier.fillMaxWidth()) {
            Text("Add income for this day")
        }

        Text("Entries", style = MaterialTheme.typography.titleMedium)

        if (entries.isEmpty()) {
            Text("No income entries for this day.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries, key = { it.id }) { e ->
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(e.sourceName, style = MaterialTheme.typography.titleSmall)
                                if (!e.note.isNullOrBlank()) {
                                    Text(
                                        e.note!!,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Text(formatCents(e.amountCents, currencyCode), style = MaterialTheme.typography.titleSmall)
                            IconButton(onClick = { onEditEntry(e.id) }) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                            IconButton(onClick = { confirmDeleteId = e.id }) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
                        }
                    }
                }
            }
        }
    }
}
