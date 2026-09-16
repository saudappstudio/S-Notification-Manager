package com.saudappstudio.snotificationmanager.data.repository

import com.saudappstudio.snotificationmanager.data.local.dao.AppDao
import com.saudappstudio.snotificationmanager.data.local.entities.AppEntity
import com.saudappstudio.snotificationmanager.domain.model.AppModel
import com.saudappstudio.snotificationmanager.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Concrete Room-backed implementation of AppRepository.
 */
class AppRepositoryImpl(
    private val appDao: AppDao
) : AppRepository {

    override fun getAllApps(): Flow<List<AppModel>> {
        return appDao.getAllApps().map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun getAppById(id: String): AppModel? {
        return appDao.getAppById(id)?.toDomainModel()
    }

    override suspend fun insertApp(app: AppModel) {
        appDao.insertApp(AppEntity.fromDomain(app))
    }

    override suspend fun updateApp(app: AppModel) {
        appDao.updateApp(AppEntity.fromDomain(app))
    }

    override suspend fun deleteApp(id: String) {
        appDao.deleteAppById(id)
    }
}
