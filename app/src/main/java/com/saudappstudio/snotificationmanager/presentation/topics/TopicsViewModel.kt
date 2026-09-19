package com.saudappstudio.snotificationmanager.presentation.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saudappstudio.snotificationmanager.domain.model.AppModel
import com.saudappstudio.snotificationmanager.domain.model.TopicModel
import com.saudappstudio.snotificationmanager.domain.repository.AppRepository
import com.saudappstudio.snotificationmanager.domain.repository.TopicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TopicsUiState(
    val topics: List<TopicModel> = emptyList(),
    val filteredTopics: List<TopicModel> = emptyList(),
    val apps: List<AppModel> = emptyList(),
    val searchQuery: String = "",
    val selectedTopicIds: Set<String> = emptySet()
)

@HiltViewModel
class TopicsViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val appRepository: AppRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedTopicIds = MutableStateFlow<Set<String>>(emptySet())

    val uiState: StateFlow<TopicsUiState> = combine(
        topicRepository.getAllTopics(),
        appRepository.getAllApps(),
        _searchQuery,
        _selectedTopicIds
    ) { topics, apps, query, selected ->
        val filtered = if (query.isBlank()) {
            topics
        } else {
            topics.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }
        TopicsUiState(
            topics = topics,
            filteredTopics = filtered,
            apps = apps,
            searchQuery = query,
            selectedTopicIds = selected
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TopicsUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleSelectTopic(id: String) {
        val current = _selectedTopicIds.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _selectedTopicIds.value = current
    }

    fun selectAllTopics() {
        val allIds = uiState.value.filteredTopics.map { it.id }.toSet()
        _selectedTopicIds.value = allIds
    }

    fun clearSelection() {
        _selectedTopicIds.value = emptySet()
    }

    fun deleteSelectedTopics(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val toDelete = _selectedTopicIds.value
            toDelete.forEach { id ->
                topicRepository.deleteTopic(id)
            }
            _selectedTopicIds.value = emptySet()
            onDeleted()
        }
    }

    fun deleteAllTopics(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val allTopics = uiState.value.topics
            allTopics.forEach { topic ->
                topicRepository.deleteTopic(topic.id)
            }
            _selectedTopicIds.value = emptySet()
            onDeleted()
        }
    }

    fun saveTopic(topic: TopicModel, onSaved: () -> Unit) {
        viewModelScope.launch {
            val existing = topicRepository.getTopicById(topic.id)
            if (existing != null) {
                topicRepository.updateTopic(topic)
            } else {
                topicRepository.insertTopic(topic)
            }
            onSaved()
        }
    }

    fun deleteTopic(topicId: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            topicRepository.deleteTopic(topicId)
            val current = _selectedTopicIds.value.toMutableSet()
            current.remove(topicId)
            _selectedTopicIds.value = current
            onDeleted()
        }
    }

    fun isValidTopicName(name: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9-_.~%]+$")
        return name.isNotBlank() && regex.matches(name)
    }
}
