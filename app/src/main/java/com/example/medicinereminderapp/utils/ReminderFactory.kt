package com.example.medicinereminderapp.utils

import com.example.medicinereminderapp.domain.model.Reminder
import java.util.Date

object ReminderFactory {
    fun newOrEdit(
        id: Int?,            // null or 0 means "new"
        name: String,
        dosage: String,
        date: Date,
        isTaken: Boolean = false
    ): Reminder = Reminder(
        id = id ?: 0,
        name = name,
        dosage = dosage,
        reminderDateTime = date,
        isTaken = isTaken
    )
}
