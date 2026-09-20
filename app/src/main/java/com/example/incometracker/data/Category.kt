package com.example.incometracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String, // Icon name from Material Icons
    val color: String, // Hex color code
    val type: TransactionType, // INCOME or EXPENSE
    val isDefault: Boolean = false, // True for built-in categories
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType {
    INCOME,
    EXPENSE
}
