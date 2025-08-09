package com.example.medicinereminderapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val dosage: String,
    val reminderDateTime: Date,
    val isTaken: Boolean = false,
)
