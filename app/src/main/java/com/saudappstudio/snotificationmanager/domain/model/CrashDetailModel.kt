package com.saudappstudio.snotificationmanager.domain.model

/**
 * Detailed information and stack trace snapshot for a specific crash issue.
 *
 * @property id Unique detail record identifier
 * @property issueId Parent issue ID
 * @property stackTrace Complete multiline stack trace text
 * @property threadName Name of the thread that threw the exception
 * @property osVersion Detailed OS build string
 * @property deviceModel Hardware model identifier
 * @property ramFreeMb Available RAM memory in megabytes at crash time
 * @property diskFreeMb Available storage in megabytes at crash time
 * @property customKeys Key-value map of custom log keys recorded at crash time
 */
data class CrashDetailModel(
    val id: String,
    val issueId: String,
    val stackTrace: String,
    val threadName: String = "main",
    val osVersion: String = "Android 14 (API 34)",
    val deviceModel: String = "Google Pixel 8 Pro",
    val ramFreeMb: Long = 1840L,
    val diskFreeMb: Long = 12400L,
    val customKeys: Map<String, String> = emptyMap()
)
