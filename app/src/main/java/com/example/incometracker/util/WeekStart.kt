package com.example.incometracker.util

import java.time.DayOfWeek

enum class WeekStart { MONDAY, SUNDAY }

fun WeekStart.toDayOfWeek(): DayOfWeek = when (this) {
    WeekStart.MONDAY -> DayOfWeek.MONDAY
    WeekStart.SUNDAY -> DayOfWeek.SUNDAY
}
