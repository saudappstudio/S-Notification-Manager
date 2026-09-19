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

/**
 * Data Transfer Object returned from Netlify /analytics endpoint.
 */
data class AnalyticsResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("propertyId") val propertyId: String? = null,
    @SerializedName("summary") val summary: AnalyticsSummaryDto? = null,
    @SerializedName("events") val events: List<AnalyticsEventDto>? = null,
    @SerializedName("osDemographics") val osDemographics: List<DemographicDto>? = null,
    @SerializedName("deviceDemographics") val deviceDemographics: List<DemographicDto>? = null,
    @SerializedName("error") val error: String? = null
)

/**
 * DTO representing aggregate metric summaries.
 */
data class AnalyticsSummaryDto(
    @SerializedName("dau") val dau: Int = 0,
    @SerializedName("wau") val wau: Int = 0,
    @SerializedName("mau") val mau: Int = 0,
    @SerializedName("totalEvents") val totalEvents: Int = 0,
    @SerializedName("avgSessionDurationSeconds") val avgSessionDurationSeconds: Int = 0,
    @SerializedName("notificationOpenRate") val notificationOpenRate: Double = 0.0,
    @SerializedName("realtimeActiveUsers") val realtimeActiveUsers: Int = 0
)

/**
 * DTO representing an individual tracked GA4 event.
 */
data class AnalyticsEventDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("eventName") val eventName: String,
    @SerializedName("category") val category: String = "GENERAL",
    @SerializedName("eventCount") val eventCount: Int = 0,
    @SerializedName("uniqueUsers") val uniqueUsers: Int = 0,
    @SerializedName("growthTrendPercentage") val growthTrendPercentage: Double = 0.0
)

/**
 * DTO representing demographic distribution data.
 */
data class DemographicDto(
    @SerializedName("label") val label: String,
    @SerializedName("count") val count: Int = 0,
    @SerializedName("percentage") val percentage: Float = 0f
)

/**
 * Data Transfer Object returned from Netlify /fetch-crashlytics endpoint.
 */
data class CrashlyticsFetchResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("configured") val configured: Boolean = false,
    @SerializedName("projectId") val projectId: String? = null,
    @SerializedName("backendKey") val backendKey: String? = null,
    @SerializedName("issues") val issues: List<RemoteCrashIssueDto>? = null,
    @SerializedName("error") val error: String? = null
)

/**
 * DTO representing a Crashlytics issue returned from the serverless backend.
 */
data class RemoteCrashIssueDto(
    @SerializedName("id") val id: String,
    @SerializedName("appId") val appId: String = "",
    @SerializedName("appName") val appName: String = "",
    @SerializedName("packageName") val packageName: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("subtitle") val subtitle: String = "",
    @SerializedName("topStackFrame") val topStackFrame: String = "",
    @SerializedName("crashCount") val crashCount: Int = 0,
    @SerializedName("userCount") val userCount: Int = 0,
    @SerializedName("isFatal") val isFatal: Boolean = true,
    @SerializedName("status") val status: String = "OPEN",
    @SerializedName("firstSeenTimestamp") val firstSeenTimestamp: Long = 0L,
    @SerializedName("lastSeenTimestamp") val lastSeenTimestamp: Long = 0L,
    @SerializedName("appVersion") val appVersion: String = "",
    @SerializedName("androidVersion") val androidVersion: String = "",
    @SerializedName("deviceModel") val deviceModel: String = "",
    @SerializedName("stackTrace") val stackTrace: String = ""
)
