package com.example.medicinereminderapp.presentation.common

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.medicinereminderapp.CHANNEL
import com.example.medicinereminderapp.R
import com.example.medicinereminderapp.domain.model.Reminder
import com.example.medicinereminderapp.domain.model.ReminderMode
import com.example.medicinereminderapp.domain.use_cases.UpdateUseCase
import com.example.medicinereminderapp.utils.AlarmSound
import com.example.medicinereminderapp.utils.REMINDER
import com.example.medicinereminderapp.utils.VibratorHelper
import com.example.medicinereminderapp.utils.cancelAlarm
import com.example.medicinereminderapp.utils.setUpAlarm
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.Date
import javax.inject.Inject


const val IS_TAKEN = "IS_TAKEN"
const val SNOOZE = "SNOOZE"

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {




    @Inject
    lateinit var updateUseCase: UpdateUseCase

    override fun onReceive(context: Context, intent: Intent) {
        val reminderJson = intent.getStringExtra(REMINDER) ?: return
        val reminder = try {
            Gson().fromJson(reminderJson, Reminder::class.java)
        } catch (_: Exception) {
            return
        }

        when (intent.action) {





            IS_TAKEN -> {
                VibratorHelper.from(context)?.stop()


                // stop sound, mark taken, dismiss and cancel alarm
                AlarmSound.stop()
                runBlocking { updateUseCase(reminder.copy(isTaken = true)) }
                NotificationManagerCompat.from(context).cancel(reminder.id)
                cancelAlarm(context, reminder)
                return
            }

            SNOOZE -> {

                VibratorHelper.from(context)?.stop()
                // stop current ring, dismiss, reschedule +2 minutes, keep not taken
                AlarmSound.stop()
                NotificationManagerCompat.from(context).cancel(reminder.id)

                val newTime = System.currentTimeMillis() + 2L * 60L * 1000L
                val snoozed = reminder.copy(
                    reminderDateTime = Date(newTime), isTaken = false
                )

                runBlocking { updateUseCase(snoozed) }
                setUpAlarm(context, snoozed)
                return
            }

            else -> {

                if (reminder.mode == ReminderMode.SILENT) {
                    AlarmSound.stop()
                    VibratorHelper.from(context)?.stop()
                    // OPTIONAL: mark taken if you want it auto-completed
                    runBlocking { updateUseCase(reminder.copy(isTaken = true)) }
                    return
                }





                // show notification + start sound
                // Android 13+ permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val granted = ContextCompat.checkSelfPermission(
                        context, POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                    if (!granted) return
                }

                // one-shot actions to avoid double-fires
                val flags =
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT

                val isTakenPI = PendingIntent.getBroadcast(
                    context,
                    reminder.id * 10 + 1,
                    Intent(context, ReminderReceiver::class.java).apply {
                        putExtra(REMINDER, reminderJson)
                        action= IS_TAKEN
                    },
                    flags
                )

                val snoozePI = PendingIntent.getBroadcast(
                    context,
                    reminder.id * 10 + 3,
                    Intent(context, ReminderReceiver::class.java).apply {
                        putExtra(REMINDER, reminderJson)
                        action = SNOOZE
                    },
                    flags
                )


                val notification = NotificationCompat.Builder(context, CHANNEL)
                    .setSmallIcon(R.drawable.ic_notifications) // or a generic bell
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText("${reminder.name}  ${reminder.dosage}")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_snooze, context.getString(R.string.snooze), snoozePI)
                    .addAction(R.drawable.ic_check, context.getString(R.string.taken), isTakenPI)
                    .build()

                NotificationManagerCompat.from(context).notify(reminder.id, notification)

                when (reminder.mode) {
                    ReminderMode.SOUND   -> {
                        AlarmSound.start(context)
                        Log.d("TAG", "onReceive: SOUND mode")
                       // AlarmSound.start(context)
                    }
                    ReminderMode.VIBRATE -> {
                        AlarmSound.stop()
                        VibratorHelper.from(context)?.vibrateAlarmStyle()
                        Log.d("TAG", "onReceive: VIBRATE mode")
                    }
                    ReminderMode.SILENT  -> {
                       AlarmSound.stop()

                        Log.d("TAG", "onReceive: SILENT mode")

                        VibratorHelper.from(context)?.stop()
                        runBlocking { updateUseCase(reminder.copy(isTaken = true)) }


                    }
                }
            }
        }
    }





}



