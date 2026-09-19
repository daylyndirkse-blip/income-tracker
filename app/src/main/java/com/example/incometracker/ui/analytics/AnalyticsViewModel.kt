package com.example.incometracker.ui.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.incometracker.data.DatabaseProvider
import com.example.incometracker.data.SettingsStore
import com.example.incometracker.data.SourceTotal
import com.example.incometracker.util.*
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

data class AnalyticsState(
    val periodType: PeriodType = PeriodType.MONTHLY,
    val chartType: ChartType = ChartType.LINE,
    val currencyCode: String = "USD",
    val currentTotalCents: Long = 0L,
    val previousTotalCents: Long = 0L,
    val percent: Double? = null,
    val chart: List<Pair<String, Long>> = emptyList(),
    val bySource: List<SourceTotal> = emptyList()
)

class AnalyticsViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = DatabaseProvider.get(app).incomeDao()
    private val settings = SettingsStore(app)

    private val _period = MutableStateFlow(PeriodType.MONTHLY)
    private val _chartType = MutableStateFlow(ChartType.LINE)
    private val today = MutableStateFlow(LocalDate.now())

    fun setPeriod(p: PeriodType) { _period.value = p }
    fun setChartType(t: ChartType) { _chartType.value = t }

    private val currency = settings.currencyCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "USD")

    private val weekStart = settings.weekStart
        .map { it.toDayOfWeek() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), java.time.DayOfWeek.MONDAY)

    private val currentTotal = combine(_period, today, weekStart) { p, t, ws ->
        currentRange(p, t, ws)
    }.flatMapLatest { r -> dao.observeTotalBetween(r.start, r.end) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    private val previousTotal = combine(_period, today, weekStart) { p, t, ws ->
        previousRange(p, t, ws)
    }.flatMapLatest { r -> dao.observeTotalBetween(r.start, r.end) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    private val bySource = combine(_period, today, weekStart) { p, t, ws ->
        currentRange(p, t, ws)
    }.flatMapLatest { r -> dao.observeTotalsBySourceBetween(r.start, r.end) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val chart = combine(_period, today, weekStart) { p, t, ws ->
        val end = t
        val start = when (p) {
            PeriodType.DAILY -> end.minusDays(29)
            PeriodType.WEEKLY -> end.minusWeeks(11).with(TemporalAdjusters.previousOrSame(ws))
            PeriodType.MONTHLY -> end.minusMonths(11).withDayOfMonth(1)
        }
        DateRange(start, end)
    }.flatMapLatest { r -> dao.observeDailyTotalsBetween(r.start, r.end) }
        .combine(_period) { daily, p ->
            when (p) {
                PeriodType.DAILY -> daily.map {
                    "%02d-%02d".format(it.date.monthValue, it.date.dayOfMonth) to it.totalCents
                }
                PeriodType.WEEKLY -> {
                    val map = linkedMapOf<LocalDate, Long>()
                    for (d in daily) {
                        val ws = d.date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                        map[ws] = (map[ws] ?: 0L) + d.totalCents
                    }
                    map.entries.map { (k, v) ->
                        "Wk %02d".format(k.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR)) to v
                    }
                }
                PeriodType.MONTHLY -> {
                    val map = linkedMapOf<YearMonth, Long>()
                    for (d in daily) {
                        val ym = YearMonth.from(d.date)
                        map[ym] = (map[ym] ?: 0L) + d.totalCents
                    }
                    map.entries.map { (ym, v) ->
                        ym.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() } to v
                    }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Fix: combine 5 flows (max 5 params without explicit type issues)
    val state: StateFlow<AnalyticsState> = combine(
        combine(_period, _chartType, currency) { p, ct, cc ->
            Triple(p, ct, cc)
        },
        combine(currentTotal, previousTotal) { cur, prev ->
            Pair(cur, prev)
        },
        combine(chart, bySource) { ch, src ->
            Pair(ch, src)
        }
    ) { (p, ct, cc), (cur, prev), (ch, src) ->
        AnalyticsState(
            periodType = p,
            chartType = ct,
            currencyCode = cc,
            currentTotalCents = cur,
            previousTotalCents = prev,
            percent = percentChange(cur, prev),
            chart = ch,
            bySource = src
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AnalyticsState())
}
