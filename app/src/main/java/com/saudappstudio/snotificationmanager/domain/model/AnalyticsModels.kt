package com.saudappstudio.snotificationmanager.domain.model

/**
 * Data representation of overall aggregate metrics for Firebase Analytics.
 *
 * @property dau Daily Active Users count.
 * @property wau Weekly Active Users count.
 * @property mau Monthly Active Users count.
 * @property totalEvents Total logged event occurrences.
 * @property avgSessionDurationSeconds Average user session duration in seconds.
 * @property notificationOpenRate Percentage of push notifications opened by users.
 * @property realtimeActiveUsers Active users in the last 30 minutes.
 */
data class AnalyticsMetricsSummary(
    val dau: Int = 0,
    val wau: Int = 0,
    val mau: Int = 0,
    val totalEvents: Int = 0,
    val avgSessionDurationSeconds: Int = 0,
    val notificationOpenRate: Double = 0.0,
    val realtimeActiveUsers: Int = 0
)

/**
 * Data representation of a tracked Firebase Analytics event.
 *
 * @property id Unique identifier for the analytics event record.
 * @property appId ID of the associated application.
 * @property eventName Name of the Firebase Analytics event (e.g. notification_opened).
 * @property category Category group for filtering (e.g., ENGAGEMENT, PUSH, SYSTEM).
 * @property eventCount Total times this event was logged.
 * @property uniqueUsers Unique users who triggered this event.
 * @property growthTrendPercentage Growth rate percentage compared to previous period.
 * @property timestamp Epoch timestamp when the event aggregation was recorded.
 */
data class AnalyticsEventModel(
    val id: String,
    val appId: String,
    val eventName: String,
    val category: String,
    val eventCount: Int,
    val uniqueUsers: Int,
    val growthTrendPercentage: Double,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Data representation of user demographic & device distribution.
 *
 * @property label Demographic classification name (e.g. "Android 14", "Samsung Galaxy S23", "United States").
 * @property count Total count of users or events in this demographic group.
 * @property percentage Proportion of overall users represented by this group.
 */
data class DemographicItem(
    val label: String,
    val count: Int,
    val percentage: Float
)

/**
 * Filter container for querying Firebase Analytics data.
 *
 * @property appId ID of the selected app, or empty for all apps.
 * @property timeRange Selected timeframe ("TODAY", "7D", "30D", "90D").
 * @property searchQuery Text search query to filter event names.
 */
data class AnalyticsFilter(
    val appId: String = "",
    val timeRange: String = "7D",
    val searchQuery: String = ""
)
