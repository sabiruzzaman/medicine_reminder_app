package com.example.medicinereminderapp.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinereminderapp.R
import com.example.medicinereminderapp.databinding.ItemReminderBinding
import com.example.medicinereminderapp.domain.model.Reminder
import com.example.medicinereminderapp.utils.DateTimeUtils

class MedicineReminderAdapter(
    private val onEditReminder: (Reminder) -> Unit,
    private val onDeleteReminder: (Reminder) -> Unit
) : RecyclerView.Adapter<MedicineReminderAdapter.MedicineReminderViewHolder>() {

    private var reminderList: List<Reminder> = emptyList()

    inner class MedicineReminderViewHolder(val binding: ItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineReminderViewHolder {
        val binding =
            ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicineReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicineReminderViewHolder, position: Int) {
        val reminder = reminderList[position]

        holder.binding.apply {
            reminderName.text = reminder.name
            reminderDosage.text = reminder.dosage
            reminderDate.text = DateTimeUtils.formatDate(reminder.reminderDateTime)
            reminderTime.text = DateTimeUtils.formatTime(reminder.reminderDateTime)

            // Set the background color based on the reminder status
            if (reminder.isTaken) {
                remainderCard.setCardBackgroundColor(root.context.getColor(R.color.green))
            } else {
                remainderCard.setCardBackgroundColor(root.context.getColor(R.color.red))
            }

            editReminderButton.setOnClickListener { onEditReminder(reminder) }
            deleteReminderButton.setOnClickListener { onDeleteReminder(reminder) }


        }
    }

    override fun getItemCount(): Int = reminderList.size


    fun submitList(newList: List<Reminder>) {
        val diffCallback = ReminderDiffCallback(reminderList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        reminderList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class ReminderDiffCallback(
        private val oldList: List<Reminder>,
        private val newList: List<Reminder>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}