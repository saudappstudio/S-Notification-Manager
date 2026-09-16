package com.saudappstudio.snotificationmanager.domain.model

/**
 * Deployment environment classification for applications and notification targets.
 */
enum class Environment(val key: String) {
    DEVELOPMENT("DEVELOPMENT"),
    TESTING("TESTING"),
    PRODUCTION("PRODUCTION");

    companion object {
        /**
         * Resolves an Environment enum from a string key, falling back to DEVELOPMENT.
         */
        fun fromKey(key: String): Environment {
            return entries.find { it.key.equals(key, ignoreCase = true) } ?: DEVELOPMENT
        }
    }
}
