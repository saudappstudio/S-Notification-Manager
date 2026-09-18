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
import java.util.UUID

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
            list.map { entityToModel(it) }
        }
    }

    override fun getHistoryByApp(appId: String): Flow<List<NotificationHistoryModel>> {
        return notificationHistoryDao.getHistoryByApp(appId).map { list ->
            list.map { entityToModel(it) }
        }
    }

    override suspend fun getHistoryById(id: String): NotificationHistoryModel? {
        val entity = notificationHistoryDao.getHistoryById(id) ?: return null
        return entityToModel(entity)
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
            notificationType = history.notificationType,
            eventTrigger = history.eventTrigger,
            isScheduled = history.isScheduled,
            scheduledTimestamp = history.scheduledTimestamp,
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
                    val msgId = body.messageId ?: "success"
                    Logger.i("Notification Sent Successfully: messageId=$msgId")
                    Result.success(msgId)
                } else {
                    val err = body?.error ?: "Backend returned unsuccessful response"
                    Logger.e("Send Notification API Error: $err")
                    Result.failure(Exception(err))
                }
            } else {
                val rawErrorBody = response.errorBody()?.string() ?: response.message()
                val errorMsg = when (response.code()) {
                    401 -> "Unauthorized (401): Check API authentication token in Settings"
                    403 -> "Forbidden (403): You do not have permission to send this notification"
                    404 -> "Not Found (404): Netlify function endpoint not found"
                    500 -> "Internal Server Error (500): Firebase Admin execution failure - $rawErrorBody"
                    else -> "HTTP ${response.code()}: $rawErrorBody"
                }
                Logger.e("Send Notification HTTP ${response.code()} Failure: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Logger.e("Send notification network exception: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    override suspend fun scheduleNotification(appName: String, payload: NotificationPayload): Result<String> {
        return try {
            val scheduledTime = payload.scheduledTimestamp ?: (System.currentTimeMillis() + payload.scheduleDelayMinutes * 60 * 1000L)
            val scheduleId = "sched_${UUID.randomUUID().toString().take(8)}"

            // Log scheduled record to history
            val historyRecord = NotificationHistoryModel(
                id = UUID.randomUUID().toString(),
                appId = payload.appId,
                appName = appName,
                title = payload.title,
                message = payload.message,
                targetType = payload.targetType,
                target = payload.target,
                environment = payload.environment,
                status = "SCHEDULED",
                messageId = scheduleId,
                error = null,
                imageUrl = payload.imageUrl,
                clickAction = payload.clickAction,
                deepLink = payload.deepLink,
                notificationType = payload.notificationType,
                eventTrigger = payload.eventTrigger,
                isScheduled = true,
                scheduledTimestamp = scheduledTime,
                customData = payload.customData,
                sentAt = scheduledTime
            )
            insertHistory(historyRecord)

            Logger.i("Notification scheduled successfully for $appName at timestamp $scheduledTime")
            Result.success(scheduleId)
        } catch (e: Exception) {
            Logger.e("Failed to schedule notification: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    override suspend fun sendTestNotification(payload: NotificationPayload): Result<String> {
        val dto = mapToDto(payload)
        return try {
            val response = apiService.sendTestNotification(dto)
            if (response.isSuccessful && response.body()?.success == true) {
                val msgId = response.body()?.messageId ?: "test-success"
                Logger.i("Test Notification Sent Successfully: messageId=$msgId")
                Result.success(msgId)
            } else {
                val rawError = response.errorBody()?.string() ?: response.body()?.error ?: "HTTP ${response.code()}"
                Logger.e("Test Notification Failed: $rawError")
                Result.failure(Exception(rawError))
            }
        } catch (e: Exception) {
            Logger.e("Send test notification network exception: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    private fun entityToModel(entity: NotificationHistoryEntity): NotificationHistoryModel {
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
            notificationType = entity.notificationType,
            eventTrigger = entity.eventTrigger,
            isScheduled = entity.isScheduled,
            scheduledTimestamp = entity.scheduledTimestamp,
            customData = parseCustomData(entity.customDataJson),
            sentAt = entity.sentAt
        )
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
            notificationType = payload.notificationType,
            eventTrigger = payload.eventTrigger.ifBlank { null },
            isScheduled = payload.isScheduled,
            scheduledTimestamp = payload.scheduledTimestamp,
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
