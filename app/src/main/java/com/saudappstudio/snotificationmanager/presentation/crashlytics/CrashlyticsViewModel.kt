package com.saudappstudio.snotificationmanager.presentation.crashlytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saudappstudio.snotificationmanager.core.logging.Logger
import com.saudappstudio.snotificationmanager.domain.model.AppModel
import com.saudappstudio.snotificationmanager.domain.model.CrashDetailModel
import com.saudappstudio.snotificationmanager.domain.model.CrashIssueModel
import com.saudappstudio.snotificationmanager.domain.model.CrashIssueStatus
import com.saudappstudio.snotificationmanager.domain.model.CrashMetricsSummary
import com.saudappstudio.snotificationmanager.domain.model.CrashlyticsFilter
import com.saudappstudio.snotificationmanager.domain.repository.AppRepository
import com.saudappstudio.snotificationmanager.domain.repository.CrashlyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State container for the Crashlytics Dashboard.
 */
data class CrashlyticsUiState(
    val selectedAppId: String = "",
    val selectedSeverity: String = "ALL", // ALL, FATAL, NON_FATAL
    val selectedStatus: String = "ALL", // ALL, OPEN, RESOLVED, MUTED
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val metricsSummary: CrashMetricsSummary = CrashMetricsSummary(),
    val appsList: List<AppModel> = emptyList()
)

@HiltViewModel
class CrashlyticsViewModel @Inject constructor(
    private val crashlyticsRepository: CrashlyticsRepository,
    private val appRepository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CrashlyticsUiState())
    val uiState: StateFlow<CrashlyticsUiState> = _uiState.asStateFlow()

    private val _selectedDetail = MutableStateFlow<CrashDetailModel?>(null)
    val selectedDetail: StateFlow<CrashDetailModel?> = _selectedDetail.asStateFlow()

    private val _selectedIssue = MutableStateFlow<CrashIssueModel?>(null)
    val selectedIssue: StateFlow<CrashIssueModel?> = _selectedIssue.asStateFlow()

    init {
        loadApps()
        refreshLiveData()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val crashIssues: StateFlow<List<CrashIssueModel>> = _uiState.flatMapLatest { state ->
        val isFatal = when (state.selectedSeverity) {
            "FATAL" -> true
            "NON_FATAL" -> false
            else -> null
        }
        val status = runCatching { CrashIssueStatus.valueOf(state.selectedStatus) }.getOrNull()

        val filter = CrashlyticsFilter(
            appId = state.selectedAppId,
            isFatal = isFatal,
            status = status,
            searchQuery = state.searchQuery
        )
        crashlyticsRepository.getCrashIssues(filter)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private fun loadApps() {
        viewModelScope.launch {
            appRepository.getAllApps().collect { apps ->
                _uiState.value = _uiState.value.copy(appsList = apps)
                updateMetrics()
            }
        }
    }

    fun onAppSelected(appId: String) {
        _uiState.value = _uiState.value.copy(selectedAppId = appId)
        updateMetrics()
    }

    fun onSeveritySelected(severity: String) {
        _uiState.value = _uiState.value.copy(selectedSeverity = severity)
    }

    fun onStatusSelected(status: String) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun loadIssueDetail(issue: CrashIssueModel) {
        _selectedIssue.value = issue
        viewModelScope.launch {
            _selectedDetail.value = crashlyticsRepository.getCrashDetail(issue.id)
        }
    }

    fun loadIssueDetailById(issueId: String) {
        viewModelScope.launch {
            val detail = crashlyticsRepository.getCrashDetail(issueId)
            _selectedDetail.value = detail
            val currentList = crashIssues.value
            val foundIssue = currentList.find { it.id == issueId }
            if (foundIssue != null) {
                _selectedIssue.value = foundIssue
            }
        }
    }

    fun updateIssueStatus(issueId: String, newStatus: CrashIssueStatus) {
        viewModelScope.launch {
            Logger.d("Updating issue status $issueId to ${newStatus.name}", "CrashlyticsViewModel")
            crashlyticsRepository.updateIssueStatus(issueId, newStatus)
            _selectedIssue.value = _selectedIssue.value?.copy(status = newStatus)
            updateMetrics()
        }
    }

    fun refreshLiveData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            updateMetrics()
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private fun updateMetrics() {
        viewModelScope.launch {
            val metrics = crashlyticsRepository.getMetricsSummary(_uiState.value.selectedAppId)
            _uiState.value = _uiState.value.copy(metricsSummary = metrics)
        }
    }
}
