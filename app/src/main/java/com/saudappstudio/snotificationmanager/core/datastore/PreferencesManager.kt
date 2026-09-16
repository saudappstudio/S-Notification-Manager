package com.saudappstudio.snotificationmanager.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.saudappstudio.snotificationmanager.core.logging.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "saud_notification_preferences")

/**
 * Encapsulates DataStore operations for secure preferences, backend configs, and safety settings.
 */
class PreferencesManager(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val BACKEND_URL = stringPreferencesKey("backend_url")
        val API_TOKEN = stringPreferencesKey("api_token")
        val LAST_SELECTED_APP_ID = stringPreferencesKey("last_selected_app_id")
        val TEST_MODE_ONLY = booleanPreferencesKey("test_mode_only")
        val CONFIRM_BEFORE_PROD_SEND = booleanPreferencesKey("confirm_before_prod_send")
        val REQUIRE_BIOMETRIC_FOR_PROD = booleanPreferencesKey("require_biometric_for_prod")
        val DEFAULT_PRIORITY = stringPreferencesKey("default_priority")
        val DEFAULT_CHANNEL_ID = stringPreferencesKey("default_channel_id")
        val LAST_CHECKED_TIMESTAMP = longPreferencesKey("last_checked_timestamp")
        val LAST_BACKEND_STATUS = stringPreferencesKey("last_backend_status")
    }

    /**
     * Flow emitting the latest UserPreferences snapshot.
     */
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Logger.e("Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                themeMode = preferences[PreferencesKeys.THEME_MODE] ?: "SYSTEM",
                backendUrl = preferences[PreferencesKeys.BACKEND_URL] ?: "https://your-site.netlify.app/.netlify/functions",
                apiToken = preferences[PreferencesKeys.API_TOKEN] ?: "",
                lastSelectedAppId = preferences[PreferencesKeys.LAST_SELECTED_APP_ID] ?: "",
                testModeOnly = preferences[PreferencesKeys.TEST_MODE_ONLY] ?: false,
                confirmBeforeProdSend = preferences[PreferencesKeys.CONFIRM_BEFORE_PROD_SEND] ?: true,
                requireBiometricForProd = preferences[PreferencesKeys.REQUIRE_BIOMETRIC_FOR_PROD] ?: false,
                defaultPriority = preferences[PreferencesKeys.DEFAULT_PRIORITY] ?: "HIGH",
                defaultChannelId = preferences[PreferencesKeys.DEFAULT_CHANNEL_ID] ?: "general_notifications",
                lastCheckedTimestamp = preferences[PreferencesKeys.LAST_CHECKED_TIMESTAMP] ?: 0L,
                lastBackendStatus = preferences[PreferencesKeys.LAST_BACKEND_STATUS] ?: "DISCONNECTED"
            )
        }

    /**
     * Updates the UI theme preference (SYSTEM, LIGHT, or DARK).
     *
     * @param themeMode Desired theme mode identifier.
     */
    suspend fun setThemeMode(themeMode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode
        }
    }

    /**
     * Updates the Netlify backend endpoint base URL.
     *
     * @param url Netlify Functions base URL.
     */
    suspend fun setBackendUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BACKEND_URL] = url
        }
    }

    /**
     * Updates the API bearer authentication secret token.
     *
     * @param token Secret token.
     */
    suspend fun setApiToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.API_TOKEN] = token
        }
    }

    /**
     * Remembers the last selected application identifier for the Send screen.
     *
     * @param appId Application unique ID.
     */
    suspend fun setLastSelectedAppId(appId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SELECTED_APP_ID] = appId
        }
    }

    /**
     * Toggles the global Test Mode Only safety control.
     *
     * @param enabled True to disable all production sending.
     */
    suspend fun setTestModeOnly(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TEST_MODE_ONLY] = enabled
        }
    }

    /**
     * Toggles whether confirmation dialog is required before production send.
     *
     * @param required True if confirmation is required.
     */
    suspend fun setConfirmBeforeProdSend(required: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CONFIRM_BEFORE_PROD_SEND] = required
        }
    }

    /**
     * Toggles biometric lock for sensitive production sending and configuration inspection.
     *
     * @param required True if biometric authentication is required.
     */
    suspend fun setRequireBiometricForProd(required: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REQUIRE_BIOMETRIC_FOR_PROD] = required
        }
    }

    /**
     * Updates default notification channel and priority.
     *
     * @param channelId Default channel name.
     * @param priority Default priority level.
     */
    suspend fun setDefaultNotificationSettings(channelId: String, priority: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_CHANNEL_ID] = channelId
            preferences[PreferencesKeys.DEFAULT_PRIORITY] = priority
        }
    }

    /**
     * Records timestamp and status of backend connectivity check.
     *
     * @param timestamp System epoch milliseconds.
     * @param status String status ("CONNECTED", "DISCONNECTED", etc.)
     */
    suspend fun setBackendHealthStatus(timestamp: Long, status: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_CHECKED_TIMESTAMP] = timestamp
            preferences[PreferencesKeys.LAST_BACKEND_STATUS] = status
        }
    }

    /**
     * Resets all stored preferences to defaults.
     */
    suspend fun resetPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
