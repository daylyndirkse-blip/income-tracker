package com.example.incometracker.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

data class DateRange(val start: LocalDate, val end: LocalDate)

fun currentRange(
    type: PeriodType,
    today: LocalDate = LocalDate.now(),
    weekStart: DayOfWeek = DayOfWeek.MONDAY
): DateRange = when (type) {
    PeriodType.DAILY -> DateRange(today, today)
    PeriodType.WEEKLY -> {
        val start = today.with(TemporalAdjusters.previousOrSame(weekStart))
        DateRange(start, start.plusDays(6))
    }
    PeriodType.MONTHLY -> {
        val ym = YearMonth.from(today)
        DateRange(ym.atDay(1), ym.atEndOfMonth())
    }
}

fun previousRange(
    type: PeriodType,
    today: LocalDate = LocalDate.now(),
    weekStart: DayOfWeek = DayOfWeek.MONDAY
): DateRange {
    val cur = currentRange(type, today, weekStart)
    return when (type) {
        PeriodType.DAILY -> DateRange(cur.start.minusDays(1), cur.end.minusDays(1))
        PeriodType.WEEKLY -> DateRange(cur.start.minusWeeks(1), cur.end.minusWeeks(1))
        PeriodType.MONTHLY -> {
            val prev = YearMonth.from(cur.start).minusMonths(1)
            DateRange(prev.atDay(1), prev.atEndOfMonth())
        }
    }
}
