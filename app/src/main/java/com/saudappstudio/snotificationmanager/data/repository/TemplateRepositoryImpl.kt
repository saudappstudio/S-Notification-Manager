package com.saudappstudio.snotificationmanager.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.saudappstudio.snotificationmanager.data.local.dao.TemplateDao
import com.saudappstudio.snotificationmanager.data.local.entities.TemplateEntity
import com.saudappstudio.snotificationmanager.domain.model.TemplateModel
import com.saudappstudio.snotificationmanager.domain.repository.TemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Concrete Room-backed implementation of TemplateRepository with JSON custom data conversion.
 */
class TemplateRepositoryImpl(
    private val templateDao: TemplateDao
) : TemplateRepository {
    private val gson = Gson()

    override fun getAllTemplates(): Flow<List<TemplateModel>> {
        return templateDao.getAllTemplates().map { list ->
            list.map { entity ->
                TemplateModel(
                    id = entity.id,
                    name = entity.name,
                    appId = entity.appId,
                    title = entity.title,
                    message = entity.message,
                    imageUrl = entity.imageUrl,
                    topic = entity.topic,
                    clickAction = entity.clickAction,
                    deepLink = entity.deepLink,
                    customData = parseCustomData(entity.customDataJson),
                    isFavorite = entity.isFavorite,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt
                )
            }
        }
    }

    override suspend fun getTemplateById(id: String): TemplateModel? {
        val entity = templateDao.getTemplateById(id) ?: return null
        return TemplateModel(
            id = entity.id,
            name = entity.name,
            appId = entity.appId,
            title = entity.title,
            message = entity.message,
            imageUrl = entity.imageUrl,
            topic = entity.topic,
            clickAction = entity.clickAction,
            deepLink = entity.deepLink,
            customData = parseCustomData(entity.customDataJson),
            isFavorite = entity.isFavorite,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    override suspend fun insertTemplate(template: TemplateModel) {
        val entity = TemplateEntity(
            id = template.id,
            name = template.name,
            appId = template.appId,
            title = template.title,
            message = template.message,
            imageUrl = template.imageUrl,
            topic = template.topic,
            clickAction = template.clickAction,
            deepLink = template.deepLink,
            customDataJson = gson.toJson(template.customData),
            isFavorite = template.isFavorite,
            createdAt = template.createdAt,
            updatedAt = template.updatedAt
        )
        templateDao.insertTemplate(entity)
    }

    override suspend fun updateTemplate(template: TemplateModel) {
        val entity = TemplateEntity(
            id = template.id,
            name = template.name,
            appId = template.appId,
            title = template.title,
            message = template.message,
            imageUrl = template.imageUrl,
            topic = template.topic,
            clickAction = template.clickAction,
            deepLink = template.deepLink,
            customDataJson = gson.toJson(template.customData),
            isFavorite = template.isFavorite,
            createdAt = template.createdAt,
            updatedAt = template.updatedAt
        )
        templateDao.updateTemplate(entity)
    }

    override suspend fun deleteTemplate(id: String) {
        templateDao.deleteTemplateById(id)
    }

    override suspend fun toggleFavorite(id: String) {
        templateDao.toggleFavorite(id)
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
