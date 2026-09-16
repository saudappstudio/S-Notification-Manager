package com.saudappstudio.snotificationmanager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saudappstudio.snotificationmanager.domain.model.Environment
import com.saudappstudio.snotificationmanager.domain.model.NotificationHistoryModel
import com.saudappstudio.snotificationmanager.domain.model.TargetType

/**
 * Room database entity storing notification send history records.
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
    val customDataJson: String,
    val sentAt: Long
)
