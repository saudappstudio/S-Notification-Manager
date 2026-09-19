package com.saudappstudio.snotificationmanager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saudappstudio.snotificationmanager.domain.model.Environment
import com.saudappstudio.snotificationmanager.domain.model.FirebaseProjectModel

/**
 * Room database entity storing Firebase project backend mappings.
 */
@Entity(tableName = "firebase_projects")
data class FirebaseProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val projectIdentifier: String,
    val environment: String,
    val backendKey: String,
    val gaPropertyId: String = "",
    val enabled: Boolean
) {
    fun toDomainModel(): FirebaseProjectModel = FirebaseProjectModel(
        id = id,
        name = name,
        projectIdentifier = projectIdentifier,
        environment = Environment.fromKey(environment),
        backendKey = backendKey,
        gaPropertyId = gaPropertyId,
        enabled = enabled
    )

    companion object {
        fun fromDomain(model: FirebaseProjectModel): FirebaseProjectEntity = FirebaseProjectEntity(
            id = model.id,
            name = model.name,
            projectIdentifier = model.projectIdentifier,
            environment = model.environment.key,
            backendKey = model.backendKey,
            gaPropertyId = model.gaPropertyId,
            enabled = model.enabled
        )
    }
}
