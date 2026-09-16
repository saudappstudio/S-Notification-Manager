package com.saudappstudio.snotificationmanager.domain.model

/**
 * Domain model representing a Firebase project identifier configuration.
 * Note: Never contains private keys. Serverless backend handles credentials via backendKey.
 */
data class FirebaseProjectModel(
    val id: String,
    val name: String,
    val projectIdentifier: String,
    val environment: Environment = Environment.PRODUCTION,
    val backendKey: String,
    val enabled: Boolean = true
)
