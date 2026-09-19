package com.saudappstudio.snotificationmanager.domain.repository

import com.saudappstudio.snotificationmanager.core.datastore.UserPreferences
import com.saudappstudio.snotificationmanager.domain.model.BackendHealthModel
import com.saudappstudio.snotificationmanager.domain.model.ExportDataModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for application settings, backend connectivity checks, and data export/import.
 */
interface SettingsRepository {
    fun getUserPreferences(): Flow<UserPreferences>
    suspend fun setThemeMode(mode: String)
    suspend fun setBackendUrl(url: String)
    suspend fun setApiToken(token: String)
    suspend fun setLastSelectedAppId(appId: String)
    suspend fun setTestModeOnly(enabled: Boolean)
    suspend fun setConfirmBeforeProdSend(required: Boolean)
    suspend fun setRequireBiometricForProd(required: Boolean)
    suspend fun setRequireBiometricOnAppOpen(required: Boolean)
    suspend fun setDefaultNotificationSettings(channelId: String, priority: String)
    suspend fun checkBackendHealth(): Result<BackendHealthModel>
    suspend fun resetSettings()
    suspend fun exportData(): ExportDataModel
    suspend fun importData(data: ExportDataModel): Result<Unit>
}
