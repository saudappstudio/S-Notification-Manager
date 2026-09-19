package com.saudappstudio.snotificationmanager.domain.usecase

import com.saudappstudio.snotificationmanager.domain.model.Environment
import com.saudappstudio.snotificationmanager.domain.model.NotificationHistoryModel
import com.saudappstudio.snotificationmanager.domain.model.NotificationPayload
import com.saudappstudio.snotificationmanager.domain.repository.NotificationRepository
import com.saudappstudio.snotificationmanager.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

/**
 * UseCase to dispatch or schedule a push / in-app notification to the Netlify backend and record the result in Room history.
 * Enforces safety controls: blocks production sends if Test Mode Only is enabled.
 */
class SendNotificationUseCase(
    private val notificationRepository: NotificationRepository,
    private val settingsRepository: SettingsRepository
) {
    /**
     * Dispatches or schedules notification and persists history record.
     *
     * @param appName Client application display name for history tracking.
     * @param payload Target payload.
     * @param isTest Whether this is explicitly a test dispatch.
     * @return Result containing backend message ID or failure.
     */
    suspend operator fun invoke(
        appName: String,
        payload: NotificationPayload,
        isTest: Boolean = false
    ): Result<String> {
        val userPrefs = settingsRepository.getUserPreferences().first()

        // Enforce safety rule: if Test Mode Only is enabled, block production sends
        if (payload.environment == Environment.PRODUCTION && userPrefs.testModeOnly) {
            return Result.failure(IllegalStateException("Production dispatch blocked by Test Mode Only"))
        }

        // Check if scheduling requested
        if (payload.isScheduled) {
            val scheduleResult = notificationRepository.scheduleNotification(appName, payload)
            settingsRepository.setLastSelectedAppId(payload.appId)
            return scheduleResult
        }

        val result = if (isTest) {
            notificationRepository.sendTestNotification(payload)
        } else {
            notificationRepository.sendNotification(payload)
        }

        val historyItem = NotificationHistoryModel(
            id = UUID.randomUUID().toString(),
            appId = payload.appId,
            appName = appName,
            title = payload.title,
            message = payload.message,
            targetType = payload.targetType,
            target = payload.target,
            environment = payload.environment,
            status = if (result.isSuccess) "SENT" else "FAILED",
            messageId = result.getOrNull(),
            error = result.exceptionOrNull()?.localizedMessage,
            imageUrl = payload.imageUrl,
            clickAction = payload.clickAction,
            deepLink = payload.deepLink,
            notificationType = payload.notificationType,
            eventTrigger = payload.eventTrigger,
            isScheduled = false,
            scheduledTimestamp = null,
            customData = payload.customData,
            sentAt = System.currentTimeMillis()
        )

        // Save history item locally regardless of delivery result
        notificationRepository.insertHistory(historyItem)

        // Remember last used app
        settingsRepository.setLastSelectedAppId(payload.appId)

        return result
    }
}
