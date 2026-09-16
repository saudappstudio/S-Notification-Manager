package com.saudappstudio.snotificationmanager.data.repository

import com.saudappstudio.snotificationmanager.data.local.dao.TopicDao
import com.saudappstudio.snotificationmanager.data.local.entities.TopicEntity
import com.saudappstudio.snotificationmanager.domain.model.TopicModel
import com.saudappstudio.snotificationmanager.domain.repository.TopicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Concrete Room-backed implementation of TopicRepository.
 */
class TopicRepositoryImpl(
    private val topicDao: TopicDao
) : TopicRepository {

    override fun getAllTopics(): Flow<List<TopicModel>> {
        return topicDao.getAllTopics().map { list -> list.map { it.toDomainModel() } }
    }

    override fun getTopicsForApp(appId: String): Flow<List<TopicModel>> {
        return topicDao.getTopicsForApp(appId).map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun getTopicById(id: String): TopicModel? {
        return topicDao.getTopicById(id)?.toDomainModel()
    }

    override suspend fun insertTopic(topic: TopicModel) {
        topicDao.insertTopic(TopicEntity.fromDomain(topic))
    }

    override suspend fun updateTopic(topic: TopicModel) {
        topicDao.updateTopic(TopicEntity.fromDomain(topic))
    }

    override suspend fun deleteTopic(id: String) {
        topicDao.deleteTopicById(id)
    }
}
