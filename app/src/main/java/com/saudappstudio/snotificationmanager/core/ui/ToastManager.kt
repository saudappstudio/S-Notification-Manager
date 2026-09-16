package com.saudappstudio.snotificationmanager.core.ui

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * Singleton Toast manager providing immediate UI feedback.
 * Automatically cancels any actively displaying Toast before showing a new one.
 */
object ToastManager {
    private var activeToast: Toast? = null

    /**
     * Displays a text toast, canceling any currently visible toast.
     *
     * @param context Application or UI context.
     * @param message Text to display in the toast.
     * @param duration Toast duration (Toast.LENGTH_SHORT or Toast.LENGTH_LONG).
     */
    fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        activeToast?.cancel()
        activeToast = Toast.makeText(context.applicationContext, message, duration).also {
            it.show()
        }
    }

    /**
     * Displays a string-resource toast, canceling any currently visible toast.
     *
     * @param context Application or UI context.
     * @param stringResId Resource ID of the localized string.
     * @param duration Toast duration.
     */
    fun show(context: Context, @StringRes stringResId: Int, duration: Int = Toast.LENGTH_SHORT) {
        show(context, context.getString(stringResId), duration)
    }
}
