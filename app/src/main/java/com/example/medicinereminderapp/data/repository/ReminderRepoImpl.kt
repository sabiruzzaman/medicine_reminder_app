package com.example.medicinereminderapp.data.repository

import com.example.medicinereminderapp.data.local.ReminderDao
import com.example.medicinereminderapp.domain.model.Reminder
import com.example.medicinereminderapp.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow

class ReminderRepoImpl(
    private val reminderDao: ReminderDao
) : ReminderRepository {

    override suspend fun insert(reminder: Reminder) {
        reminderDao.insert(reminder)
    }

    override suspend fun update(reminder: Reminder) {
        reminderDao.update(reminder)
    }

    override suspend fun delete(reminder: Reminder) {
        reminderDao.delete(reminder)
    }

    override fun getAllReminders(): Flow<List<Reminder>> = reminderDao.getAllReminders()


}