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
    val searchQuery: String = ""
)

@HiltViewModel
class TopicsViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val appRepository: AppRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<TopicsUiState> = combine(
        topicRepository.getAllTopics(),
        appRepository.getAllApps(),
        _searchQuery
    ) { topics, apps, query ->
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
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TopicsUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
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
            onDeleted()
        }
    }

    fun isValidTopicName(name: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9-_.~%]+$")
        return name.isNotBlank() && regex.matches(name)
    }
}
