package com.saudappstudio.snotificationmanager.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saudappstudio.snotificationmanager.core.logging.Logger
import com.saudappstudio.snotificationmanager.domain.model.AnalyticsEventModel
import com.saudappstudio.snotificationmanager.domain.model.AnalyticsFilter
import com.saudappstudio.snotificationmanager.domain.model.AnalyticsMetricsSummary
import com.saudappstudio.snotificationmanager.domain.model.AppModel
import com.saudappstudio.snotificationmanager.domain.model.DemographicItem
import com.saudappstudio.snotificationmanager.domain.repository.AnalyticsRepository
import com.saudappstudio.snotificationmanager.domain.repository.AppRepository
import com.saudappstudio.snotificationmanager.domain.repository.FirebaseProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state container for the Firebase Analytics Dashboard.
 */
data class AnalyticsUiState(
    val selectedAppId: String = "",
    val selectedTimeRange: String = "7D",
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val metricsSummary: AnalyticsMetricsSummary = AnalyticsMetricsSummary(),
    val appsList: List<AppModel> = emptyList(),
    val osDemographics: List<DemographicItem> = emptyList(),
    val deviceDemographics: List<DemographicItem> = emptyList()
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val analyticsRepository: AnalyticsRepository,
    private val appRepository: AppRepository,
    private val firebaseProjectRepository: FirebaseProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val analyticsEvents: StateFlow<List<AnalyticsEventModel>> = _uiState.flatMapLatest { state ->
        val filter = AnalyticsFilter(
            appId = state.selectedAppId,
            timeRange = state.selectedTimeRange,
            searchQuery = state.searchQuery
        )
        analyticsRepository.getAnalyticsEvents(filter)
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

    /**
     * Updates selected app filter and fetches real metrics from GA4 Data API.
     */
    fun onAppSelected(appId: String) {
        _uiState.value = _uiState.value.copy(selectedAppId = appId, errorMessage = null)
        updateMetrics()
    }

    /**
     * Updates time-range filter ("TODAY", "7D", "30D", "90D") and re-fetches GA4 metrics.
     */
    fun onTimeRangeSelected(timeRange: String) {
        _uiState.value = _uiState.value.copy(selectedTimeRange = timeRange, errorMessage = null)
        updateMetrics()
    }

    /**
     * Updates text search query for filtering tracked events.
     */
    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    /**
     * Fetches live metrics and demographics from Netlify / GA4 Data API for the selected app.
     */
    fun updateMetrics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val currentAppId = _uiState.value.selectedAppId
            val timeRange = _uiState.value.selectedTimeRange

            // Resolve selected app & backend Key
            val app = _uiState.value.appsList.find { it.id == currentAppId }
            var backendKey = "dictionary"
            var gaPropertyId: String? = null

            if (app != null) {
                val projects = runCatching { firebaseProjectRepository.getAllProjects().first() }.getOrDefault(emptyList())
                val matchedProject = projects.find { it.id == app.firebaseProjectId || it.backendKey == app.firebaseProjectId }
                if (matchedProject != null) {
                    backendKey = matchedProject.backendKey
                    gaPropertyId = matchedProject.gaPropertyId.ifBlank { null }
                }
            }

            // Fetch live GA4 report metrics via Netlify function
            val result = analyticsRepository.fetchRealAnalytics(
                appId = currentAppId,
                backendKey = backendKey,
                gaPropertyId = gaPropertyId,
                timeRange = timeRange
            )

            if (result.isSuccess) {
                val summary = result.getOrNull() ?: analyticsRepository.getMetricsSummary(currentAppId, timeRange)
                val (osList, deviceList) = analyticsRepository.getDemographics(currentAppId)

                _uiState.value = _uiState.value.copy(
                    metricsSummary = summary,
                    osDemographics = osList,
                    deviceDemographics = deviceList,
                    isLoading = false,
                    errorMessage = null
                )
            } else {
                val error = result.exceptionOrNull()?.message ?: "Failed to connect to Firebase GA4 Analytics API."
                Logger.e("Analytics fetch error: $error", tag = "AnalyticsViewModel")
                val summary = analyticsRepository.getMetricsSummary(currentAppId, timeRange)
                val (osList, deviceList) = analyticsRepository.getDemographics(currentAppId)

                _uiState.value = _uiState.value.copy(
                    metricsSummary = summary,
                    osDemographics = osList,
                    deviceDemographics = deviceList,
                    isLoading = false,
                    errorMessage = error
                )
            }
        }
    }

    /**
     * Formats current analytics dashboard report into JSON format for export.
     */
    fun exportAnalyticsReport(): String {
        val state = _uiState.value
        val summary = state.metricsSummary
        val events = analyticsEvents.value

        return """
        {
          "appId": "${state.selectedAppId.ifEmpty { "ALL" }}",
          "timeRange": "${state.selectedTimeRange}",
          "dau": ${summary.dau},
          "wau": ${summary.wau},
          "mau": ${summary.mau},
          "totalEvents": ${summary.totalEvents},
          "avgSessionDuration": ${summary.avgSessionDurationSeconds},
          "notificationOpenRate": ${summary.notificationOpenRate},
          "realtimeActiveUsers": ${summary.realtimeActiveUsers},
          "eventCount": ${events.size}
        }
        """.trimIndent()
    }
}
