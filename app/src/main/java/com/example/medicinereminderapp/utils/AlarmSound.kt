package com.example.medicinereminderapp.utils

import android.content.Context
import android.media.MediaPlayer
import com.example.medicinereminderapp.R

object AlarmSound {
    @Volatile private var mp: MediaPlayer? = null

    fun start(context: Context) {
        stop() // ensure clean state
        mp = MediaPlayer.create(context.applicationContext, R.raw.alarm_music).apply {
            isLooping = true // or true if you want continuous ring
            setOnCompletionListener {
                it.release()
                mp = null
            }
            start()
        }
    }

    fun stop() {
        try {
            mp?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (_: Throwable) { /* ignore */ }
        mp = null
    }
}
