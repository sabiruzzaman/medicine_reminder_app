package com.example.medicinereminderapp.utils


import com.google.android.material.textfield.TextInputLayout
import java.util.Date

object FormUtils {

    /** Sets error if value is blank; returns true if valid. */
    fun required(til: TextInputLayout, value: String?, msg: String): Boolean {
        val ok = !value.isNullOrBlank()
        til.error = if (ok) null else msg
        return ok
    }

    /** Validates both date selection and not-in-past (when required). */
    fun validateDate(
        selected: Date?,
        requireFuture: Boolean,
        msgPick: () -> String,
        onError: (String) -> Unit
    ): Boolean {
        if (selected == null) { onError(msgPick()); return false }
        if (requireFuture && DateTimeUtils.isPast(selected)) {
            onError("Selected time is in the past")
            return false
        }
        return true
    }
}
