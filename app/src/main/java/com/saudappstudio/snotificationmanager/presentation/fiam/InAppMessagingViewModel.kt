package com.saudappstudio.snotificationmanager.presentation.fiam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saudappstudio.snotificationmanager.core.datastore.PreferencesManager
import com.saudappstudio.snotificationmanager.core.datastore.UserPreferences
import com.saudappstudio.snotificationmanager.core.logging.Logger
import com.saudappstudio.snotificationmanager.domain.model.AppModel
import com.saudappstudio.snotificationmanager.domain.model.FirebaseProjectModel
import com.saudappstudio.snotificationmanager.domain.model.NotificationHistoryModel
import com.saudappstudio.snotificationmanager.domain.model.NotificationPayload
import com.saudappstudio.snotificationmanager.domain.model.TargetType
import com.saudappstudio.snotificationmanager.domain.model.TopicModel
import com.saudappstudio.snotificationmanager.domain.repository.AppRepository
import com.saudappstudio.snotificationmanager.domain.repository.CloudinaryRepository
import com.saudappstudio.snotificationmanager.domain.repository.FirebaseProjectRepository
import com.saudappstudio.snotificationmanager.domain.repository.NotificationRepository
import com.saudappstudio.snotificationmanager.domain.repository.SettingsRepository
import com.saudappstudio.snotificationmanager.domain.repository.TopicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class FiamLayoutType {
    CARD,
    MODAL,
    BANNER,
    IMAGE_ONLY
}

data class InAppMessagingUiState(
    val apps: List<AppModel> = emptyList(),
    val projects: List<FirebaseProjectModel> = emptyList(),
    val topics: List<TopicModel> = emptyList(),
    val selectedApp: AppModel? = null,
    val selectedProject: FirebaseProjectModel? = null,
    val targetType: TargetType = TargetType.TOPIC,
    val selectedTopic: String = "",
    val tokenInput: String = "",
    val layoutType: FiamLayoutType = FiamLayoutType.CARD,
    val eventTrigger: String = "timer_1_min",
    val customEventName: String = "",
    val title: String = "",
    val body: String = "",
    val imageUrl: String = "",
    val primaryButtonText: String = "",
    val primaryButtonUrl: String = "",
    val secondaryButtonText: String = "",
    val secondaryButtonUrl: String = "",
    val customData: Map<String, String> = emptyMap(),
    val isSending: Boolean = false,
    val sendSuccess: Boolean = false,
    val errorMessage: String? = null,
    val userPreferences: UserPreferences = UserPreferences()
)

