package com.example.medicinereminderapp.presentation.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinereminderapp.databinding.ItemReminderBinding
import com.example.medicinereminderapp.domain.model.Reminder
import com.example.medicinereminderapp.utils.DateTimeUtils

class MedicineReminderAdapter(
    private val onEditReminder: (Reminder) -> Unit,
    private val onDeleteReminder: (Reminder) -> Unit
) : ListAdapter<Reminder, MedicineReminderAdapter.MedicineReminderViewHolder>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Reminder>() {
            override fun areItemsTheSame(o: Reminder, n: Reminder) = o.id == n.id
            override fun areContentsTheSame(o: Reminder, n: Reminder) = o == n
        }
    }

    inner class MedicineReminderViewHolder(val binding: ItemReminderBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineReminderViewHolder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicineReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicineReminderViewHolder, position: Int) {
        val reminder = getItem(position)

        holder.binding.apply {
            reminderName.text = reminder.name
            reminderDosage.text = reminder.dosage
            reminderDate.text = DateTimeUtils.formatDate(reminder.reminderDateTime)
            reminderTime.text = DateTimeUtils.formatTime(reminder.reminderDateTime)

            editReminderButton.setOnClickListener { onEditReminder(reminder) }
            deleteReminderButton.setOnClickListener { onDeleteReminder(reminder) }
        }
    }
}