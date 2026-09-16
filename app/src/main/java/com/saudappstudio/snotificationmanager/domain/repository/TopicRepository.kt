package com.saudappstudio.snotificationmanager.domain.repository

import com.saudappstudio.snotificationmanager.domain.model.TopicModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing notification audience topics.
 */
interface TopicRepository {
    fun getAllTopics(): Flow<List<TopicModel>>
    fun getTopicsForApp(appId: String): Flow<List<TopicModel>>
    suspend fun getTopicById(id: String): TopicModel?
    suspend fun insertTopic(topic: TopicModel)
    suspend fun updateTopic(topic: TopicModel)
    suspend fun deleteTopic(id: String)
}
