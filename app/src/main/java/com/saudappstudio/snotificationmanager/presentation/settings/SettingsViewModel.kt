package com.saudappstudio.snotificationmanager.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.saudappstudio.snotificationmanager.core.datastore.UserPreferences
import com.saudappstudio.snotificationmanager.domain.model.BackendHealthModel
import com.saudappstudio.snotificationmanager.domain.model.ExportDataModel
import com.saudappstudio.snotificationmanager.domain.repository.NotificationRepository
import com.saudappstudio.snotificationmanager.domain.repository.SettingsRepository
import com.saudappstudio.snotificationmanager.domain.usecase.ExportConfigurationUseCase
import com.saudappstudio.snotificationmanager.domain.usecase.ImportConfigurationUseCase
import com.saudappstudio.snotificationmanager.domain.usecase.TestBackendConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val userPreferences: UserPreferences = UserPreferences(),
    val isTestingConnection: Boolean = false,
    val connectionResult: String? = null,
    val isConnectionSuccess: Boolean = false,
    val exportedJson: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val notificationRepository: NotificationRepository,
    private val testBackendConnectionUseCase: TestBackendConnectionUseCase,
    private val exportConfigurationUseCase: ExportConfigurationUseCase,
    private val importConfigurationUseCase: ImportConfigurationUseCase
) : ViewModel() {

    private val _isTestingConnection = MutableStateFlow(false)
    private val _connectionResult = MutableStateFlow<String?>(null)
    private val _isConnectionSuccess = MutableStateFlow(false)
    private val _exportedJson = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.getUserPreferences(),
        _isTestingConnection,
        _connectionResult,
        _isConnectionSuccess,
        _exportedJson
    ) { prefs, testing, result, success, json ->
        SettingsUiState(
            userPreferences = prefs,
            isTestingConnection = testing,
            connectionResult = result,
            isConnectionSuccess = success,
            exportedJson = json
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setBackendUrl(url: String) {
        viewModelScope.launch {
            settingsRepository.setBackendUrl(url)
        }
    }

    fun setApiToken(token: String) {
        viewModelScope.launch {
            settingsRepository.setApiToken(token)
        }
    }

    fun setTestModeOnly(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setTestModeOnly(enabled)
        }
    }

    fun setConfirmBeforeProdSend(required: Boolean) {
        viewModelScope.launch {
            settingsRepository.setConfirmBeforeProdSend(required)
        }
    }

    fun setRequireBiometricForProd(required: Boolean) {
        viewModelScope.launch {
            settingsRepository.setRequireBiometricForProd(required)
        }
    }

    fun testBackendConnection() {
        viewModelScope.launch {
            _isTestingConnection.value = true
            _connectionResult.value = null
            val result = testBackendConnectionUseCase()
            _isTestingConnection.value = false
            if (result.isSuccess) {
                _isConnectionSuccess.value = true
                _connectionResult.value = "Connected! API v${result.getOrNull()?.version ?: "1.0.0"}"
            } else {
                _isConnectionSuccess.value = false
                _connectionResult.value = result.exceptionOrNull()?.localizedMessage ?: "Failed to connect"
            }
        }
    }

    fun clearAllHistory(onCleared: () -> Unit) {
        viewModelScope.launch {
            notificationRepository.clearHistory()
            onCleared()
        }
    }

    fun resetSettings(onReset: () -> Unit) {
        viewModelScope.launch {
            settingsRepository.resetSettings()
            onReset()
        }
    }

    fun exportConfiguration(onExported: (String) -> Unit) {
        viewModelScope.launch {
            val data = exportConfigurationUseCase()
            val json = Gson().toJson(data)
            _exportedJson.value = json
            onExported(json)
        }
    }

    fun importConfiguration(json: String, onImported: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val data = Gson().fromJson(json, ExportDataModel::class.java)
                val result = importConfigurationUseCase(data)
                onImported(result.isSuccess)
            } catch (_: Exception) {
                onImported(false)
            }
        }
    }
}
