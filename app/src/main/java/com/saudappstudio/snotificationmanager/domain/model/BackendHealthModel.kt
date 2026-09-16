package com.saudappstudio.snotificationmanager.domain.model

/**
 * Domain model representing Netlify backend health status response.
 */
data class BackendHealthModel(
    val isConnected: Boolean,
    val version: String,
    val timestamp: Long,
    val message: String? = null
)
