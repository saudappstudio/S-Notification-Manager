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
import com.saudappstudio.snotificationmanager.domain.repository.CloudinaryRepository
import com.saudappstudio.snotificationmanager.domain.repository.FirebaseProjectRepository
import com.saudappstudio.snotificationmanager.domain.repository.SettingsRepository
import com.saudappstudio.snotificationmanager.domain.repository.TemplateRepository
import com.saudappstudio.snotificationmanager.domain.repository.TopicRepository
import com.saudappstudio.snotificationmanager.domain.usecase.SendNotificationUseCase
import com.saudappstudio.snotificationmanager.provider.ScheduleOptionsProvider
import com.saudappstudio.snotificationmanager.provider.ScheduleTimeUnit
import com.saudappstudio.snotificationmanager.provider.ScheduleType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * UI State representation for the Send Notification screen.
 */
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
    val notificationType: String = "PUSH",
    val eventTrigger: String = "timer_1_min",
    val customEventName: String = "",
    val isScheduled: Boolean = false,
    val scheduleType: ScheduleType = ScheduleType.PRESETS,
    val scheduleDelayMinutes: Int = 15,
    val customDelayValueInput: String = "30",
    val customDelayUnit: ScheduleTimeUnit = ScheduleTimeUnit.MINUTES,
    val selectedScheduledTimestamp: Long = System.currentTimeMillis() + 15 * 60 * 1000L,
    val calculatedScheduledTimestamp: Long = System.currentTimeMillis() + 15 * 60 * 1000L,
    val formattedScheduledTime: String = "",
    val customData: Map<String, String> = emptyMap(),
    val showPrimaryButton: Boolean = false,
    val primaryButtonText: String = "",
    val primaryButtonUrl: String = "",
    val isSending: Boolean = false,
    val sendSuccess: Boolean = false,
    val isScheduleSuccess: Boolean = false,
    val errorMessage: String? = null,
    val showConfirmDialog: Boolean = false,
    val pendingIsTest: Boolean = false
)

private data class BaseState(
    val apps: List<AppModel>,
    val projects: List<FirebaseProjectModel>,
    val topics: List<TopicModel>,
    val userPreferences: UserPreferences
)

private data class AudienceState(
    val appId: String,
    val targetType: TargetType,
    val topic: String,
    val token: String
)

private data class ContentState(
    val title: String,
    val message: String,
    val imageUrl: String,
    val clickAction: String
)

private data class ScheduleConfigState(
    val notificationType: String,
    val eventTrigger: String,
    val customEventName: String,
    val isScheduled: Boolean,
    val scheduleType: ScheduleType,
    val scheduleDelayMinutes: Int,
    val customDelayValueInput: String,
    val customDelayUnit: ScheduleTimeUnit,
    val selectedScheduledTimestamp: Long
)

private data class StatusAndDataState(
    val customData: Map<String, String>,
    val showPrimaryButton: Boolean,
    val primaryButtonText: String,
    val primaryButtonUrl: String,
    val isSending: Boolean,
    val sendSuccess: Boolean,
    val isScheduleSuccess: Boolean,
    val errorMessage: String?,
    val showConfirmDialog: Boolean,
    val pendingIsTest: Boolean
)

private data class Tuple2<A, B>(val v1: A, val v2: B)
private data class Tuple4<A, B, C, D>(val v1: A, val v2: B, val v3: C, val v4: D)
private data class Tuple5<A, B, C, D, E>(val v1: A, val v2: B, val v3: C, val v4: D, val v5: E)

/**
 * ViewModel managing notification creation, scheduling configuration, and dispatching.
 */
