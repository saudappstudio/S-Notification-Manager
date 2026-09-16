package com.saudappstudio.snotificationmanager.provider

import androidx.annotation.StringRes
import com.saudappstudio.snotificationmanager.R
import com.saudappstudio.snotificationmanager.domain.model.Environment

/**
 * Option item for environment selectors referencing string resources.
 */
data class EnvironmentOption(
    val environment: Environment,
    @StringRes val labelRes: Int
)

/**
 * Provider for environment dropdown and filter options.
 */
object EnvironmentOptionsProvider {
    val options: List<EnvironmentOption> = listOf(
        EnvironmentOption(Environment.PRODUCTION, R.string.status_production),
        EnvironmentOption(Environment.TESTING, R.string.status_testing),
        EnvironmentOption(Environment.DEVELOPMENT, R.string.status_development)
    )
}
