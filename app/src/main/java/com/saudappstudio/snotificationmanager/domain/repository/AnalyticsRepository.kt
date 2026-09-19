package com.saudappstudio.snotificationmanager.domain.repository

import com.saudappstudio.snotificationmanager.domain.model.AnalyticsEventModel
import com.saudappstudio.snotificationmanager.domain.model.AnalyticsFilter
import com.saudappstudio.snotificationmanager.domain.model.AnalyticsMetricsSummary
import com.saudappstudio.snotificationmanager.domain.model.DemographicItem
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository interface for accessing real Firebase Analytics event metrics via Netlify & GA4 Data API.
 */
interface AnalyticsRepository {

    /**
     * Observes filtered analytics events stream stored in Room database.
     *
     * @param filter Analytics search, app, and time range filters.
     * @return Flow list of matching analytics events.
     */
    fun getAnalyticsEvents(filter: AnalyticsFilter): Flow<List<AnalyticsEventModel>>

    /**
     * Retrieves cached aggregate summary metrics for the given app and time range.
     *
     * @param appId Target app identifier or empty for all apps.
     * @param timeRange Timeframe filter identifier ("TODAY", "7D", "30D", "90D").
     * @return Calculated AnalyticsMetricsSummary.
     */
    suspend fun getMetricsSummary(appId: String = "", timeRange: String = "7D"): AnalyticsMetricsSummary

    /**
     * Fetches real live analytics data from Netlify serverless endpoint proxying GA4 Data API v1beta.
     *
     * @param appId Target app ID.
     * @param backendKey Netlify service account identifier.
     * @param gaPropertyId Optional Google Analytics 4 property ID (e.g. "properties/123456789").
     * @param timeRange Timeframe filter ("TODAY", "7D", "30D", "90D").
     * @return Result containing updated AnalyticsMetricsSummary.
     */
    suspend fun fetchRealAnalytics(
        appId: String = "",
        backendKey: String = "dictionary",
        gaPropertyId: String? = null,
        timeRange: String = "7D"
    ): Result<AnalyticsMetricsSummary>

    /**
     * Gets device and OS demographic distributions for the target app.
     *
     * @param appId Target app identifier.
     * @return Pair of OS demographics list and Device model demographics list.
     */
    suspend fun getDemographics(appId: String = ""): Pair<List<DemographicItem>, List<DemographicItem>>

    /**
     * Clears cached analytics records from Room database.
     */
    suspend fun clearAllAnalytics()
}
