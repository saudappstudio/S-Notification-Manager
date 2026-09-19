package com.saudappstudio.snotificationmanager.domain.model

/**
 * Represents the status of a Firebase Crashlytics issue.
 */
enum class CrashIssueStatus {
    OPEN,
    RESOLVED,
    MUTED
}

/**
 * Domain model representing a Firebase Crashlytics issue item in dashboard lists.
 *
 * @property id Unique identifier for the crash issue
 * @property appId ID of the associated application
 * @property appName Name of the application
 * @property packageName Package identifier of the application
 * @property title Primary title/exception type (e.g. java.lang.NullPointerException)
 * @property subtitle Error description message or top frame summary
 * @property topStackFrame Specific file name and line number of the failure
 * @property crashCount Total recorded occurrence count
 * @property userCount Number of unique affected users/devices
 * @property isFatal True if this is a fatal crash; false if non-fatal logged exception
 * @property status Resolution status of the issue (OPEN, RESOLVED, MUTED)
 * @property firstSeenTimestamp Timestamp of first recorded crash event
 * @property lastSeenTimestamp Timestamp of most recent recorded crash event
 * @property appVersion App version name/code where crash occurred
 * @property androidVersion Primary Android OS version affected
 * @property deviceModel Representative device model
 */
data class CrashIssueModel(
    val id: String,
    val appId: String,
    val appName: String,
    val packageName: String,
    val title: String,
    val subtitle: String,
    val topStackFrame: String,
    val crashCount: Int,
    val userCount: Int,
    val isFatal: Boolean,
    val status: CrashIssueStatus = CrashIssueStatus.OPEN,
    val firstSeenTimestamp: Long = System.currentTimeMillis(),
    val lastSeenTimestamp: Long = System.currentTimeMillis(),
    val appVersion: String = "1.0.0",
    val androidVersion: String = "Android 14 (API 34)",
    val deviceModel: String = "Google Pixel 8 Pro"
)

/**
 * Summary metrics container for Crashlytics dashboard cards.
 */
data class CrashMetricsSummary(
    val totalCrashes: Int = 0,
    val affectedUsers: Int = 0,
    val crashFreeRatePercentage: Double = 100.0,
    val fatalCount: Int = 0,
    val nonFatalCount: Int = 0
)
