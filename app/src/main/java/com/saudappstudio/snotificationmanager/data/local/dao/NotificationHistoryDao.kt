package com.saudappstudio.snotificationmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saudappstudio.snotificationmanager.data.local.entities.NotificationHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationHistoryDao {
    @Query("SELECT * FROM notification_history ORDER BY sentAt DESC")
    fun getAllHistory(): Flow<List<NotificationHistoryEntity>>

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
}
