package com.saudappstudio.snotificationmanager.domain.repository

import com.saudappstudio.snotificationmanager.domain.model.TemplateModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing reusable notification templates.
 */
interface TemplateRepository {
    fun getAllTemplates(): Flow<List<TemplateModel>>
    suspend fun getTemplateById(id: String): TemplateModel?
    suspend fun insertTemplate(template: TemplateModel)
    suspend fun updateTemplate(template: TemplateModel)
    suspend fun deleteTemplate(id: String)
    suspend fun toggleFavorite(id: String)
}
