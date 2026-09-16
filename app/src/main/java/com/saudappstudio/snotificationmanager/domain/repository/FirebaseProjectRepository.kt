package com.saudappstudio.snotificationmanager.domain.repository

import com.saudappstudio.snotificationmanager.domain.model.FirebaseProjectModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing Firebase project backend configuration mappings.
 */
interface FirebaseProjectRepository {
    fun getAllProjects(): Flow<List<FirebaseProjectModel>>
    suspend fun getProjectById(id: String): FirebaseProjectModel?
    suspend fun insertProject(project: FirebaseProjectModel)
    suspend fun updateProject(project: FirebaseProjectModel)
    suspend fun deleteProject(id: String)
}
