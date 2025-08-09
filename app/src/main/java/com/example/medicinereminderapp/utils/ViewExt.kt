package com.example.medicinereminderapp.utils


import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible

fun View.show() { isVisible = true }
fun View.hide() { isVisible = false }

fun Context.toast(msg: CharSequence) =
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun TextView.setTextOrClear(value: CharSequence?) {
    text = value ?: ""
}

