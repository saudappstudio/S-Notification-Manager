package com.saudappstudio.snotificationmanager.presentation.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saudappstudio.snotificationmanager.domain.model.AppModel
import com.saudappstudio.snotificationmanager.domain.model.FirebaseProjectModel
import com.saudappstudio.snotificationmanager.domain.model.NotificationHistoryModel
import com.saudappstudio.snotificationmanager.domain.model.TopicModel
import com.saudappstudio.snotificationmanager.domain.repository.AppRepository
import com.saudappstudio.snotificationmanager.domain.repository.FirebaseProjectRepository
import com.saudappstudio.snotificationmanager.domain.repository.NotificationRepository
import com.saudappstudio.snotificationmanager.domain.repository.TopicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppsUiState(
    val apps: List<AppModel> = emptyList(),
    val filteredApps: List<AppModel> = emptyList(),
    val searchQuery: String = "",
    val projects: List<FirebaseProjectModel> = emptyList(),
    val topics: List<TopicModel> = emptyList(),
    val selectedApp: AppModel? = null,
    val appNotifications: List<NotificationHistoryModel> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: String? = null
)

@HiltViewModel
class AppsViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val firebaseProjectRepository: FirebaseProjectRepository,
    private val topicRepository: TopicRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedApp = MutableStateFlow<AppModel?>(null)
    private val _appNotifications = MutableStateFlow<List<NotificationHistoryModel>>(emptyList())
    private val _userMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AppsUiState> = combine(
        combine(
            appRepository.getAllApps(),
            firebaseProjectRepository.getAllProjects(),
            topicRepository.getAllTopics()
        ) { apps, projects, topics ->
            Triple(apps, projects, topics)
        },
        _searchQuery,
        _selectedApp,
        _appNotifications,
        _userMessage
    ) { (apps, projects, topics), query, selected, appNotifications, message ->
        val filtered = if (query.isBlank()) {
            apps
        } else {
            apps.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.packageName.contains(query, ignoreCase = true) ||
                it.defaultTopic.contains(query, ignoreCase = true)
            }
        }
        AppsUiState(
            apps = apps,
            filteredApps = filtered,
            searchQuery = query,
            projects = projects,
            topics = topics,
            selectedApp = selected,
            appNotifications = appNotifications,
            userMessage = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppsUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun loadAppDetails(appId: String) {
        viewModelScope.launch {
            _selectedApp.value = appRepository.getAppById(appId)
        }
        viewModelScope.launch {
            notificationRepository.getHistoryByApp(appId).collect { historyList ->
                _appNotifications.value = historyList
            }
        }
    }

    fun saveApp(app: AppModel, onSaved: () -> Unit) {
        viewModelScope.launch {
            val existing = appRepository.getAppById(app.id)
            if (existing != null) {
                appRepository.updateApp(app.copy(updatedAt = System.currentTimeMillis()))
            } else {
                appRepository.insertApp(app)
            }
            _userMessage.value = "Application saved successfully"
            onSaved()
        }
    }

    fun updateAppControls(
        appId: String,
        enabled: Boolean,
        testMode: Boolean,
        allowProd: Boolean,
        requireConfirm: Boolean
    ) {
        viewModelScope.launch {
            val app = appRepository.getAppById(appId) ?: return@launch
            val updated = app.copy(
                enabled = enabled,
                testMode = testMode,
                allowPush = allowProd,
                requireConfirmForProd = requireConfirm,
                updatedAt = System.currentTimeMillis()
            )
            appRepository.updateApp(updated)
            _selectedApp.value = updated
        }
    }

    fun deleteApp(appId: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            appRepository.deleteApp(appId)
            _selectedApp.value = null
            _appNotifications.value = emptyList()
            onDeleted()
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }
}
