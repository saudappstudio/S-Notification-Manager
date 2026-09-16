package com.saudappstudio.snotificationmanager.domain.model

/**
 * Domain model representing a pub/sub FCM topic.
 */
data class TopicModel(
    val id: String,
    val name: String,
    val description: String,
    val appId: String,
    val environment: Environment = Environment.PRODUCTION,
    val enabled: Boolean = true
)
