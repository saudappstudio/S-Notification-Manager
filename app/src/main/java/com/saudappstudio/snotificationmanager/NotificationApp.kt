package com.saudappstudio.snotificationmanager

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.saudappstudio.snotificationmanager.core.logging.Logger
import dagger.hilt.android.HiltAndroidApp

/**
 * Base Application class for Saud Notification Manager.
 * Initializes Hilt dependency injection, production-safe logger, and notification channels.
 */
@HiltAndroidApp
class NotificationApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Logger.i("Saud Notification Manager initialized.")
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "general_notifications",
                "General Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Default channel for FCM management dispatches"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
