package com.saudappstudio.snotificationmanager.presentation.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saudappstudio.snotificationmanager.core.datastore.UserPreferences
import com.saudappstudio.snotificationmanager.domain.model.AppModel
import com.saudappstudio.snotificationmanager.domain.model.Environment
import com.saudappstudio.snotificationmanager.domain.model.FirebaseProjectModel
import com.saudappstudio.snotificationmanager.domain.model.NotificationPayload
import com.saudappstudio.snotificationmanager.domain.model.TargetType
import com.saudappstudio.snotificationmanager.domain.model.TemplateModel
import com.saudappstudio.snotificationmanager.domain.model.TopicModel
import com.saudappstudio.snotificationmanager.domain.repository.AppRepository
import com.saudappstudio.snotificationmanager.domain.repository.FirebaseProjectRepository
import com.saudappstudio.snotificationmanager.domain.repository.SettingsRepository
import com.saudappstudio.snotificationmanager.domain.repository.TemplateRepository
import com.saudappstudio.snotificationmanager.domain.repository.TopicRepository
import com.saudappstudio.snotificationmanager.domain.usecase.SendNotificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SendNotificationUiState(
    val apps: List<AppModel> = emptyList(),
    val projects: List<FirebaseProjectModel> = emptyList(),
    val topics: List<TopicModel> = emptyList(),
    val userPreferences: UserPreferences = UserPreferences(),
    val selectedApp: AppModel? = null,
    val selectedProject: FirebaseProjectModel? = null,
    val targetType: TargetType = TargetType.TOPIC,
    val selectedTopic: String = "",
    val tokenInput: String = "",
    val title: String = "",
    val message: String = "",
    val imageUrl: String = "",
    val clickAction: String = "OPEN_APP",
    val deepLink: String = "",
    val channelId: String = "general_notifications",
    val priority: String = "HIGH",
    val ttl: Long = 86400L,
    val collapseKey: String = "",
    val customData: Map<String, String> = emptyMap(),
    val isSending: Boolean = false,
    val sendSuccess: Boolean = false,
    val errorMessage: String? = null,
    val showConfirmDialog: Boolean = false
)

