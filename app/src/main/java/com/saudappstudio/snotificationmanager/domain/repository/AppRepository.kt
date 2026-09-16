package com.saudappstudio.snotificationmanager.domain.repository

import com.saudappstudio.snotificationmanager.domain.model.AppModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing registered client applications.
 */
interface AppRepository {
    fun getAllApps(): Flow<List<AppModel>>
    suspend fun getAppById(id: String): AppModel?
    suspend fun insertApp(app: AppModel)
    suspend fun updateApp(app: AppModel)
    suspend fun deleteApp(id: String)
}