@HiltViewModel
class InAppMessagingViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val firebaseProjectRepository: FirebaseProjectRepository,
    private val topicRepository: TopicRepository,
    private val notificationRepository: NotificationRepository,
    private val settingsRepository: SettingsRepository,
    private val preferencesManager: PreferencesManager,
    val cloudinaryRepository: CloudinaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InAppMessagingUiState())
    val uiState: StateFlow<InAppMessagingUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            preferencesManager.userPreferencesFlow.collect { prefs ->
                _uiState.update { it.copy(userPreferences = prefs) }
            }
        }

        viewModelScope.launch {
            appRepository.getAllApps().collect { apps ->
                _uiState.update { state ->
                    val selApp = state.selectedApp ?: apps.firstOrNull()
                    state.copy(
                        apps = apps,
                        selectedApp = selApp,
                        selectedTopic = selApp?.defaultTopic ?: state.selectedTopic
                    )
                }
                loadSelectedProject()
            }
        }

        viewModelScope.launch {
            topicRepository.getAllTopics().collect { topics ->
                _uiState.update { it.copy(topics = topics) }
            }
        }

        viewModelScope.launch {
            firebaseProjectRepository.getAllProjects().collect { projects ->
                _uiState.update { it.copy(projects = projects) }
                loadSelectedProject()
            }
        }
    }

    fun initializeWithApp(appId: String) {
        if (appId.isNotBlank()) {
            viewModelScope.launch {
                val app = appRepository.getAppById(appId)
                if (app != null) {
                    _uiState.update {
                        it.copy(
                            selectedApp = app,
                            selectedTopic = if (app.defaultTopic.isNotBlank()) app.defaultTopic else it.selectedTopic
                        )
                    }
                    loadSelectedProject()
                }
            }
        }
    }

    private fun loadSelectedProject() {
        val selApp = _uiState.value.selectedApp ?: return
        val project = _uiState.value.projects.find { it.id == selApp.firebaseProjectId }
        _uiState.update { it.copy(selectedProject = project) }
    }

    fun setSelectedApp(appId: String) {
        viewModelScope.launch {
            val app = appRepository.getAppById(appId)
            if (app != null) {
                _uiState.update {
                    it.copy(
                        selectedApp = app,
                        selectedTopic = if (app.defaultTopic.isNotBlank()) app.defaultTopic else it.selectedTopic
                    )
                }
                loadSelectedProject()
            }
        }
    }

    fun setTargetType(type: TargetType) {
        _uiState.update { it.copy(targetType = type) }
    }

    fun setSelectedTopic(topic: String) {
        _uiState.update { it.copy(selectedTopic = topic) }
    }

    fun setTokenInput(token: String) {
        _uiState.update { it.copy(tokenInput = token) }
    }

    fun setLayoutType(layoutType: FiamLayoutType) {
        _uiState.update { it.copy(layoutType = layoutType) }
    }

    fun setEventTrigger(trigger: String) {
        _uiState.update { it.copy(eventTrigger = trigger) }
    }

    fun setCustomEventName(name: String) {
        _uiState.update { it.copy(customEventName = name) }
    }

    fun setTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun setBody(body: String) {
        _uiState.update { it.copy(body = body) }
    }

    fun setImageUrl(url: String) {
        _uiState.update { it.copy(imageUrl = url) }
    }

    fun setPrimaryButtonText(text: String) {
        _uiState.update { it.copy(primaryButtonText = text) }
    }

    fun setPrimaryButtonUrl(url: String) {
        _uiState.update { it.copy(primaryButtonUrl = url) }
    }

    fun setSecondaryButtonText(text: String) {
        _uiState.update { it.copy(secondaryButtonText = text) }
    }

    fun setSecondaryButtonUrl(url: String) {
        _uiState.update { it.copy(secondaryButtonUrl = url) }
    }

    fun setCustomData(data: Map<String, String>) {
        _uiState.update { it.copy(customData = data) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(sendSuccess = false) }
    }

    fun sendInAppMessage() {
        val state = _uiState.value
        val app = state.selectedApp
        if (app == null) {
            _uiState.update { it.copy(errorMessage = "Please select an application") }
            return
        }

        if (state.layoutType != FiamLayoutType.IMAGE_ONLY && state.title.isBlank() && state.body.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter title or message body") }
            return
        }

        if (state.layoutType == FiamLayoutType.IMAGE_ONLY && state.imageUrl.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Image URL is required for Image Only layout") }
            return
        }

        val resolvedTrigger = if (state.eventTrigger == "custom") {
            state.customEventName.ifBlank { "custom_event" }
        } else {
            state.eventTrigger
        }

        val target = if (state.targetType == TargetType.TOPIC) {
            state.selectedTopic.ifBlank { app.defaultTopic }
        } else {
            state.tokenInput
        }

        val payloadData = mutableMapOf<String, String>().apply {
            put("notification_type", "IN_APP")
            put("fiam_layout", state.layoutType.name)
            put("event_trigger", resolvedTrigger)
            if (state.primaryButtonText.isNotBlank()) put("primary_button_text", state.primaryButtonText)
            if (state.primaryButtonUrl.isNotBlank()) put("primary_button_url", state.primaryButtonUrl)
            if (state.secondaryButtonText.isNotBlank()) put("secondary_button_text", state.secondaryButtonText)
            if (state.secondaryButtonUrl.isNotBlank()) put("secondary_button_url", state.secondaryButtonUrl)
            putAll(state.customData)
        }

        _uiState.update { it.copy(isSending = true, errorMessage = null) }

        viewModelScope.launch {
            val payload = NotificationPayload(
                appId = app.id,
                firebaseProjectKey = app.firebaseProjectId,
                environment = app.environment,
                targetType = state.targetType,
                target = target,
                title = state.title.ifBlank { "In-App Popup (${state.layoutType.name})" },
                message = state.body,
                imageUrl = state.imageUrl,
                clickAction = state.primaryButtonUrl,
                notificationType = "IN_APP",
                eventTrigger = resolvedTrigger,
                customData = payloadData
            )

            val result = notificationRepository.sendNotification(payload)
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = {
                        currentState.copy(isSending = false, sendSuccess = true)
                    },
                    onFailure = { err ->
                        currentState.copy(isSending = false, errorMessage = err.localizedMessage ?: "Failed to dispatch In-App message")
                    }
                )
            }
        }
    }
}
