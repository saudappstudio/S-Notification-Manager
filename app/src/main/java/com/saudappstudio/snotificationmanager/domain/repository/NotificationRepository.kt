package com.saudappstudio.snotificationmanager.domain.repository

import com.saudappstudio.snotificationmanager.domain.model.NotificationHistoryModel
import com.saudappstudio.snotificationmanager.domain.model.NotificationPayload
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for dispatching notifications and managing send history records.
 */
interface NotificationRepository {
    fun getAllHistory(): Flow<List<NotificationHistoryModel>>
    suspend fun getHistoryById(id: String): NotificationHistoryModel?
    suspend fun insertHistory(history: NotificationHistoryModel)
    suspend fun clearHistory()
    suspend fun sendNotification(payload: NotificationPayload): Result<String>
    suspend fun sendTestNotification(payload: NotificationPayload): Result<String>
}
