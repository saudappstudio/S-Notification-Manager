package com.saudappstudio.snotificationmanager.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saudappstudio.snotificationmanager.domain.model.Environment
import com.saudappstudio.snotificationmanager.domain.model.NotificationHistoryModel
import com.saudappstudio.snotificationmanager.domain.model.TemplateModel
import com.saudappstudio.snotificationmanager.domain.repository.NotificationRepository
import com.saudappstudio.snotificationmanager.domain.repository.TemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class HistoryFilter {
    ALL,
    SENT,
    FAILED,
    TESTING,
    PRODUCTION
}

data class HistoryUiState(
    val historyItems: List<NotificationHistoryModel> = emptyList(),
    val filteredItems: List<NotificationHistoryModel> = emptyList(),
    val searchQuery: String = "",
    val activeFilter: HistoryFilter = HistoryFilter.ALL,
    val selectedItem: NotificationHistoryModel? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val templateRepository: TemplateRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _activeFilter = MutableStateFlow(HistoryFilter.ALL)
    private val _selectedItem = MutableStateFlow<NotificationHistoryModel?>(null)

    val uiState: StateFlow<HistoryUiState> = combine(
        notificationRepository.getAllHistory(),
        _searchQuery,
        _activeFilter,
        _selectedItem
    ) { history, query, filter, selected ->
        val filtered = history.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.appName.contains(query, ignoreCase = true) ||
                    item.target.contains(query, ignoreCase = true) ||
                    item.message.contains(query, ignoreCase = true)
            val matchesFilter = when (filter) {
                HistoryFilter.ALL -> true
                HistoryFilter.SENT -> item.status.equals("SENT", ignoreCase = true)
                HistoryFilter.FAILED -> item.status.equals("FAILED", ignoreCase = true)
                HistoryFilter.TESTING -> item.environment == Environment.TESTING
                HistoryFilter.PRODUCTION -> item.environment == Environment.PRODUCTION
            }
            matchesQuery && matchesFilter
        }
        HistoryUiState(
            historyItems = history,
            filteredItems = filtered,
            searchQuery = query,
            activeFilter = filter,
            selectedItem = selected
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: HistoryFilter) {
        _activeFilter.value = filter
    }

    fun loadDetails(historyId: String) {
        viewModelScope.launch {
            _selectedItem.value = notificationRepository.getHistoryById(historyId)
        }
    }

    fun clearAllHistory(onCleared: () -> Unit) {
        viewModelScope.launch {
            notificationRepository.clearHistory()
            onCleared()
        }
    }

    fun saveHistoryAsTemplate(item: NotificationHistoryModel, onSaved: () -> Unit) {
        viewModelScope.launch {
            val template = TemplateModel(
                id = UUID.randomUUID().toString(),
                name = item.title,
                appId = item.appId,
                title = item.title,
                message = item.message,
                imageUrl = item.imageUrl,
                topic = item.target,
                clickAction = item.clickAction,
                deepLink = item.deepLink,
                customData = item.customData
            )
            templateRepository.insertTemplate(template)
            onSaved()
        }
    }
}
