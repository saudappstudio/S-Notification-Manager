package com.saudappstudio.snotificationmanager.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object dispatched to the Netlify serverless endpoint.
 */
data class NotificationRequestDto(
    @SerializedName("appId") val appId: String,
    @SerializedName("firebaseProjectKey") val firebaseProjectKey: String,
    @SerializedName("environment") val environment: String,
    @SerializedName("targetType") val targetType: String,
    @SerializedName("target") val target: String,
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("clickAction") val clickAction: String? = null,
    @SerializedName("deepLink") val deepLink: String? = null,
    @SerializedName("channelId") val channelId: String? = null,
    @SerializedName("priority") val priority: String? = null,
    @SerializedName("ttl") val ttl: Long? = null,
    @SerializedName("collapseKey") val collapseKey: String? = null,
    @SerializedName("badge") val badge: Int? = null,
    @SerializedName("notificationType") val notificationType: String? = null,
    @SerializedName("eventTrigger") val eventTrigger: String? = null,
    @SerializedName("isScheduled") val isScheduled: Boolean? = null,
    @SerializedName("scheduledTimestamp") val scheduledTimestamp: Long? = null,
    @SerializedName("customData") val customData: Map<String, String>? = null
)

/**
 * Data Transfer Object returned from Netlify serverless function execution.
 */
data class NotificationResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("messageId") val messageId: String? = null,
    @SerializedName("error") val error: String? = null
)

/**
 * Data Transfer Object representing Netlify /health endpoint response.
 */
data class BackendHealthDto(
    @SerializedName("status") val status: String? = null,
    @SerializedName("version") val version: String? = null,
    @SerializedName("timestamp") val timestamp: Long? = null,
    @SerializedName("message") val message: String? = null
)
