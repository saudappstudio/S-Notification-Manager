package com.saudappstudio.snotificationmanager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity storing notification send and schedule history records.
 */
@Entity(tableName = "notification_history")
data class NotificationHistoryEntity(
    @PrimaryKey val id: String,
    val appId: String,
    val appName: String,
    val title: String,
    val message: String,
    val targetType: String,
    val target: String,
    val environment: String,
    val status: String,
    val messageId: String?,
    val error: String?,
    val imageUrl: String,
    val clickAction: String,
    val deepLink: String,
    val notificationType: String = "PUSH",
    val eventTrigger: String = "",
    val isScheduled: Boolean = false,
    val scheduledTimestamp: Long? = null,
    val customDataJson: String,
    val sentAt: Long
)
