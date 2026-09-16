package com.saudappstudio.snotificationmanager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saudappstudio.snotificationmanager.domain.model.TemplateModel

/**
 * Room database entity storing reusable notification templates.
 */
@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey val id: String,
    val name: String,
    val appId: String,
    val title: String,
    val message: String,
    val imageUrl: String,
    val topic: String,
    val clickAction: String,
    val deepLink: String,
    val customDataJson: String,
    val isFavorite: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
