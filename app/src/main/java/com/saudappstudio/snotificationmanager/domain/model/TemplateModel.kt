package com.saudappstudio.snotificationmanager.domain.model

/**
 * Domain model representing a reusable notification template.
 */
data class TemplateModel(
    val id: String,
    val name: String,
    val appId: String,
    val title: String,
    val message: String,
    val imageUrl: String = "",
    val topic: String = "",
    val clickAction: String = "OPEN_APP",
    val deepLink: String = "",
    val customData: Map<String, String> = emptyMap(),
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
