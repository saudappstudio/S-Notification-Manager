package com.saudappstudio.snotificationmanager.domain.repository

import com.saudappstudio.snotificationmanager.domain.model.CrashDetailModel
import com.saudappstudio.snotificationmanager.domain.model.CrashIssueModel
import com.saudappstudio.snotificationmanager.domain.model.CrashIssueStatus
import com.saudappstudio.snotificationmanager.domain.model.CrashMetricsSummary
import com.saudappstudio.snotificationmanager.domain.model.CrashlyticsFilter
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository interface for accessing and managing Firebase Crashlytics reports.
 */
interface CrashlyticsRepository {

    /**
     * Observes filtered crash issues stream.
     *
     * @param filter Search and category filters
     * @return Flow list of matching crash issues
     */
    fun getCrashIssues(filter: CrashlyticsFilter): Flow<List<CrashIssueModel>>

    /**
     * Retrieves crash issue details including full stack trace by issue ID.
     *
     * @param issueId Unique issue ID
     * @return CrashDetailModel or null if not found
     */
    suspend fun getCrashDetail(issueId: String): CrashDetailModel?

    /**
     * Gets summary dashboard metrics for specified app or across all apps.
     *
     * @param appId Target app ID or empty string for all apps
     * @return CrashMetricsSummary calculation
     */
    suspend fun getMetricsSummary(appId: String = ""): CrashMetricsSummary

    /**
     * Updates resolution status of a crash issue.
     *
     * @param issueId Unique issue ID
     * @param status New CrashIssueStatus
     */
    suspend fun updateIssueStatus(issueId: String, status: CrashIssueStatus)

    /**
     * Populates database with sample real-world crash logs for demonstration/preview.
     */
    suspend fun seedSampleCrashData()

    /**
     * Clears cached crash logs.
     */
    suspend fun clearAllCrashes()
}
