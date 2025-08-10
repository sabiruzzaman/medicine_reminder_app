package com.example.medicinereminderapp.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.medicinereminderapp.R
import com.example.medicinereminderapp.databinding.FragmentAddOrEditMedicineReminderBinding
import com.example.medicinereminderapp.domain.model.Reminder
import com.example.medicinereminderapp.presentation.view_model.MedicineReminderViewModel
import com.example.medicinereminderapp.utils.DateTimePickerHelper
import com.example.medicinereminderapp.utils.DateTimeUtils
import com.example.medicinereminderapp.utils.FormUtils
import com.example.medicinereminderapp.utils.ReminderFactory
import com.example.medicinereminderapp.utils.setUpAlarmWithPermissionCheck
import com.example.medicinereminderapp.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class AddOrEditMedicineReminderFragment
    : Fragment(R.layout.fragment_add_or_edit_medicine_reminder) {

    private var _binding: FragmentAddOrEditMedicineReminderBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MedicineReminderViewModel>()
    private lateinit var dt: DateTimePickerHelper

    private val editingId: Int? by lazy {
        arguments?.getInt(ARG_REMINDER_ID)?.takeIf { it != 0 }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddOrEditMedicineReminderBinding.bind(view)
        dt = DateTimePickerHelper(requireContext())

        setupTopBar()
        setupPickers()
        bindForEditValues()
        setupSave()
    }

    private fun setupTopBar() = with(binding) {
        title.text = if (editingId == null)
            getString(R.string.add_medicine_reminder)
        else
            getString(R.string.edit_medicine_reminder)
        backButton.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupPickers() = with(binding) {
        reminderDate.setOnClickListener { dt.pickDate(reminderDate) }
        reminderTime.setOnClickListener { dt.pickTime(reminderTime, is24Hour = false) }
    }

    private fun bindForEditValues() {
        val id = editingId ?: return
        viewModel.state.observe(viewLifecycleOwner) { ui ->
            val r = ui.data.firstOrNull { it.id == id } ?: return@observe
            // Texts
            binding.medicineNameEditText.setText(r.name)
            binding.medicineDosageEditText.setText(r.dosage)
            // Date-time
            dt.setFrom(r.reminderDateTime)
            binding.reminderDate.text = DateTimeUtils.formatDate(r.reminderDateTime)
            binding.reminderTime.text = DateTimeUtils.formatTime(r.reminderDateTime)
        }
    }

    private fun setupSave() = with(binding) {
        saveReminder.setOnClickListener {
            // clear old errors
            medicineNameInputLayout.error = null
            medicineDosageInputLayout.error = null

            val name = medicineNameEditText.text?.toString()?.trim().orEmpty()
            val dosage = medicineDosageEditText.text?.toString()?.trim().orEmpty()

            val okName = FormUtils.required(medicineNameInputLayout, name, getString(R.string.required_field))
            val okDose = FormUtils.required(medicineDosageInputLayout, dosage, getString(R.string.required_field))
            if (!okName || !okDose) return@setOnClickListener

            val pickedDate: Date? = dt.getSelectedDate()
            val okDate = FormUtils.validateDate(
                selected = pickedDate,
                requireFuture = true,
                msgPick = { getString(R.string.pick_date_time) },
                onError = { requireContext().toast(it) }
            )
            if (!okDate) return@setOnClickListener

            // Optional roll-forward helper if we add repeat later
            val finalDate = DateTimeUtils.requireFutureOrRoll(pickedDate!!, repeatDaily = false)

            val reminder: Reminder = ReminderFactory.newOrEdit(
                id = editingId,
                name = name,
                dosage = dosage,
                date = finalDate
            )

            if (editingId == null) {
                viewModel.saveReminder(reminder)
            } else {
                viewModel.update(reminder)
            }

             // Set up the alarm
            setUpAlarmWithPermissionCheck(requireContext(), reminder)
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_REMINDER_ID = "reminder_id"
    }
}