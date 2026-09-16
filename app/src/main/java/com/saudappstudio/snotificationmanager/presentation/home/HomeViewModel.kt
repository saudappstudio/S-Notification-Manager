package com.saudappstudio.snotificationmanager.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saudappstudio.snotificationmanager.core.datastore.UserPreferences
import com.saudappstudio.snotificationmanager.domain.model.AppModel
import com.saudappstudio.snotificationmanager.domain.model.NotificationHistoryModel
import com.saudappstudio.snotificationmanager.domain.repository.AppRepository
import com.saudappstudio.snotificationmanager.domain.repository.NotificationRepository
import com.saudappstudio.snotificationmanager.domain.repository.SettingsRepository
import com.saudappstudio.snotificationmanager.domain.usecase.SeedInitialDataUseCase
import com.saudappstudio.snotificationmanager.domain.usecase.TestBackendConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val apps: List<AppModel> = emptyList(),
    val recentNotifications: List<NotificationHistoryModel> = emptyList(),
    val userPreferences: UserPreferences = UserPreferences(),
    val isCheckingHealth: Boolean = false,
    val isConnected: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val notificationRepository: NotificationRepository,
    private val settingsRepository: SettingsRepository,
    private val testBackendConnectionUseCase: TestBackendConnectionUseCase,
    private val seedInitialDataUseCase: SeedInitialDataUseCase
) : ViewModel() {

    private val _isCheckingHealth = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        appRepository.getAllApps(),
        notificationRepository.getAllHistory(),
        settingsRepository.getUserPreferences(),
        _isCheckingHealth
    ) { apps, history, prefs, checking ->
        HomeUiState(
            apps = apps.take(4),
            recentNotifications = history.take(3),
            userPreferences = prefs,
            isCheckingHealth = checking,
            isConnected = prefs.lastBackendStatus == "CONNECTED"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    init {
        viewModelScope.launch {
            seedInitialDataUseCase()
            checkBackendConnection()
        }
    }

    fun checkBackendConnection() {
        viewModelScope.launch {
            _isCheckingHealth.value = true
            testBackendConnectionUseCase()
            _isCheckingHealth.value = false
        }
    }
}
