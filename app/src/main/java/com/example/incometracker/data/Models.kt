package com.example.incometracker.data

import java.time.LocalDate

data class DailyTotal(val date: LocalDate, val totalCents: Long)

data class IncomeEntryWithSource(
    val id: Long,
    val sourceName: String,
    val amountCents: Long,
    val date: LocalDate,
    val note: String?,
    val createdAtEpochMillis: Long
)

data class SourceTotal(val sourceName: String, val totalCents: Long)
