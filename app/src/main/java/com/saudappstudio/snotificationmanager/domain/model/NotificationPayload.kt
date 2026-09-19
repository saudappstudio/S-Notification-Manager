package com.saudappstudio.snotificationmanager.domain.model

/**
 * Payload dispatched to the Netlify serverless function for FCM / In-App Messaging delivery.
 *
 * @param appId Associated unique application identifier.
 * @param firebaseProjectKey Identifier mapped to the Firebase project on the backend.
 * @param environment Target runtime environment (PRODUCTION or TESTING).
 * @param targetType Targeting mechanism (TOPIC or TOKEN).
 * @param target The target topic name or device FCM token.
 * @param title Notification headline / message title.
 * @param message Body text of the notification or message.
 * @param imageUrl Optional hero / banner image URL.
 * @param clickAction Tap action to execute (e.g. OPEN_APP, DEEP_LINK).
 * @param deepLink Deep link URI when clickAction is DEEP_LINK.
 * @param channelId Android notification channel identifier.
 * @param priority Notification delivery priority (HIGH or NORMAL).
 * @param ttl Time-to-live in seconds.
 * @param collapseKey Collapse key grouping related notifications.
 * @param badge Launcher badge count indicator.
 * @param notificationType Type of message: "PUSH" (standard push) or "IN_APP" (in-app messaging).
 * @param eventTrigger Trigger event for in-app messaging (e.g. timer_1_min, timer_90_sec, app_open).
 * @param isScheduled Whether this notification is scheduled for delayed delivery.
 * @param scheduledTimestamp Unix epoch timestamp at which the notification is scheduled to dispatch.
 * @param scheduleDelayMinutes Delay in minutes for scheduling.
 * @param customData Custom key-value pairs delivered within the FCM data payload.
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
    val notificationType: String = "PUSH",
    val eventTrigger: String = "",
    val isScheduled: Boolean = false,
    val scheduledTimestamp: Long? = null,
    val scheduleDelayMinutes: Int = 0,
    val customData: Map<String, String> = emptyMap()
)
