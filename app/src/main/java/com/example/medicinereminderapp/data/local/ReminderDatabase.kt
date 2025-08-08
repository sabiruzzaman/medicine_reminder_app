package com.example.medicinereminderapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.medicinereminderapp.domain.model.Reminder

@Database(entities = [Reminder::class], version = 1)
abstract class ReminderDatabase : RoomDatabase() {

    abstract fun getReminderDao(): ReminderDao

    companion object {
        fun getInstance(context: Context) =
            Room.databaseBuilder(context, ReminderDatabase::class.java, "reminder_database")
                .build()
    }

}
