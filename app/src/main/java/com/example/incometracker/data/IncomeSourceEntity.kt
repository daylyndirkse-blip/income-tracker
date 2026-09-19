package com.example.incometracker.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "income_sources",
    indices = [Index(value = ["name"], unique = true)]
)
data class IncomeSourceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)
