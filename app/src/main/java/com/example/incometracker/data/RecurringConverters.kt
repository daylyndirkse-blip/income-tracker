package com.example.incometracker.data

import androidx.room.TypeConverter

class RecurringConverters {
    @TypeConverter
    fun fromRecurringFrequency(value: RecurringFrequency): String {
        return value.name
    }

    @TypeConverter
    fun toRecurringFrequency(value: String): RecurringFrequency {
        return RecurringFrequency.valueOf(value)
    }
}
