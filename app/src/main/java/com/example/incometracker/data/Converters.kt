package com.example.incometracker.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter fun localDateToEpochDay(d: LocalDate?): Long? = d?.toEpochDay()
    @TypeConverter fun epochDayToLocalDate(v: Long?): LocalDate? = v?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter fun recurrenceToString(r: Recurrence?): String? = r?.name
    @TypeConverter fun stringToRecurrence(v: String?): Recurrence? = v?.let { Recurrence.valueOf(it) }
}
