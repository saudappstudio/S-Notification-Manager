package com.saudappstudio.snotificationmanager.domain.usecase

import com.saudappstudio.snotificationmanager.domain.model.ExportDataModel
import com.saudappstudio.snotificationmanager.domain.repository.SettingsRepository

/**
 * UseCase to import a configuration snapshot into the local database.
 */
class ImportConfigurationUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(data: ExportDataModel): Result<Unit> {
        return settingsRepository.importData(data)
    }
}
