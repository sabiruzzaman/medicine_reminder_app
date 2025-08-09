package com.example.medicinereminderapp.data.local

import androidx.room.TypeConverter
import java.util.Date

object DateConverter {
    @TypeConverter
    @JvmStatic
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    @JvmStatic
    fun toDate(millis: Long?): Date? = millis?.let { Date(it) }
}