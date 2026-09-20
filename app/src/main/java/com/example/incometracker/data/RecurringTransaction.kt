package com.example.incometracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_transactions")
data class RecurringTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val source: String,
    val amountCents: Long,
    val frequency: RecurringFrequency,
    val dayOfMonth: Int, // For MONTHLY: 1-31, For WEEKLY: 1-7 (Monday-Sunday)
    val isActive: Boolean = true,
    val lastProcessed: Long = 0, // Epoch day of last processing
    val createdAt: Long = System.currentTimeMillis()
)

enum class RecurringFrequency {
    DAILY,
    WEEKLY,
    MONTHLY
}
