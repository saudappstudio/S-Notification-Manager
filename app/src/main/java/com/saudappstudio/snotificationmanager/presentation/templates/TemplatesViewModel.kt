package com.saudappstudio.snotificationmanager.presentation.templates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saudappstudio.snotificationmanager.core.datastore.PreferencesManager
import com.saudappstudio.snotificationmanager.core.datastore.UserPreferences
import com.saudappstudio.snotificationmanager.domain.model.AppModel
import com.saudappstudio.snotificationmanager.domain.model.TemplateModel
import com.saudappstudio.snotificationmanager.domain.repository.AppRepository
import com.saudappstudio.snotificationmanager.domain.repository.CloudinaryRepository
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

enum class TemplateFilter {
    ALL,
    FAVORITES,
    BY_APP
}

data class TemplatesUiState(
    val templates: List<TemplateModel> = emptyList(),
    val filteredTemplates: List<TemplateModel> = emptyList(),
    val apps: List<AppModel> = emptyList(),
    val searchQuery: String = "",
    val activeFilter: TemplateFilter = TemplateFilter.ALL,
    val selectedAppIdFilter: String = "",
    val userPreferences: UserPreferences = UserPreferences()
)

@HiltViewModel
class TemplatesViewModel @Inject constructor(
    private val templateRepository: TemplateRepository,
    private val appRepository: AppRepository,
    private val preferencesManager: PreferencesManager,
    val cloudinaryRepository: CloudinaryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _activeFilter = MutableStateFlow(TemplateFilter.ALL)
    private val _selectedAppIdFilter = MutableStateFlow("")

    val uiState: StateFlow<TemplatesUiState> = combine(
        combine(
            templateRepository.getAllTemplates(),
            appRepository.getAllApps(),
            preferencesManager.userPreferencesFlow
        ) { templates, apps, prefs ->
            Triple(templates, apps, prefs)
        },
        _searchQuery,
        _activeFilter,
        _selectedAppIdFilter
    ) { (templates, apps, prefs), query, filter, appFilter ->
        val filtered = templates.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.message.contains(query, ignoreCase = true)
            val matchesFilter = when (filter) {
                TemplateFilter.ALL -> true
                TemplateFilter.FAVORITES -> item.isFavorite
                TemplateFilter.BY_APP -> appFilter.isBlank() || item.appId == appFilter
            }
            matchesQuery && matchesFilter
        }
        TemplatesUiState(
            templates = templates,
            filteredTemplates = filtered,
            apps = apps,
            searchQuery = query,
            activeFilter = filter,
            selectedAppIdFilter = appFilter,
            userPreferences = prefs
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TemplatesUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: TemplateFilter, appId: String = "") {
        _activeFilter.value = filter
        _selectedAppIdFilter.value = appId
    }

    fun toggleFavorite(templateId: String) {
        viewModelScope.launch {
            templateRepository.toggleFavorite(templateId)
        }
    }

    fun duplicateTemplate(template: TemplateModel) {
        viewModelScope.launch {
            val copy = template.copy(
                id = UUID.randomUUID().toString(),
                name = "${template.name} (Copy)",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            templateRepository.insertTemplate(copy)
        }
    }

    fun deleteTemplate(templateId: String) {
        viewModelScope.launch {
            templateRepository.deleteTemplate(templateId)
        }
    }

    fun saveTemplate(template: TemplateModel, onSaved: () -> Unit) {
        viewModelScope.launch {
            val existing = templateRepository.getTemplateById(template.id)
            if (existing != null) {
                templateRepository.updateTemplate(template.copy(updatedAt = System.currentTimeMillis()))
            } else {
                templateRepository.insertTemplate(template)
            }
            onSaved()
        }
    }
}
