package com.saudappstudio.snotificationmanager.data.repository

import com.saudappstudio.snotificationmanager.core.logging.Logger
import com.saudappstudio.snotificationmanager.data.local.dao.AnalyticsDao
import com.saudappstudio.snotificationmanager.data.local.dao.AppDao
import com.saudappstudio.snotificationmanager.data.local.dao.FirebaseProjectDao
import com.saudappstudio.snotificationmanager.data.local.entities.AnalyticsEventEntity
import com.saudappstudio.snotificationmanager.data.remote.api.NetlifyApiService
import com.saudappstudio.snotificationmanager.domain.model.AnalyticsEventModel
import com.saudappstudio.snotificationmanager.domain.model.AnalyticsFilter
import com.saudappstudio.snotificationmanager.domain.model.AnalyticsMetricsSummary
import com.saudappstudio.snotificationmanager.domain.model.DemographicItem
import com.saudappstudio.snotificationmanager.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation managing real Firebase Analytics data via Netlify serverless proxy and GA4 Data API v1beta.
 */
@Singleton
class AnalyticsRepositoryImpl @Inject constructor(
    private val analyticsDao: AnalyticsDao,
    private val apiService: NetlifyApiService,
    private val appDao: AppDao,
    private val firebaseProjectDao: FirebaseProjectDao
) : AnalyticsRepository {

    private var cachedSummary: AnalyticsMetricsSummary = AnalyticsMetricsSummary()
    private var cachedOsDemographics: List<DemographicItem> = emptyList()
    private var cachedDeviceDemographics: List<DemographicItem> = emptyList()

    override fun getAnalyticsEvents(filter: AnalyticsFilter): Flow<List<AnalyticsEventModel>> {
        val baseFlow = if (filter.appId.isBlank()) {
            analyticsDao.getAllAnalyticsEvents()
        } else {
            analyticsDao.getAnalyticsEventsByApp(filter.appId)
        }

        return baseFlow.map { entities ->
            entities.map { it.toDomainModel() }
                .filter { event ->
                    val matchesApp = filter.appId.isBlank() || event.appId == filter.appId
                    val matchesQuery = filter.searchQuery.isBlank() ||
                            event.eventName.contains(filter.searchQuery, ignoreCase = true) ||
                            event.category.contains(filter.searchQuery, ignoreCase = true)

                    matchesApp && matchesQuery
                }
        }
    }

    override suspend fun getMetricsSummary(appId: String, timeRange: String): AnalyticsMetricsSummary {
        val flow = if (appId.isBlank()) {
            analyticsDao.getAllAnalyticsEvents()
        } else {
            analyticsDao.getAnalyticsEventsByApp(appId)
        }
        val list = runCatching { flow.first() }.getOrDefault(emptyList())

        if (list.isEmpty()) {
            return cachedSummary
        }

        val totalEvents = list.sumOf { it.eventCount }
        val maxUsers = list.maxOfOrNull { it.uniqueUsers } ?: 0

        return cachedSummary.copy(
            totalEvents = totalEvents,
            mau = maxOf(cachedSummary.mau, maxUsers)
        )
    }

    override suspend fun fetchRealAnalytics(
        appId: String,
        backendKey: String,
        gaPropertyId: String?,
        timeRange: String
    ): Result<AnalyticsMetricsSummary> {
        return try {
            Logger.d("Fetching real GA4 analytics for appId=$appId backendKey=$backendKey", "AnalyticsRepository")

            val response = apiService.getAnalyticsData(
                backendKey = backendKey.ifBlank { "dictionary" },
                propertyId = gaPropertyId?.takeIf { it.isNotBlank() },
                timeRange = timeRange
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()!!
                val sumDto = dto.summary

                val realSummary = AnalyticsMetricsSummary(
                    dau = sumDto?.dau ?: 0,
                    wau = sumDto?.wau ?: 0,
                    mau = sumDto?.mau ?: 0,
                    totalEvents = sumDto?.totalEvents ?: 0,
                    avgSessionDurationSeconds = sumDto?.avgSessionDurationSeconds ?: 0,
                    notificationOpenRate = sumDto?.notificationOpenRate ?: 0.0,
                    realtimeActiveUsers = sumDto?.realtimeActiveUsers ?: 0
                )
                cachedSummary = realSummary

                // Save real event models to Room DB
                dto.events?.let { eventsDto ->
                    val entities = eventsDto.map { evt ->
                        AnalyticsEventEntity(
                            id = evt.id ?: "evt_${evt.eventName}",
                            appId = appId,
                            eventName = evt.eventName,
                            category = evt.category,
                            eventCount = evt.eventCount,
                            uniqueUsers = evt.uniqueUsers,
                            growthTrendPercentage = evt.growthTrendPercentage,
                            timestamp = System.currentTimeMillis()
                        )
                    }
                    analyticsDao.insertAnalyticsEvents(entities)
                }

                // Cache real demographics
                cachedOsDemographics = dto.osDemographics?.map { DemographicItem(it.label, it.count, it.percentage) } ?: emptyList()
                cachedDeviceDemographics = dto.deviceDemographics?.map { DemographicItem(it.label, it.count, it.percentage) } ?: emptyList()

                Result.success(realSummary)
            } else {
                val errorMsg = response.body()?.error ?: response.errorBody()?.string() ?: "HTTP ${response.code()}"
                Logger.e("Analytics API call failed: $errorMsg", tag = "AnalyticsRepository")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Logger.e("Exception during real analytics fetch", e, "AnalyticsRepository")
            Result.failure(e)
        }
    }

    override suspend fun getDemographics(appId: String): Pair<List<DemographicItem>, List<DemographicItem>> {
        return Pair(cachedOsDemographics, cachedDeviceDemographics)
    }

    override suspend fun clearAllAnalytics() {
        try {
            analyticsDao.deleteAll()
            cachedSummary = AnalyticsMetricsSummary()
            cachedOsDemographics = emptyList()
            cachedDeviceDemographics = emptyList()
            Logger.d("Cleared all cached analytics records.", "AnalyticsRepository")
        } catch (e: Exception) {
            Logger.e("Error clearing analytics", e, "AnalyticsRepository")
        }
    }
}
