package com.example.medicinereminderapp.domain.use_cases

import com.example.medicinereminderapp.domain.model.Reminder
import com.example.medicinereminderapp.domain.repository.ReminderRepository
import javax.inject.Inject

class InsertUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {

    suspend operator fun invoke(reminder: Reminder) = reminderRepository.insert(reminder)

}