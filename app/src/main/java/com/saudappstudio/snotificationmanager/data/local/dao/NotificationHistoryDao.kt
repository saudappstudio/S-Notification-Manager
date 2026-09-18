package com.saudappstudio.snotificationmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saudappstudio.snotificationmanager.data.local.entities.NotificationHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for notification history and scheduled push records.
 */
@Dao
interface NotificationHistoryDao {
    @Query("SELECT * FROM notification_history ORDER BY sentAt DESC")
    fun getAllHistory(): Flow<List<NotificationHistoryEntity>>

    @Query("SELECT * FROM notification_history WHERE appId = :appId ORDER BY sentAt DESC")
    fun getHistoryByApp(appId: String): Flow<List<NotificationHistoryEntity>>

    @Query("SELECT * FROM notification_history WHERE id = :id")
    suspend fun getHistoryById(id: String): NotificationHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: NotificationHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<NotificationHistoryEntity>)

    @Query("DELETE FROM notification_history")
    suspend fun clearHistory()

    @Query("SELECT COUNT(*) FROM notification_history")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM notification_history WHERE appId = :appId")
    suspend fun getCountByApp(appId: String): Int
}
