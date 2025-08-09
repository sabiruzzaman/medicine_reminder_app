package com.example.medicinereminderapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinereminderapp.domain.model.Reminder
import com.example.medicinereminderapp.domain.use_cases.DeleteUseCase
import com.example.medicinereminderapp.domain.use_cases.GetAllReminderUseCase
import com.example.medicinereminderapp.domain.use_cases.InsertUseCase
import com.example.medicinereminderapp.domain.use_cases.UpdateUseCase
import com.example.medicinereminderapp.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineReminderViewModel @Inject constructor(
    private val insertUseCase: InsertUseCase,
    private val updateUseCase: UpdateUseCase,
    private val deleteUseCase: DeleteUseCase,
    private val getAllReminderUseCase: GetAllReminderUseCase
) : ViewModel() {

    private val _state = MutableLiveData(UiState())
    val state: LiveData<UiState> = _state

    init {
        observeReminders()
    }

    private fun observeReminders() {
        viewModelScope.launch {
            getAllReminderUseCase()
                .onStart { _state.postValue(_state.value!!.copy(isLoading = true)) }
                .catch { e -> _state.postValue(UiState(error = e.message)) }
                .collect { list -> _state.postValue(UiState(data = list)) }
        }
    }

    fun saveReminder(reminder: Reminder) = viewModelScope.launch {
        insertUseCase.invoke(reminder)
    }

    fun update(reminder: Reminder) = viewModelScope.launch {
        updateUseCase.invoke(reminder)
    }

    fun delete(reminder: Reminder) = viewModelScope.launch {
        deleteUseCase.invoke(reminder)
    }
}
