package com.saudappstudio.snotificationmanager.domain.model

/**
 * Domain model representing a managed client application.
 */
data class AppModel(
    val id: String,
    val name: String,
    val packageName: String,
    val appId: String,
    val iconName: String = "ic_notification_logo",
    val firebaseProjectId: String,
    val environment: Environment = Environment.PRODUCTION,
    val defaultTopic: String = "",
    val enabled: Boolean = true,
    val testMode: Boolean = false,
    val allowPush: Boolean = true,
    val allowTopic: Boolean = true,
    val allowToken: Boolean = true,
    val allowImage: Boolean = true,
    val allowDeepLinks: Boolean = true,
    val requireConfirmForProd: Boolean = true,
    val defaultClickAction: String = "OPEN_APP",
    val defaultChannelId: String = "general_notifications",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
