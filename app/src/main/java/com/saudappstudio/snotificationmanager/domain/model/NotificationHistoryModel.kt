package com.saudappstudio.snotificationmanager.domain.model

/**
 * Domain model representing a dispatched push or in-app notification history record.
 */
data class NotificationHistoryModel(
    val id: String,
    val appId: String,
    val appName: String,
    val title: String,
    val message: String,
    val targetType: TargetType,
    val target: String,
    val environment: Environment,
    val status: String,
    val messageId: String? = null,
    val error: String? = null,
    val imageUrl: String = "",
    val clickAction: String = "OPEN_APP",
    val deepLink: String = "",
    val notificationType: String = "PUSH",
    val eventTrigger: String = "",
    val isScheduled: Boolean = false,
    val scheduledTimestamp: Long? = null,
    val customData: Map<String, String> = emptyMap(),
    val sentAt: Long = System.currentTimeMillis()
)
