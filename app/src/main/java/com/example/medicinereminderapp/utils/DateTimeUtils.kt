package com.example.medicinereminderapp.utils


import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {

    fun formatDate(d: Date): String =
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(d)

    fun formatTime(d: Date): String =
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(d)

    fun formatDateTime(d: Date): String =
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(d)


    fun combine(
        year: Int, monthZeroBased: Int, day: Int,
        hour24: Int, minute: Int
    ): Date = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, monthZeroBased)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, hour24)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time


    fun isPast(d: Date): Boolean = d.before(Date())

    /** If date is in the past and repeat=true, roll forward by 1 day; else return as-is. */
    fun requireFutureOrRoll(d: Date, repeatDaily: Boolean): Date {
        if (!repeatDaily || !isPast(d)) return d
        val cal = Calendar.getInstance().apply { time = d }
        while (isPast(cal.time)) cal.add(Calendar.DATE, 1)
        return cal.time
    }
}
