package com.saudappstudio.snotificationmanager.data.repository

import com.saudappstudio.snotificationmanager.core.logging.Logger
import com.saudappstudio.snotificationmanager.data.local.dao.CrashlyticsDao
import com.saudappstudio.snotificationmanager.data.local.entities.CrashIssueEntity
import com.saudappstudio.snotificationmanager.data.remote.api.NetlifyApiService
import com.saudappstudio.snotificationmanager.domain.model.CrashDetailModel
import com.saudappstudio.snotificationmanager.domain.model.CrashIssueModel
import com.saudappstudio.snotificationmanager.domain.model.CrashIssueStatus
import com.saudappstudio.snotificationmanager.domain.model.CrashMetricsSummary
import com.saudappstudio.snotificationmanager.domain.model.CrashlyticsFilter
import com.saudappstudio.snotificationmanager.domain.repository.CrashlyticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for managing real-time Firebase Crashlytics issues and Netlify serverless backend integration.
 */
@Singleton
class CrashlyticsRepositoryImpl @Inject constructor(
    private val crashlyticsDao: CrashlyticsDao,
    private val netlifyApiService: NetlifyApiService
) : CrashlyticsRepository {

    override fun getCrashIssues(filter: CrashlyticsFilter): Flow<List<CrashIssueModel>> {
        val baseFlow = if (filter.appId.isBlank()) {
            crashlyticsDao.getAllCrashIssues()
        } else {
            crashlyticsDao.getCrashIssuesByApp(filter.appId)
        }

        return baseFlow.map { entities ->
            entities.map { it.toDomainModel() }
                .filter { issue ->
                    val matchesFatal = filter.isFatal == null || issue.isFatal == filter.isFatal
                    val matchesStatus = filter.status == null || issue.status == filter.status
                    val matchesQuery = filter.searchQuery.isBlank() ||
                            issue.title.contains(filter.searchQuery, ignoreCase = true) ||
                            issue.subtitle.contains(filter.searchQuery, ignoreCase = true) ||
                            issue.packageName.contains(filter.searchQuery, ignoreCase = true) ||
                            issue.topStackFrame.contains(filter.searchQuery, ignoreCase = true)

                    matchesFatal && matchesStatus && matchesQuery
                }
        }
    }

    override suspend fun getCrashDetail(issueId: String): CrashDetailModel? {
        val entity = crashlyticsDao.getCrashIssueById(issueId) ?: return null
        return CrashDetailModel(
            id = "detail_${entity.id}",
            issueId = entity.id,
            stackTrace = entity.stackTrace.ifBlank { "${entity.title}: ${entity.subtitle}\n\tat ${entity.topStackFrame}" },
            osVersion = entity.androidVersion,
            deviceModel = entity.deviceModel
        )
    }

    override suspend fun getMetricsSummary(appId: String): CrashMetricsSummary {
        val flow = if (appId.isBlank()) crashlyticsDao.getAllCrashIssues() else crashlyticsDao.getCrashIssuesByApp(appId)
        val list = runCatching { flow.first() }.getOrDefault(emptyList())

        if (list.isEmpty()) {
            return CrashMetricsSummary(
                totalCrashes = 0,
                affectedUsers = 0,
                crashFreeRatePercentage = 100.0,
                fatalCount = 0,
                nonFatalCount = 0
            )
        }

        val totalCrashes = list.sumOf { it.crashCount }
        val affectedUsers = list.sumOf { it.userCount }
        val fatalCount = list.filter { it.isFatal }.sumOf { it.crashCount }
        val nonFatalCount = list.filter { !it.isFatal }.sumOf { it.crashCount }
        val crashFreeRate = if (totalCrashes > 0) {
            maxOf(90.0, 100.0 - (affectedUsers.toDouble() / 100.0))
        } else {
            100.0
        }

        return CrashMetricsSummary(
            totalCrashes = totalCrashes,
            affectedUsers = affectedUsers,
            crashFreeRatePercentage = crashFreeRate,
            fatalCount = fatalCount,
            nonFatalCount = nonFatalCount
        )
    }

    override suspend fun updateIssueStatus(issueId: String, status: CrashIssueStatus) {
        Logger.d("Updating status for issue $issueId to ${status.name}", "CrashlyticsRepository")
        crashlyticsDao.updateStatus(issueId, status.name)
    }

    override suspend fun seedSampleCrashData() {
        try {
            Logger.d("Fetching live Crashlytics issues from Netlify backend", "CrashlyticsRepository")
            val response = netlifyApiService.fetchCrashlytics(backendKey = "dictionary")
            if (response.isSuccessful && response.body()?.success == true) {
                val remoteIssues = response.body()?.issues ?: emptyList()
                val entities = remoteIssues.map { dto ->
                    CrashIssueEntity(
                        id = dto.id,
                        appId = dto.appId,
                        appName = dto.appName,
                        packageName = dto.packageName,
                        title = dto.title,
                        subtitle = dto.subtitle,
                        topStackFrame = dto.topStackFrame,
                        crashCount = dto.crashCount,
                        userCount = dto.userCount,
                        isFatal = dto.isFatal,
                        status = dto.status,
                        firstSeenTimestamp = dto.firstSeenTimestamp,
                        lastSeenTimestamp = dto.lastSeenTimestamp,
                        appVersion = dto.appVersion,
                        androidVersion = dto.androidVersion,
                        deviceModel = dto.deviceModel,
                        stackTrace = dto.stackTrace
                    )
                }
                if (entities.isNotEmpty()) {
                    crashlyticsDao.insertCrashIssues(entities)
                    Logger.d("Synced ${entities.size} live Crashlytics issues from Netlify.", "CrashlyticsRepository")
                }
            }
        } catch (e: Exception) {
            Logger.e("Failed to sync live Crashlytics issues from Netlify: ${e.message}", e, "CrashlyticsRepository")
        }
    }

    override suspend fun clearAllCrashes() {
        Logger.d("Clearing all local crash entries", "CrashlyticsRepository")
        crashlyticsDao.deleteAll()
    }
}
