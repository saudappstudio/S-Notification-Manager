package com.saudappstudio.snotificationmanager.domain.model

// Additional filter data class for query parameters
data class CrashlyticsFilter(
    val appId: String = "",
    val isFatal: Boolean? = null,
    val status: CrashIssueStatus? = null,
    val searchQuery: String = ""
)
