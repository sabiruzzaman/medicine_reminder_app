package com.example.medicinereminderapp.utils

import com.example.medicinereminderapp.domain.model.Reminder
import com.example.medicinereminderapp.domain.model.ReminderMode
import java.util.Date

object ReminderFactory {
    fun newOrEdit(
        id: Int?, // null or 0 means "new"
        name: String,
        dosage: String,
        date: Date,
        isTaken: Boolean = false,
        mode: ReminderMode = ReminderMode.SOUND // default
    ): Reminder = Reminder(
        id = id ?: 0,
        name = name,
        dosage = dosage,
        reminderDateTime = date,
        isTaken = isTaken,
        mode = mode
    )
}
