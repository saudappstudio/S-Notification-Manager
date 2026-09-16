package com.saudappstudio.snotificationmanager.domain.model

/**
 * Payload dispatched to the Netlify serverless function for FCM delivery.
 */
data class NotificationPayload(
    val appId: String,
    val firebaseProjectKey: String,
    val environment: Environment,
    val targetType: TargetType,
    val target: String,
    val title: String,
    val message: String,
    val imageUrl: String = "",
    val clickAction: String = "OPEN_APP",
    val deepLink: String = "",
    val channelId: String = "general_notifications",
    val priority: String = "HIGH",
    val ttl: Long = 86400L,
    val collapseKey: String = "",
    val badge: Int = 1,
    val customData: Map<String, String> = emptyMap()
)
