package com.saudappstudio.snotificationmanager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saudappstudio.snotificationmanager.domain.model.Environment
import com.saudappstudio.snotificationmanager.domain.model.TopicModel

/**
 * Room database entity storing audience topics.
 */
@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val appId: String,
    val environment: String,
    val enabled: Boolean
) {
    fun toDomainModel(): TopicModel = TopicModel(
        id = id,
        name = name,
        description = description,
        appId = appId,
        environment = Environment.fromKey(environment),
        enabled = enabled
    )

    companion object {
        fun fromDomain(model: TopicModel): TopicEntity = TopicEntity(
            id = model.id,
            name = model.name,
            description = model.description,
            appId = model.appId,
            environment = model.environment.key,
            enabled = model.enabled
        )
    }
}
