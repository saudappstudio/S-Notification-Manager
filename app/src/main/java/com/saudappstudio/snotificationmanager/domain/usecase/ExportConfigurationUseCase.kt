package com.saudappstudio.snotificationmanager.domain.usecase

import com.saudappstudio.snotificationmanager.domain.model.ExportDataModel
import com.saudappstudio.snotificationmanager.domain.repository.SettingsRepository

/**
 * UseCase to export all configured apps, projects, topics, and templates into a transferable structure.
 */
class ExportConfigurationUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): ExportDataModel {
        return settingsRepository.exportData()
    }
}
