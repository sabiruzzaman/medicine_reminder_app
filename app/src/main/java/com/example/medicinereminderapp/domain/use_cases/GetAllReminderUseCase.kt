package com.example.medicinereminderapp.domain.use_cases

import com.example.medicinereminderapp.domain.repository.ReminderRepository
import javax.inject.Inject

class GetAllReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {

    operator fun invoke() = reminderRepository.getAllReminders()

}