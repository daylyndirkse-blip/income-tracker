package com.example.incometracker.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.abs

fun parseAmountToCents(input: String): Long? {
    val s = input.trim().replace(",", ".")
    if (s.isEmpty()) return null
    val parts = s.split(".")
    if (parts.size > 2) return null

    val whole = parts[0].ifEmpty { "0" }.toLongOrNull() ?: return null
    val frac = if (parts.size == 2) parts[1] else ""
    val frac2 = when (frac.length) {
        0 -> 0
        1 -> (frac[0].digitToIntOrNull() ?: return null) * 10
        else -> {
            val a = frac[0].digitToIntOrNull() ?: return null
            val b = frac[1].digitToIntOrNull() ?: return null
            a * 10 + b
        }
    }
    return whole * 100 + frac2
}

fun formatCents(cents: Long, currencyCode: String): String {
    val fmt = NumberFormat.getCurrencyInstance(Locale.getDefault())
    fmt.currency = Currency.getInstance(currencyCode)
    return fmt.format(cents / 100.0)
}

fun percentChange(current: Long, previous: Long): Double? {
    if (previous == 0L) return null
    return ((current - previous).toDouble() / abs(previous).toDouble()) * 100.0
}
