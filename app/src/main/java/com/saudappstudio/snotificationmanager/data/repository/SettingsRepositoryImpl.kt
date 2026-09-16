package com.saudappstudio.snotificationmanager.data.repository

import com.saudappstudio.snotificationmanager.core.datastore.PreferencesManager
import com.saudappstudio.snotificationmanager.core.datastore.UserPreferences
import com.saudappstudio.snotificationmanager.core.logging.Logger
import com.saudappstudio.snotificationmanager.data.local.dao.AppDao
import com.saudappstudio.snotificationmanager.data.local.dao.FirebaseProjectDao
import com.saudappstudio.snotificationmanager.data.local.dao.TemplateDao
import com.saudappstudio.snotificationmanager.data.local.dao.TopicDao
import com.saudappstudio.snotificationmanager.data.local.entities.AppEntity
import com.saudappstudio.snotificationmanager.data.local.entities.FirebaseProjectEntity
import com.saudappstudio.snotificationmanager.data.local.entities.TemplateEntity
import com.saudappstudio.snotificationmanager.data.local.entities.TopicEntity
import com.saudappstudio.snotificationmanager.data.remote.api.NetlifyApiService
import com.saudappstudio.snotificationmanager.domain.model.BackendHealthModel
import com.saudappstudio.snotificationmanager.domain.model.ExportDataModel
import com.saudappstudio.snotificationmanager.domain.repository.SettingsRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Concrete implementation of SettingsRepository handling preferences, health checks, and backup export/import.
 */
class SettingsRepositoryImpl(
    private val preferencesManager: PreferencesManager,
    private val apiService: NetlifyApiService,
    private val appDao: AppDao,
    private val firebaseProjectDao: FirebaseProjectDao,
    private val topicDao: TopicDao,
    private val templateDao: TemplateDao
) : SettingsRepository {
    private val gson = Gson()

    override fun getUserPreferences(): Flow<UserPreferences> = preferencesManager.userPreferencesFlow

    override suspend fun setThemeMode(mode: String) {
        preferencesManager.setThemeMode(mode)
    }

    override suspend fun setBackendUrl(url: String) {
        preferencesManager.setBackendUrl(url)
    }

    override suspend fun setApiToken(token: String) {
        preferencesManager.setApiToken(token)
    }

    override suspend fun setLastSelectedAppId(appId: String) {
        preferencesManager.setLastSelectedAppId(appId)
    }

    override suspend fun setTestModeOnly(enabled: Boolean) {
        preferencesManager.setTestModeOnly(enabled)
    }

    override suspend fun setConfirmBeforeProdSend(required: Boolean) {
        preferencesManager.setConfirmBeforeProdSend(required)
    }

    override suspend fun setRequireBiometricForProd(required: Boolean) {
        preferencesManager.setRequireBiometricForProd(required)
    }

    override suspend fun setDefaultNotificationSettings(channelId: String, priority: String) {
        preferencesManager.setDefaultNotificationSettings(channelId, priority)
    }

    override suspend fun checkBackendHealth(): Result<BackendHealthModel> {
        return try {
            val response = apiService.checkHealth()
            if (response.isSuccessful) {
                val body = response.body()
                val model = BackendHealthModel(
                    isConnected = true,
                    version = body?.version ?: "1.0.0",
                    timestamp = System.currentTimeMillis(),
                    message = body?.status ?: "OK"
                )
                preferencesManager.setBackendHealthStatus(model.timestamp, "CONNECTED")
                Result.success(model)
            } else {
                preferencesManager.setBackendHealthStatus(System.currentTimeMillis(), "DISCONNECTED")
                Result.failure(Exception("HTTP : "))
            }
        } catch (e: Exception) {
            Logger.e("Backend health check failed", e)
            preferencesManager.setBackendHealthStatus(System.currentTimeMillis(), "DISCONNECTED")
            Result.failure(e)
        }
    }

    override suspend fun resetSettings() {
        preferencesManager.resetPreferences()
    }

    override suspend fun exportData(): ExportDataModel {
        val apps = appDao.getAllApps().first().map { it.toDomainModel() }
        val projects = firebaseProjectDao.getAllProjects().first().map { it.toDomainModel() }
        val topics = topicDao.getAllTopics().first().map { it.toDomainModel() }
        val templates = templateDao.getAllTemplates().first().map { entity ->
            val customData = try {
                gson.fromJson(entity.customDataJson, Map::class.java) as? Map<String, String> ?: emptyMap()
            } catch (_: Exception) {
                emptyMap()
            }
            com.saudappstudio.snotificationmanager.domain.model.TemplateModel(
                id = entity.id,
                name = entity.name,
                appId = entity.appId,
                title = entity.title,
                message = entity.message,
                imageUrl = entity.imageUrl,
                topic = entity.topic,
                clickAction = entity.clickAction,
                deepLink = entity.deepLink,
                customData = customData,
                isFavorite = entity.isFavorite,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }

        return ExportDataModel(
            exportVersion = 1,
            exportedAt = System.currentTimeMillis(),
            apps = apps,
            firebaseProjects = projects,
            topics = topics,
            templates = templates
        )
    }

    override suspend fun importData(data: ExportDataModel): Result<Unit> {
        return try {
            appDao.insertAll(data.apps.map { AppEntity.fromDomain(it) })
            firebaseProjectDao.insertAll(data.firebaseProjects.map { FirebaseProjectEntity.fromDomain(it) })
            topicDao.insertAll(data.topics.map { TopicEntity.fromDomain(it) })
            templateDao.insertAll(data.templates.map { tpl ->
                TemplateEntity(
                    id = tpl.id,
                    name = tpl.name,
                    appId = tpl.appId,
                    title = tpl.title,
                    message = tpl.message,
                    imageUrl = tpl.imageUrl,
                    topic = tpl.topic,
                    clickAction = tpl.clickAction,
                    deepLink = tpl.deepLink,
                    customDataJson = gson.toJson(tpl.customData),
                    isFavorite = tpl.isFavorite,
                    createdAt = tpl.createdAt,
                    updatedAt = tpl.updatedAt
                )
            })
            Result.success(Unit)
        } catch (e: Exception) {
            Logger.e("Import configuration failed", e)
            Result.failure(e)
        }
    }
}
