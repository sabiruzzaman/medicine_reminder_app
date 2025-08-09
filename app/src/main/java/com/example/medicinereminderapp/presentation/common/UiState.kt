package com.example.medicinereminderapp.presentation.common


import com.example.medicinereminderapp.domain.model.Reminder

data class UiState(
    val isLoading: Boolean = false,
    val data: List<Reminder> = emptyList(),
    val error: String? = null
)
