package com.saudappstudio.snotificationmanager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saudappstudio.snotificationmanager.domain.model.AppModel
import com.saudappstudio.snotificationmanager.domain.model.Environment

/**
 * Room database entity storing application configurations.
 */
@Entity(tableName = "apps")
data class AppEntity(
    @PrimaryKey val id: String,
    val name: String,
    val packageName: String,
    val appId: String,
    val iconName: String,
    val firebaseProjectId: String,
    val environment: String,
    val defaultTopic: String,
    val enabled: Boolean,
    val testMode: Boolean,
    val allowPush: Boolean,
    val allowTopic: Boolean,
    val allowToken: Boolean,
    val allowImage: Boolean,
    val allowDeepLinks: Boolean,
    val requireConfirmForProd: Boolean,
    val defaultClickAction: String,
    val defaultChannelId: String,
    val description: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomainModel(): AppModel = AppModel(
        id = id,
        name = name,
        packageName = packageName,
        appId = appId,
        iconName = iconName,
        firebaseProjectId = firebaseProjectId,
        environment = Environment.fromKey(environment),
        defaultTopic = defaultTopic,
        enabled = enabled,
        testMode = testMode,
        allowPush = allowPush,
        allowTopic = allowTopic,
        allowToken = allowToken,
        allowImage = allowImage,
        allowDeepLinks = allowDeepLinks,
        requireConfirmForProd = requireConfirmForProd,
        defaultClickAction = defaultClickAction,
        defaultChannelId = defaultChannelId,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(model: AppModel): AppEntity = AppEntity(
            id = model.id,
            name = model.name,
            packageName = model.packageName,
            appId = model.appId,
            iconName = model.iconName,
            firebaseProjectId = model.firebaseProjectId,
            environment = model.environment.key,
            defaultTopic = model.defaultTopic,
            enabled = model.enabled,
            testMode = model.testMode,
            allowPush = model.allowPush,
            allowTopic = model.allowTopic,
            allowToken = model.allowToken,
            allowImage = model.allowImage,
            allowDeepLinks = model.allowDeepLinks,
            requireConfirmForProd = model.requireConfirmForProd,
            defaultClickAction = model.defaultClickAction,
            defaultChannelId = model.defaultChannelId,
            description = model.description,
            createdAt = model.createdAt,
            updatedAt = model.updatedAt
        )
    }
}
