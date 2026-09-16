package com.saudappstudio.snotificationmanager.presentation.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saudappstudio.snotificationmanager.domain.model.FirebaseProjectModel
import com.saudappstudio.snotificationmanager.domain.repository.FirebaseProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FirebaseProjectsUiState(
    val projects: List<FirebaseProjectModel> = emptyList(),
    val searchQuery: String = "",
    val filteredProjects: List<FirebaseProjectModel> = emptyList()
)

@HiltViewModel
class FirebaseProjectsViewModel @Inject constructor(
    private val firebaseProjectRepository: FirebaseProjectRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<FirebaseProjectsUiState> = combine(
        firebaseProjectRepository.getAllProjects(),
        _searchQuery
    ) { projects, query ->
        val filtered = if (query.isBlank()) {
            projects
        } else {
            projects.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.projectIdentifier.contains(query, ignoreCase = true) ||
                it.backendKey.contains(query, ignoreCase = true)
            }
        }
        FirebaseProjectsUiState(
            projects = projects,
            searchQuery = query,
            filteredProjects = filtered
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FirebaseProjectsUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun saveProject(project: FirebaseProjectModel, onSaved: () -> Unit) {
        viewModelScope.launch {
            val existing = firebaseProjectRepository.getProjectById(project.id)
            if (existing != null) {
                firebaseProjectRepository.updateProject(project)
            } else {
                firebaseProjectRepository.insertProject(project)
            }
            onSaved()
        }
    }

    fun deleteProject(projectId: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            firebaseProjectRepository.deleteProject(projectId)
            onDeleted()
        }
    }
}
