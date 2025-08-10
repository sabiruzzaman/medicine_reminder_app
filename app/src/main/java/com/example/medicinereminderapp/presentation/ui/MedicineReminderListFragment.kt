package com.example.medicinereminderapp.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.medicinereminderapp.R
import com.example.medicinereminderapp.databinding.FragmentMedicineReminderListBinding
import com.example.medicinereminderapp.domain.model.Reminder
import com.example.medicinereminderapp.presentation.view_model.MedicineReminderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicineReminderListFragment : Fragment(R.layout.fragment_medicine_reminder_list) {

    private var _binding: FragmentMedicineReminderListBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MedicineReminderViewModel>()

    private lateinit var adapter: MedicineReminderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMedicineReminderListBinding.bind(view)

        setupRecycler()
        setupClicks()
        observeState()
    }

    private fun setupRecycler() = with(binding) {
        adapter = MedicineReminderAdapter(
            onEditReminder = { reminder -> navigateToAddEdit(reminder) },
            onDeleteReminder = { reminder -> viewModel.delete(reminder) }
        )
        reminderRecyclerView.adapter = adapter

    }

    private fun setupClicks() = with(binding) {
        addReminderButton.setOnClickListener {
            // navigate to add screen (no existing reminder)
            findNavController().navigate(
                R.id.action_medicineReminderListFragment_to_addOrEditMedicineReminderFragment
            )
        }
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { ui ->
            val list = ui.data
            adapter.submitList(list)

            // Update UI based on the list state
            binding.apply {
                if (list.isEmpty()) {
                    noRemindersText.visibility = View.VISIBLE
                    reminderRecyclerView.visibility = View.GONE
                } else {
                    noRemindersText.visibility = View.GONE
                    reminderRecyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun navigateToAddEdit(reminder: Reminder) {
        val bundle = Bundle().apply {
            putInt("reminder_id", reminder.id)
        }
        findNavController().navigate(
            R.id.action_medicineReminderListFragment_to_addOrEditMedicineReminderFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}