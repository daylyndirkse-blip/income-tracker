package com.example.incometracker.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.incometracker.data.DatabaseProvider
import com.example.incometracker.data.IncomeEntryWithSource
import com.example.incometracker.data.SettingsStore
import com.example.incometracker.util.DateRange
import com.example.incometracker.util.WeekStart
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val visibleMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val totalsByDate: Map<LocalDate, Long> = emptyMap(),
    val entriesForSelectedDate: List<IncomeEntryWithSource> = emptyList(),
    val currencyCode: String = "USD",
    val weekStart: WeekStart = WeekStart.MONDAY
)

class CalendarViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = DatabaseProvider.get(app).incomeDao()
    private val settings = SettingsStore(app)

    private val _visibleMonth = MutableStateFlow(YearMonth.now())
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    fun setVisibleMonth(m: YearMonth) { _visibleMonth.value = m }
    fun selectDate(d: LocalDate) { _selectedDate.value = d }
    fun deleteEntry(id: Long) { viewModelScope.launch { dao.deleteEntryById(id) } }

    private val currency = settings.currencyCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "USD")

    private val weekStart = settings.weekStart
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WeekStart.MONDAY)

    private val monthTotals = _visibleMonth.flatMapLatest { ym ->
        val range = DateRange(ym.atDay(1), ym.atEndOfMonth())
        dao.observeDailyTotalsBetween(range.start, range.end)
    }.map { list -> list.associate { it.date to it.totalCents } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    private val selectedEntries = _selectedDate
        .flatMapLatest { d -> dao.observeEntriesWithSourceForDate(d) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Fix: combine max 5 at a time
    val state: StateFlow<CalendarUiState> = combine(
        combine(_visibleMonth, _selectedDate, monthTotals) { m, d, totals ->
            Triple(m, d, totals)
        },
        combine(selectedEntries, currency, weekStart) { entries, cc, ws ->
            Triple(entries, cc, ws)
        }
    ) { (m, d, totals), (entries, cc, ws) ->
        CalendarUiState(
            visibleMonth = m,
            selectedDate = d,
            totalsByDate = totals,
            entriesForSelectedDate = entries,
            currencyCode = cc,
            weekStart = ws
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CalendarUiState())
}
