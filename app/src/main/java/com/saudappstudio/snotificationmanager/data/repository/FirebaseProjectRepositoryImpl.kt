package com.saudappstudio.snotificationmanager.data.repository

import com.saudappstudio.snotificationmanager.data.local.dao.FirebaseProjectDao
import com.saudappstudio.snotificationmanager.data.local.entities.FirebaseProjectEntity
import com.saudappstudio.snotificationmanager.domain.model.FirebaseProjectModel
import com.saudappstudio.snotificationmanager.domain.repository.FirebaseProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Concrete Room-backed implementation of FirebaseProjectRepository.
 */
class FirebaseProjectRepositoryImpl(
    private val firebaseProjectDao: FirebaseProjectDao
) : FirebaseProjectRepository {

    override fun getAllProjects(): Flow<List<FirebaseProjectModel>> {
        return firebaseProjectDao.getAllProjects().map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun getProjectById(id: String): FirebaseProjectModel? {
        return firebaseProjectDao.getProjectById(id)?.toDomainModel()
    }

    override suspend fun insertProject(project: FirebaseProjectModel) {
        firebaseProjectDao.insertProject(FirebaseProjectEntity.fromDomain(project))
    }

    override suspend fun updateProject(project: FirebaseProjectModel) {
        firebaseProjectDao.updateProject(FirebaseProjectEntity.fromDomain(project))
    }

    override suspend fun deleteProject(id: String) {
        firebaseProjectDao.deleteProjectById(id)
    }
}
