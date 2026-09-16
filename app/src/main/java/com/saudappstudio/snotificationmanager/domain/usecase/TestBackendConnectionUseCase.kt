package com.saudappstudio.snotificationmanager.domain.usecase

import com.saudappstudio.snotificationmanager.domain.model.BackendHealthModel
import com.saudappstudio.snotificationmanager.domain.repository.SettingsRepository

/**
 * UseCase for testing connectivity and serverless function readiness.
 */
class TestBackendConnectionUseCase(
    private val settingsRepository: SettingsRepository
) {
    /**
     * Queries the Netlify /health endpoint and updates connection timestamp.
     *
     * @return Result containing BackendHealthModel or error.
     */
    suspend operator fun invoke(): Result<BackendHealthModel> {
        return settingsRepository.checkBackendHealth()
    }
}
