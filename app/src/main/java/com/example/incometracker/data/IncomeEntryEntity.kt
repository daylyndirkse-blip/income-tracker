package com.example.incometracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "income_entries",
    foreignKeys = [
        ForeignKey(
            entity = IncomeSourceEntity::class,
            parentColumns = ["id"],
            childColumns = ["sourceId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = IncomeRuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["ruleId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("date"),
        Index("sourceId"),
        Index(value = ["ruleId", "date"], unique = true)
    ]
)
data class IncomeEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceId: Long,
    val amountCents: Long,
    val date: LocalDate,
    val note: String? = null,
    val ruleId: Long? = null,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)
