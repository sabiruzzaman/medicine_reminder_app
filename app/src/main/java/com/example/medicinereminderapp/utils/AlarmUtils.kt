package com.example.medicinereminderapp.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.medicinereminderapp.domain.model.Reminder
import com.example.medicinereminderapp.presentation.common.ReminderReceiver
import com.google.gson.Gson
import android.os.Build
import android.provider.Settings

const val REMINDER = "REMINDER"


fun setUpAlarm(context: Context, reminder: Reminder) {
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra(REMINDER, Gson().toJson(reminder))
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context, reminder.id,
        intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    try {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            reminder.reminderDateTime.time,
            pendingIntent
        )
    } catch (e: SecurityException) {
        Log.e("TAG", "SecurityException: Permission missing to set exact alarm", e)
    } catch (e: Exception) {
        Log.e("TAG", "Failed to set alarm for reminderId=${reminder.id}", e)
    }
}

fun cancelAlarm(context: Context, reminder: Reminder) {
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra(REMINDER, Gson().toJson(reminder))
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context, reminder.id,
        intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    try {
        alarmManager.cancel(pendingIntent)
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}

/**
 * Requests SCHEDULE_EXACT_ALARM permission on Android 12+ if needed.
 */
fun checkAndRequestExactAlarmPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
            // Permission not granted, redirect user to settings
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            return false
        }
    }
    // permission already granted or OS version < 12
    return true
}

fun setUpAlarmWithPermissionCheck(context: Context, reminder: Reminder) {
    if (!checkAndRequestExactAlarmPermission(context)) {
        // permission not granted, do not set the alarm
        return
    }
    // permission granted, proceed to set the alarm
    setUpAlarm(context, reminder)
}
