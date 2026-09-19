package com.saudappstudio.snotificationmanager.core.datastore

/**
 * Data representation of stored application user preferences and security controls.
 */
data class UserPreferences(
    val themeMode: String = "SYSTEM",
    val backendUrl: String = "https://saudnotificationmanager.netlify.app/.netlify/functions",
    val apiToken: String = "",
    val lastSelectedAppId: String = "",
    val testModeOnly: Boolean = false,
    val confirmBeforeProdSend: Boolean = true,
    val requireBiometricForProd: Boolean = false,
    val requireBiometricOnAppOpen: Boolean = false,
    val defaultPriority: String = "HIGH",
    val defaultChannelId: String = "general_notifications",
    val lastCheckedTimestamp: Long = 0L,
    val lastBackendStatus: String = "DISCONNECTED",
    val cloudinaryCloudName: String = "dvyx3z9vp",
    val cloudinaryUploadPreset: String = "saud_preset"
)
