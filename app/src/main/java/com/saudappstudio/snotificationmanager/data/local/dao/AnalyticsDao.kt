package com.saudappstudio.snotificationmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saudappstudio.snotificationmanager.data.local.entities.AnalyticsEventEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for local Room database operations on analytics events.
 */
@Dao
interface AnalyticsDao {

    @Query("SELECT * FROM analytics_events ORDER BY eventCount DESC")
    fun getAllAnalyticsEvents(): Flow<List<AnalyticsEventEntity>>

    @Query("SELECT * FROM analytics_events WHERE appId = :appId ORDER BY eventCount DESC")
    fun getAnalyticsEventsByApp(appId: String): Flow<List<AnalyticsEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalyticsEvents(events: List<AnalyticsEventEntity>)

    @Query("DELETE FROM analytics_events")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM analytics_events")
    suspend fun getCount(): Int
}
