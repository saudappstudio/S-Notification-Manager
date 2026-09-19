package com.saudappstudio.snotificationmanager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saudappstudio.snotificationmanager.domain.model.AnalyticsEventModel

/**
 * Room entity storing cached Firebase Analytics events locally.
 */
@Entity(tableName = "analytics_events")
data class AnalyticsEventEntity(
    @PrimaryKey val id: String,
    val appId: String,
    val eventName: String,
    val category: String,
    val eventCount: Int,
    val uniqueUsers: Int,
    val growthTrendPercentage: Double,
    val timestamp: Long
) {
    /**
     * Converts entity to domain model representation.
     */
    fun toDomainModel(): AnalyticsEventModel = AnalyticsEventModel(
        id = id,
        appId = appId,
        eventName = eventName,
        category = category,
        eventCount = eventCount,
        uniqueUsers = uniqueUsers,
        growthTrendPercentage = growthTrendPercentage,
        timestamp = timestamp
    )

    companion object {
        /**
         * Converts domain model into database entity.
         */
        fun fromDomain(model: AnalyticsEventModel): AnalyticsEventEntity = AnalyticsEventEntity(
            id = model.id,
            appId = model.appId,
            eventName = model.eventName,
            category = model.category,
            eventCount = model.eventCount,
            uniqueUsers = model.uniqueUsers,
            growthTrendPercentage = model.growthTrendPercentage,
            timestamp = model.timestamp
        )
    }
}
