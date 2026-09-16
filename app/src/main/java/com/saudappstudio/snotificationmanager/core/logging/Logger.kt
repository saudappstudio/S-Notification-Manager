package com.saudappstudio.snotificationmanager.core.logging

import android.util.Log
import com.saudappstudio.snotificationmanager.BuildConfig

/**
 * Production-safe logging abstraction.
 * Suppresses debug and verbose log output in release configurations.
 */
object Logger {
    private const val DEFAULT_TAG = "SNotificationManager"

    /**
     * Emits a debug log message only when running under debug build variants.
     *
     * @param message Content of the debug log statement.
     * @param tag Optional logging tag identifier.
     */
    fun d(message: String, tag: String = DEFAULT_TAG) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    /**
     * Emits an informational log message.
     *
     * @param message Content of the information log statement.
     * @param tag Optional logging tag identifier.
     */
    fun i(message: String, tag: String = DEFAULT_TAG) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message)
        }
    }

    /**
     * Emits a warning log message.
     *
     * @param message Content of the warning log statement.
     * @param tag Optional logging tag identifier.
     */
    fun w(message: String, tag: String = DEFAULT_TAG) {
        Log.w(tag, message)
    }

    /**
     * Emits an error log message along with an optional exception.
     *
     * @param message Error description message.
     * @param throwable Optional exception stack trace.
     * @param tag Optional logging tag identifier.
     */
    fun e(message: String, throwable: Throwable? = null, tag: String = DEFAULT_TAG) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }
}
