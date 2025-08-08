package com.example.medicinereminderapp.domain.model

import androidx.room.Entity

@Entity
data class Reminder(
    val name: String,
    val dosage: String,
    @androidx.room.PrimaryKey(autoGenerate = false)
    val timeInMillis: Long,
    val isTaken: Boolean = false,
    val isSnooze: Boolean = false
)
