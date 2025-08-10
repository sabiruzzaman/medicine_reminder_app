package com.example.medicinereminderapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medicinereminderapp.domain.model.Reminder
import com.example.medicinereminderapp.domain.use_cases.InsertUseCase
import com.example.medicinereminderapp.domain.use_cases.GetAllReminderUseCase
import com.example.medicinereminderapp.domain.use_cases.UpdateUseCase
import com.example.medicinereminderapp.domain.use_cases.DeleteUseCase
import com.example.medicinereminderapp.presentation.view_model.MedicineReminderViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*

import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class MedicineReminderViewModelTest {

    @get:Rule val instantRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()

    private lateinit var insert: InsertUseCase
    private lateinit var getAll: GetAllReminderUseCase
    private lateinit var update: UpdateUseCase
    private lateinit var delete: DeleteUseCase

    private lateinit var vm: MedicineReminderViewModel



    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        insert = mockk(relaxed = true)
        update = mockk(relaxed = true)
        delete = mockk(relaxed = true)
        // expose a simple flow with one item
        val sample = listOf(Reminder(
            id = 1,
            name = "Test",
            dosage = "1 tab",
            reminderDateTime = java.util.Date(),
            isTaken = false
        ))


        getAll = mockk<GetAllReminderUseCase> {
            // invoke() is NOT suspend when it returns Flow → use `every`
            every { this@mockk.invoke() } returns flowOf(sample)
        }

        vm = MedicineReminderViewModel(insert, update, delete, getAll)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load list populates ui state with data and no error`() = runTest {
        // Let init{} collectors finish
        dispatcher.scheduler.advanceUntilIdle()

        val state = vm.state.value  // StateFlow<UiState>

        Assert.assertFalse(state.isLoading)
        Assert.assertNull(state.error)
        Assert.assertEquals(1, state.data.size)
        Assert.assertEquals("Test", state.data.first().name)
    }


    @Test
    fun `save reminder calls insert use case`() = runTest {
        val newR = Reminder(
            id = 2,
            name = "New Reminder",
            dosage = "2 tabs",
            reminderDateTime = java.util.Date(),
            isTaken = false
        )
        coEvery { insert.invoke(newR) } returns Unit

        vm.saveReminder(newR)
        dispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { insert.invoke(newR) }
    }


}