@HiltViewModel
class SendNotificationViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val firebaseProjectRepository: FirebaseProjectRepository,
    private val topicRepository: TopicRepository,
    private val templateRepository: TemplateRepository,
    private val settingsRepository: SettingsRepository,
    private val sendNotificationUseCase: SendNotificationUseCase
) : ViewModel() {

    private val _selectedAppId = MutableStateFlow("")
    private val _targetType = MutableStateFlow(TargetType.TOPIC)
    private val _selectedTopic = MutableStateFlow("")
    private val _tokenInput = MutableStateFlow("")
    private val _title = MutableStateFlow("")
    private val _message = MutableStateFlow("")
    private val _imageUrl = MutableStateFlow("")
    private val _clickAction = MutableStateFlow("OPEN_APP")
    private val _deepLink = MutableStateFlow("")
    private val _channelId = MutableStateFlow("general_notifications")
    private val _priority = MutableStateFlow("HIGH")
    private val _ttl = MutableStateFlow(86400L)
    private val _collapseKey = MutableStateFlow("")
    private val _customData = MutableStateFlow<Map<String, String>>(emptyMap())
    private val _isSending = MutableStateFlow(false)
    private val _sendSuccess = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _showConfirmDialog = MutableStateFlow(false)

    val uiState: StateFlow<SendNotificationUiState> = combine(
        combine(appRepository.getAllApps(), firebaseProjectRepository.getAllProjects(), topicRepository.getAllTopics(), settingsRepository.getUserPreferences()) { a, p, t, u -> Quad(a, p, t, u) },
        combine(_selectedAppId, _targetType, _selectedTopic, _tokenInput) { id, type, topic, token -> Quad(id, type, topic, token) },
        combine(_title, _message, _imageUrl, _clickAction) { title, msg, img, action -> Quad(title, msg, img, action) },
        combine(_deepLink, _channelId, _priority, _customData) { dl, ch, pr, cd -> Quad(dl, ch, pr, cd) },
        combine(_isSending, _sendSuccess, _errorMessage, _showConfirmDialog) { snd, succ, err, conf -> Quad(snd, succ, err, conf) }
    ) { base, audience, content, advanced, status ->
        val (apps, projects, topics, prefs) = base
        val (appId, targetType, topic, token) = audience
        val (title, message, imageUrl, clickAction) = content
        val (deepLink, channelId, priority, customData) = advanced
        val (isSending, sendSuccess, errorMessage, showConfirm) = status

        val app = apps.find { it.id == appId } ?: apps.firstOrNull()
        val project = projects.find { it.id == app?.firebaseProjectId }

        SendNotificationUiState(
            apps = apps,
            projects = projects,
            topics = topics.filter { it.appId == app?.id || it.appId.isBlank() },
            userPreferences = prefs,
            selectedApp = app,
            selectedProject = project,
            targetType = targetType,
            selectedTopic = topic.ifBlank { app?.defaultTopic ?: "" },
            tokenInput = token,
            title = title,
            message = message,
            imageUrl = imageUrl,
            clickAction = clickAction,
            deepLink = deepLink,
            channelId = channelId.ifBlank { app?.defaultChannelId ?: prefs.defaultChannelId },
            priority = priority.ifBlank { prefs.defaultPriority },
            ttl = _ttl.value,
            collapseKey = _collapseKey.value,
            customData = customData,
            isSending = isSending,
            sendSuccess = sendSuccess,
            errorMessage = errorMessage,
            showConfirmDialog = showConfirm
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SendNotificationUiState()
    )

    init {
        viewModelScope.launch {
            val prefs = settingsRepository.getUserPreferences().first()
            if (prefs.lastSelectedAppId.isNotBlank()) {
                _selectedAppId.value = prefs.lastSelectedAppId
            }
        }
    }

    fun initializeFromParams(appId: String, templateId: String) {
        viewModelScope.launch {
            if (appId.isNotBlank()) {
                _selectedAppId.value = appId
            }
            if (templateId.isNotBlank()) {
                val template = templateRepository.getTemplateById(templateId)
                if (template != null) {
                    applyTemplate(template)
                }
            }
        }
    }

    fun applyTemplate(template: TemplateModel) {
        _selectedAppId.value = template.appId
        _title.value = template.title
        _message.value = template.message
        _imageUrl.value = template.imageUrl
        if (template.topic.isNotBlank()) {
            _selectedTopic.value = template.topic
            _targetType.value = TargetType.TOPIC
        }
        _clickAction.value = template.clickAction
        _deepLink.value = template.deepLink
        _customData.value = template.customData
    }

    fun setSelectedApp(appId: String) {
        _selectedAppId.value = appId
        viewModelScope.launch {
            settingsRepository.setLastSelectedAppId(appId)
            val app = appRepository.getAppById(appId)
            if (app != null && app.defaultTopic.isNotBlank()) {
                _selectedTopic.value = app.defaultTopic
            }
        }
    }

    fun setTargetType(type: TargetType) {
        _targetType.value = type
    }

    fun setSelectedTopic(topic: String) {
        _selectedTopic.value = topic
    }

    fun setTokenInput(token: String) {
        _tokenInput.value = token
    }

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setMessage(message: String) {
        _message.value = message
    }

    fun setImageUrl(url: String) {
        _imageUrl.value = url
    }

    fun setClickAction(action: String) {
        _clickAction.value = action
    }

    fun setDeepLink(link: String) {
        _deepLink.value = link
    }

    fun setChannelId(channelId: String) {
        _channelId.value = channelId
    }

    fun setPriority(priority: String) {
        _priority.value = priority
    }

    fun setCustomData(data: Map<String, String>) {
        _customData.value = data
    }

    fun onSendClicked() {
        val current = uiState.value
        val app = current.selectedApp

        if (current.title.isBlank() || current.message.isBlank()) {
            _errorMessage.value = "Title and message cannot be blank."
            return
        }

        // If Production environment and confirmation is required, pop dialog
        if (app?.environment == Environment.PRODUCTION && (app.requireConfirmForProd || current.userPreferences.confirmBeforeProdSend)) {
            _showConfirmDialog.value = true
        } else {
            dispatchNotification(isTest = false)
        }
    }

    fun confirmProductionSend() {
        _showConfirmDialog.value = false
        dispatchNotification(isTest = false)
    }

    fun dismissConfirmDialog() {
        _showConfirmDialog.value = false
    }

    fun sendTest() {
        dispatchNotification(isTest = true)
    }

    private fun dispatchNotification(isTest: Boolean) {
        viewModelScope.launch {
            val current = uiState.value
            val app = current.selectedApp ?: return@launch
            val project = current.selectedProject

            _isSending.value = true
            _errorMessage.value = null
            _sendSuccess.value = false

            val target = if (current.targetType == TargetType.TOPIC) current.selectedTopic else current.tokenInput

            val payload = NotificationPayload(
                appId = app.id,
                firebaseProjectKey = project?.backendKey ?: "default",
                environment = if (isTest) Environment.TESTING else app.environment,
                targetType = current.targetType,
                target = target,
                title = current.title,
                message = current.message,
                imageUrl = current.imageUrl,
                clickAction = current.clickAction,
                deepLink = current.deepLink,
                channelId = current.channelId,
                priority = current.priority,
                ttl = current.ttl,
                collapseKey = current.collapseKey,
                badge = 1,
                customData = current.customData
            )

            val result = sendNotificationUseCase(
                appName = app.name,
                payload = payload,
                isTest = isTest
            )

            _isSending.value = false
            if (result.isSuccess) {
                _sendSuccess.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Failed to dispatch notification"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun resetSuccess() {
        _sendSuccess.value = false
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
