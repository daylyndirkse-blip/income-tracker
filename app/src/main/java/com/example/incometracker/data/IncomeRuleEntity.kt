package com.example.incometracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "income_rules",
    foreignKeys = [
        ForeignKey(
            entity = IncomeSourceEntity::class,
            parentColumns = ["id"],
            childColumns = ["sourceId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("sourceId")]
)
data class IncomeRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceId: Long,
    val amountCents: Long,
    val recurrence: Recurrence,
    val anchorDate: LocalDate,
    val note: String? = null,
    val active: Boolean = true,
    val lastGeneratedDate: LocalDate? = null
)
