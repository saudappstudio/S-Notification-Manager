package com.saudappstudio.snotificationmanager.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.saudappstudio.snotificationmanager.core.logging.Logger
import com.saudappstudio.snotificationmanager.data.local.dao.NotificationHistoryDao
import com.saudappstudio.snotificationmanager.data.local.entities.NotificationHistoryEntity
import com.saudappstudio.snotificationmanager.data.remote.api.NetlifyApiService
import com.saudappstudio.snotificationmanager.data.remote.models.NotificationRequestDto
import com.saudappstudio.snotificationmanager.domain.model.Environment
import com.saudappstudio.snotificationmanager.domain.model.NotificationHistoryModel
import com.saudappstudio.snotificationmanager.domain.model.NotificationPayload
import com.saudappstudio.snotificationmanager.domain.model.TargetType
import com.saudappstudio.snotificationmanager.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Concrete implementation of NotificationRepository coordinating Room persistence and Netlify API communication.
 */
class NotificationRepositoryImpl(
    private val notificationHistoryDao: NotificationHistoryDao,
    private val apiService: NetlifyApiService
) : NotificationRepository {
    private val gson = Gson()

    override fun getAllHistory(): Flow<List<NotificationHistoryModel>> {
        return notificationHistoryDao.getAllHistory().map { list ->
            list.map { entity ->
                NotificationHistoryModel(
                    id = entity.id,
                    appId = entity.appId,
                    appName = entity.appName,
                    title = entity.title,
                    message = entity.message,
                    targetType = TargetType.fromKey(entity.targetType),
                    target = entity.target,
                    environment = Environment.fromKey(entity.environment),
                    status = entity.status,
                    messageId = entity.messageId,
                    error = entity.error,
                    imageUrl = entity.imageUrl,
                    clickAction = entity.clickAction,
                    deepLink = entity.deepLink,
                    customData = parseCustomData(entity.customDataJson),
                    sentAt = entity.sentAt
                )
            }
        }
    }

    override suspend fun getHistoryById(id: String): NotificationHistoryModel? {
        val entity = notificationHistoryDao.getHistoryById(id) ?: return null
        return NotificationHistoryModel(
            id = entity.id,
            appId = entity.appId,
            appName = entity.appName,
            title = entity.title,
            message = entity.message,
            targetType = TargetType.fromKey(entity.targetType),
            target = entity.target,
            environment = Environment.fromKey(entity.environment),
            status = entity.status,
            messageId = entity.messageId,
            error = entity.error,
            imageUrl = entity.imageUrl,
            clickAction = entity.clickAction,
            deepLink = entity.deepLink,
            customData = parseCustomData(entity.customDataJson),
            sentAt = entity.sentAt
        )
    }

    override suspend fun insertHistory(history: NotificationHistoryModel) {
        val entity = NotificationHistoryEntity(
            id = history.id,
            appId = history.appId,
            appName = history.appName,
            title = history.title,
            message = history.message,
            targetType = history.targetType.key,
            target = history.target,
            environment = history.environment.key,
            status = history.status,
            messageId = history.messageId,
            error = history.error,
            imageUrl = history.imageUrl,
            clickAction = history.clickAction,
            deepLink = history.deepLink,
            customDataJson = gson.toJson(history.customData),
            sentAt = history.sentAt
        )
        notificationHistoryDao.insertHistory(entity)
    }

    override suspend fun clearHistory() {
        notificationHistoryDao.clearHistory()
    }

    override suspend fun sendNotification(payload: NotificationPayload): Result<String> {
        val dto = mapToDto(payload)
        return try {
            val response = apiService.sendNotification(dto)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body.messageId ?: "success")
                } else {
                    Result.failure(Exception(body?.error ?: "Backend returned unsuccessful response"))
                }
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Unauthorized: Check API authentication token in Settings"
                    403 -> "Forbidden: You do not have permission to send this notification"
                    404 -> "Not Found: Netlify function endpoint not found"
                    500 -> "Internal Server Error: Backend Firebase Admin SDK execution failure"
                    else -> "HTTP : "
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Logger.e("Send notification network error", e)
            Result.failure(e)
        }
    }

    override suspend fun sendTestNotification(payload: NotificationPayload): Result<String> {
        val dto = mapToDto(payload)
        return try {
            val response = apiService.sendTestNotification(dto)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.messageId ?: "test-success")
            } else {
                Result.failure(Exception(response.body()?.error ?: "Test send failed (HTTP )"))
            }
        } catch (e: Exception) {
            Logger.e("Send test notification network error", e)
            Result.failure(e)
        }
    }

    private fun mapToDto(payload: NotificationPayload): NotificationRequestDto {
        return NotificationRequestDto(
            appId = payload.appId,
            firebaseProjectKey = payload.firebaseProjectKey,
            environment = payload.environment.key,
            targetType = payload.targetType.key,
            target = payload.target,
            title = payload.title,
            message = payload.message,
            imageUrl = payload.imageUrl.ifBlank { null },
            clickAction = payload.clickAction.ifBlank { null },
            deepLink = payload.deepLink.ifBlank { null },
            channelId = payload.channelId.ifBlank { null },
            priority = payload.priority.ifBlank { null },
            ttl = payload.ttl,
            collapseKey = payload.collapseKey.ifBlank { null },
            badge = payload.badge,
            customData = payload.customData.ifEmpty { null }
        )
    }

    private fun parseCustomData(json: String): Map<String, String> {
        if (json.isBlank()) return emptyMap()
        val type = object : TypeToken<Map<String, String>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyMap()
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