@HiltViewModel
class SendNotificationViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val firebaseProjectRepository: FirebaseProjectRepository,
    private val topicRepository: TopicRepository,
    private val templateRepository: TemplateRepository,
    private val settingsRepository: SettingsRepository,
    private val sendNotificationUseCase: SendNotificationUseCase,
    val cloudinaryRepository: CloudinaryRepository
) : ViewModel() {

    private val _selectedAppId = MutableStateFlow("")
    private val _targetType = MutableStateFlow(TargetType.TOPIC)
    private val _selectedTopic = MutableStateFlow("global")
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
    private val _notificationType = MutableStateFlow("PUSH")
    private val _eventTrigger = MutableStateFlow("timer_1_min")
    private val _customEventName = MutableStateFlow("")
    private val _isScheduled = MutableStateFlow(false)
    private val _scheduleType = MutableStateFlow(ScheduleType.PRESETS)
    private val _scheduleDelayMinutes = MutableStateFlow(15)
    private val _customDelayValueInput = MutableStateFlow("30")
    private val _customDelayUnit = MutableStateFlow(ScheduleTimeUnit.MINUTES)
    private val _selectedScheduledTimestamp = MutableStateFlow(System.currentTimeMillis() + 15 * 60 * 1000L)
    private val _customData = MutableStateFlow<Map<String, String>>(emptyMap())
    private val _showPrimaryButton = MutableStateFlow(false)
    private val _primaryButtonText = MutableStateFlow("")
    private val _primaryButtonUrl = MutableStateFlow("")
    private val _isSending = MutableStateFlow(false)
    private val _sendSuccess = MutableStateFlow(false)
    private val _isScheduleSuccess = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _showConfirmDialog = MutableStateFlow(false)
    private val _pendingIsTest = MutableStateFlow(false)

    private val _baseFlow = combine(
        appRepository.getAllApps(),
        firebaseProjectRepository.getAllProjects(),
        topicRepository.getAllTopics(),
        settingsRepository.getUserPreferences()
    ) { apps, projects, topics, prefs ->
        BaseState(apps, projects, topics, prefs)
    }

    private val _audienceFlow = combine(
        _selectedAppId,
        _targetType,
        _selectedTopic,
        _tokenInput
    ) { appId, targetType, topic, token ->
        AudienceState(appId, targetType, topic, token)
    }

    private val _contentFlow = combine(
        _title,
        _message,
        _imageUrl,
        _clickAction
    ) { title, message, imageUrl, clickAction ->
        ContentState(title, message, imageUrl, clickAction)
    }

    private val _scheduleConfigFlow = combine(
        combine(_notificationType, _eventTrigger, _customEventName, _isScheduled) { nType, ev, custEv, sched ->
            Tuple4(nType, ev, custEv, sched)
        },
        combine(_scheduleType, _scheduleDelayMinutes, _customDelayValueInput, _customDelayUnit, _selectedScheduledTimestamp) { sType, delMins, custVal, custUnit, selTs ->
            Tuple5(sType, delMins, custVal, custUnit, selTs)
        }
    ) { tuple4, tuple5 ->
        ScheduleConfigState(
            notificationType = tuple4.v1,
            eventTrigger = tuple4.v2,
            customEventName = tuple4.v3,
            isScheduled = tuple4.v4,
            scheduleType = tuple5.v1,
            scheduleDelayMinutes = tuple5.v2,
            customDelayValueInput = tuple5.v3,
            customDelayUnit = tuple5.v4,
            selectedScheduledTimestamp = tuple5.v5
        )
    }

    private val _statusAndDataFlow = combine(
        combine(_customData, _showPrimaryButton, _primaryButtonText, _primaryButtonUrl) { data, showBtn, btnTxt, btnUrl ->
            Tuple4(data, showBtn, btnTxt, btnUrl)
        },
        combine(_isSending, _sendSuccess, _isScheduleSuccess, _errorMessage) { snd, succ, schedSucc, err ->
            Tuple4(snd, succ, schedSucc, err)
        },
        combine(_showConfirmDialog, _pendingIsTest) { show, isTest ->
            Tuple2(show, isTest)
        }
    ) { tupleData, tupleStatus, tupleDialog ->
        StatusAndDataState(
            customData = tupleData.v1,
            showPrimaryButton = tupleData.v2,
            primaryButtonText = tupleData.v3,
            primaryButtonUrl = tupleData.v4,
            isSending = tupleStatus.v1,
            sendSuccess = tupleStatus.v2,
            isScheduleSuccess = tupleStatus.v3,
            errorMessage = tupleStatus.v4,
            showConfirmDialog = tupleDialog.v1,
            pendingIsTest = tupleDialog.v2
        )
    }

    val uiState: StateFlow<SendNotificationUiState> = combine(
        _baseFlow,
        _audienceFlow,
        _contentFlow,
        _scheduleConfigFlow,
        _statusAndDataFlow
    ) { base, audience, content, sched, status ->
        val app = base.apps.find { it.id == audience.appId } ?: base.apps.firstOrNull()
        val project = base.projects.find { it.id == app?.firebaseProjectId }
        val effectiveTopic = audience.topic.ifBlank { if (!app?.defaultTopic.isNullOrBlank()) app.defaultTopic else "global" }

        val (calculatedTs, _) = computeScheduledTime(
            sched.scheduleType,
            sched.scheduleDelayMinutes,
            sched.customDelayValueInput,
            sched.customDelayUnit,
            sched.selectedScheduledTimestamp
        )
        val formattedTime = formatTimestamp(calculatedTs)

        SendNotificationUiState(
            apps = base.apps,
            projects = base.projects,
            topics = base.topics.filter { it.appId == app?.id || it.appId.isBlank() },
            userPreferences = base.userPreferences,
            selectedApp = app,
            selectedProject = project,
            targetType = audience.targetType,
            selectedTopic = effectiveTopic,
            tokenInput = audience.token,
            title = content.title,
            message = content.message,
            imageUrl = content.imageUrl,
            clickAction = content.clickAction,
            deepLink = _deepLink.value,
            channelId = _channelId.value.ifBlank { app?.defaultChannelId ?: base.userPreferences.defaultChannelId },
            priority = _priority.value.ifBlank { base.userPreferences.defaultPriority },
            ttl = _ttl.value,
            collapseKey = _collapseKey.value,
            notificationType = sched.notificationType,
            eventTrigger = sched.eventTrigger,
            customEventName = sched.customEventName,
            isScheduled = sched.isScheduled,
            scheduleType = sched.scheduleType,
            scheduleDelayMinutes = sched.scheduleDelayMinutes,
            customDelayValueInput = sched.customDelayValueInput,
            customDelayUnit = sched.customDelayUnit,
            selectedScheduledTimestamp = sched.selectedScheduledTimestamp,
            calculatedScheduledTimestamp = calculatedTs,
            formattedScheduledTime = formattedTime,
            customData = status.customData,
            showPrimaryButton = status.showPrimaryButton,
            primaryButtonText = status.primaryButtonText,
            primaryButtonUrl = status.primaryButtonUrl,
            isSending = status.isSending,
            sendSuccess = status.sendSuccess,
            isScheduleSuccess = status.isScheduleSuccess,
            errorMessage = status.errorMessage,
            showConfirmDialog = status.showConfirmDialog,
            pendingIsTest = status.pendingIsTest
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SendNotificationUiState()
    )

    init {
        viewModelScope.launch {
            val prefs = settingsRepository.getUserPreferences().first()
            if (prefs.lastSelectedAppId.isNotBlank() && _selectedAppId.value.isBlank()) {
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
            if (app != null) {
                _selectedTopic.value = if (app.defaultTopic.isNotBlank()) app.defaultTopic else "global"
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

    fun setNotificationType(type: String) {
        _notificationType.value = type
    }

    fun setEventTrigger(trigger: String) {
        _eventTrigger.value = trigger
    }

    fun setCustomEventName(name: String) {
        _customEventName.value = name
    }

    fun setIsScheduled(scheduled: Boolean) {
        _isScheduled.value = scheduled
    }

    fun setScheduleType(type: ScheduleType) {
        _scheduleType.value = type
    }

    fun setScheduleDelayMinutes(minutes: Int) {
        _scheduleDelayMinutes.value = minutes
    }

    fun setCustomDelayValueInput(value: String) {
        _customDelayValueInput.value = value.filter { it.isDigit() }
    }

    fun setCustomDelayUnit(unit: ScheduleTimeUnit) {
        _customDelayUnit.value = unit
    }

    fun setSelectedScheduledTimestamp(timestamp: Long) {
        _selectedScheduledTimestamp.value = timestamp
    }

    /**
     * Sets the scheduled date component of the exact scheduled timestamp.
     */
    fun setScheduledDate(year: Int, month: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance()
        val currentTs = _selectedScheduledTimestamp.value
        if (currentTs > 0) {
            cal.timeInMillis = currentTs
        } else {
            cal.timeInMillis = System.currentTimeMillis() + 15 * 60 * 1000L
        }
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        _selectedScheduledTimestamp.value = cal.timeInMillis
    }

    /**
     * Sets the scheduled time-of-day component of the exact scheduled timestamp.
     */
    fun setScheduledTime(hourOfDay: Int, minute: Int) {
        val cal = Calendar.getInstance()
        val currentTs = _selectedScheduledTimestamp.value
        if (currentTs > 0) {
            cal.timeInMillis = currentTs
        } else {
            cal.timeInMillis = System.currentTimeMillis() + 15 * 60 * 1000L
        }
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        _selectedScheduledTimestamp.value = cal.timeInMillis
    }

    fun setCustomData(data: Map<String, String>) {
        _customData.value = data
    }

    fun setShowPrimaryButton(show: Boolean) {
        _showPrimaryButton.value = show
    }

    fun setPrimaryButtonText(text: String) {
        _primaryButtonText.value = text
    }

    fun setPrimaryButtonUrl(url: String) {
        _primaryButtonUrl.value = url
    }

    fun onSendClicked() {
        val current = uiState.value
        if (current.title.isBlank() || current.message.isBlank()) {
            _errorMessage.value = "Title and message cannot be blank."
            return
        }

        _pendingIsTest.value = false
        _showConfirmDialog.value = true
    }

    fun sendTest() {
        val current = uiState.value
        if (current.title.isBlank() || current.message.isBlank()) {
            _errorMessage.value = "Title and message cannot be blank."
            return
        }

        _pendingIsTest.value = true
        _showConfirmDialog.value = true
    }

    fun confirmProductionSend() {
        _showConfirmDialog.value = false
        dispatchNotification(isTest = _pendingIsTest.value)
    }

    fun dismissConfirmDialog() {
        _showConfirmDialog.value = false
    }

    private fun dispatchNotification(isTest: Boolean) {
        viewModelScope.launch {
            val current = uiState.value
            val app = current.selectedApp
            if (app == null) {
                _errorMessage.value = "No application selected. Please select an application."
                return@launch
            }
            val project = current.selectedProject

            val rawTarget = if (current.targetType == TargetType.TOPIC) {
                current.selectedTopic.ifBlank { if (app.defaultTopic.isNotBlank()) app.defaultTopic else "global" }
            } else {
                current.tokenInput
            }

            val target = if (current.targetType == TargetType.TOPIC) {
                rawTarget.trim().removePrefix("/topics/").removePrefix("topics/")
            } else {
                rawTarget.trim()
            }

            if (target.isBlank()) {
                _errorMessage.value = if (current.targetType == TargetType.TOPIC) {
                    "Topic target cannot be blank."
                } else {
                    "Device FCM Token cannot be blank."
                }
                return@launch
            }

            val (scheduledTimeMs, delayMinutes) = if (current.isScheduled) {
                when (current.scheduleType) {
                    ScheduleType.PRESETS -> {
                        val ts = System.currentTimeMillis() + current.scheduleDelayMinutes * 60 * 1000L
                        Pair(ts, current.scheduleDelayMinutes)
                    }
                    ScheduleType.CUSTOM_DELAY -> {
                        val delayVal = current.customDelayValueInput.toIntOrNull()
                        if (delayVal == null || delayVal <= 0) {
                            _errorMessage.value = "Please enter a valid positive number for schedule delay."
                            return@launch
                        }
                        val totalMins = delayVal * current.customDelayUnit.minutesMultiplier
                        val ts = System.currentTimeMillis() + totalMins * 60 * 1000L
                        Pair(ts, totalMins)
                    }
                    ScheduleType.EXACT_TIME -> {
                        val ts = current.selectedScheduledTimestamp
                        if (ts <= System.currentTimeMillis()) {
                            _errorMessage.value = "Scheduled time must be in the future."
                            return@launch
                        }
                        val mins = ((ts - System.currentTimeMillis()) / (60 * 1000L)).coerceAtLeast(1).toInt()
                        Pair(ts, mins)
                    }
                }
            } else {
                Pair(null, 0)
            }

            _isSending.value = true
            _errorMessage.value = null
            _sendSuccess.value = false
            _isScheduleSuccess.value = false

            val effectiveEvent = if (current.eventTrigger == "custom") {
                current.customEventName.ifBlank { "custom_event" }
            } else {
                current.eventTrigger
            }

            val finalCustomData = current.customData.toMutableMap()
            if (current.notificationType == "IN_APP") {
                finalCustomData["in_app_event"] = effectiveEvent
                finalCustomData["notification_type"] = "IN_APP"
                if (current.showPrimaryButton) {
                    finalCustomData["in_app_primary_button_show"] = "true"
                    finalCustomData["in_app_primary_button_text"] = current.primaryButtonText.ifBlank { "Learn More" }
                    finalCustomData["in_app_primary_button_url"] = current.primaryButtonUrl
                }
            }

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
                notificationType = current.notificationType,
                eventTrigger = effectiveEvent,
                isScheduled = current.isScheduled,
                scheduledTimestamp = scheduledTimeMs,
                scheduleDelayMinutes = delayMinutes,
                customData = finalCustomData
            )

            val result = sendNotificationUseCase(
                appName = app.name,
                payload = payload,
                isTest = isTest
            )

            _isSending.value = false
            if (result.isSuccess) {
                if (current.isScheduled) {
                    _isScheduleSuccess.value = true
                } else {
                    _sendSuccess.value = true
                }
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
        _isScheduleSuccess.value = false
    }

    private fun computeScheduledTime(
        type: ScheduleType,
        presetMinutes: Int,
        customValueInput: String,
        customUnit: ScheduleTimeUnit,
        selectedTimestamp: Long
    ): Pair<Long, Int> {
        return when (type) {
            ScheduleType.PRESETS -> {
                val ts = System.currentTimeMillis() + presetMinutes * 60 * 1000L
                Pair(ts, presetMinutes)
            }
            ScheduleType.CUSTOM_DELAY -> {
                val valInt = customValueInput.toIntOrNull() ?: 0
                val mins = valInt * customUnit.minutesMultiplier
                val ts = System.currentTimeMillis() + mins * 60 * 1000L
                Pair(ts, mins)
            }
            ScheduleType.EXACT_TIME -> {
                val ts = if (selectedTimestamp > 0) selectedTimestamp else System.currentTimeMillis() + 15 * 60 * 1000L
                val mins = ((ts - System.currentTimeMillis()) / (60 * 1000L)).coerceAtLeast(1).toInt()
                Pair(ts, mins)
            }
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        if (timestamp <= 0) return ""
        val sdf = SimpleDateFormat("EEE, MMM d, yyyy 'at' hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
