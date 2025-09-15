package com.example.medicinereminderapp.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.example.medicinereminderapp.domain.model.Reminder
import com.example.medicinereminderapp.domain.model.ReminderMode
import com.example.medicinereminderapp.presentation.common.ReminderReceiver
import com.google.gson.Gson

const val REMINDER = "REMINDER"
private const val TAG = "AlarmUtils"

fun setUpAlarm(context: Context, reminder: Reminder) {
    // ⛔ Don’t schedule anything for SILENT reminders
    if (reminder.mode == ReminderMode.SILENT) {
        Log.d(TAG, "Skip scheduling: reminder ${reminder.id} is SILENT")
        return
    }

    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra(REMINDER, Gson().toJson(reminder)) // includes updated mode
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminder.id, // stable per reminder
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val triggerAtMillis = reminder.reminderDateTime.time

    try {
        // Prefer doze-aware exact alarm when available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
        Log.d(TAG, "Scheduled alarm for reminder ${reminder.id} at $triggerAtMillis (mode=${reminder.mode})")
    } catch (e: SecurityException) {
        Log.e(TAG, "SecurityException: exact alarm permission missing", e)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to set alarm for reminderId=${reminder.id}", e)
    }
}

fun cancelAlarm(context: Context, reminder: Reminder) {
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra(REMINDER, Gson().toJson(reminder))
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminder.id,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    try {
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Canceled alarm for reminder ${reminder.id}")
    } catch (e: SecurityException) {
        Log.e(TAG, "SecurityException canceling alarm", e)
    }
}

/**
 * Requests SCHEDULE_EXACT_ALARM permission on Android 12+ if needed.
 * Returns true if we can schedule exact alarms.
 */
fun checkAndRequestExactAlarmPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val canSchedule = alarmManager?.canScheduleExactAlarms() == true
        if (!canSchedule) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Log.w(TAG, "Requesting exact alarm permission")
            return false
        }
    }
    return true
}

fun setUpAlarmWithPermissionCheck(context: Context, reminder: Reminder) {
    // ⛔ Don’t schedule anything for SILENT reminders
    if (reminder.mode == ReminderMode.SILENT) {
        Log.d(TAG, "Skip scheduling (permission check path): reminder ${reminder.id} is SILENT")
        return
    }
    if (!checkAndRequestExactAlarmPermission(context)) return
    setUpAlarm(context, reminder)
}
