package com.example.medicinereminderapp.domain.repository

import com.example.medicinereminderapp.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {

    suspend fun insert(reminder: Reminder)

    suspend fun delete(reminder: Reminder)

    suspend fun update(reminder: Reminder)

    fun getAllReminders(): Flow<List<Reminder>>

}