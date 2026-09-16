package com.saudappstudio.snotificationmanager.domain.model

/**
 * Recipient audience type for an FCM push notification.
 */
enum class TargetType(val key: String) {
    TOPIC("TOPIC"),
    TOKEN("TOKEN");

    companion object {
        /**
         * Resolves a TargetType from a string key, falling back to TOPIC.
         */
        fun fromKey(key: String): TargetType {
            return entries.find { it.key.equals(key, ignoreCase = true) } ?: TOPIC
        }
    }
}
