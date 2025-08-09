package com.example.medicinereminderapp.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import android.widget.TextView
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateTimePickerHelper(private val context: Context) {
    var year: Int? = null
    var month0: Int? = null // 0-based
    var day: Int? = null
    var hour24: Int? = null
    var minute: Int? = null

    fun pickDate(target: TextView) {
        val now = Calendar.getInstance()
        val y = year ?: now.get(Calendar.YEAR)
        val m0 = month0 ?: now.get(Calendar.MONTH)
        val d = day ?: now.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(context, { _, yy, mm0, dd ->
            year = yy; month0 = mm0; day = dd
            target.text = String.format(Locale.getDefault(), "%02d-%02d-%04d", dd, mm0 + 1, yy)
        }, y, m0, d).show()
    }

    fun pickTime(target: TextView, is24Hour: Boolean = false) {
        val now = Calendar.getInstance()
        val hh = hour24 ?: now.get(Calendar.HOUR_OF_DAY)
        val mm = minute ?: now.get(Calendar.MINUTE)
        TimePickerDialog(context, { _, h, m ->
            hour24 = h; minute = m
            val tmp = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, h); set(Calendar.MINUTE, m) }
            target.text = DateFormat.format("hh:mm a", tmp)
        }, hh, mm, is24Hour).show()
    }

    fun getSelectedDate(): Date? {
        if (year == null || month0 == null || day == null || hour24 == null || minute == null) return null
        return DateTimeUtils.combine(year!!, month0!!, day!!, hour24!!, minute!!)
    }

    fun setFrom(date: Date) {
        val cal = Calendar.getInstance().apply { time = date }
        year = cal.get(Calendar.YEAR)
        month0 = cal.get(Calendar.MONTH)
        day = cal.get(Calendar.DAY_OF_MONTH)
        hour24 = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
    }
}
