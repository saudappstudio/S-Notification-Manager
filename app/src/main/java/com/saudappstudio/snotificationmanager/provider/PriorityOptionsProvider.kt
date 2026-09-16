package com.saudappstudio.snotificationmanager.provider

import androidx.annotation.StringRes
import com.saudappstudio.snotificationmanager.R

/**
 * Option item for notification priority selection.
 */
data class PriorityOption(
    val key: String,
    @StringRes val labelRes: Int
)

/**
 * Provider for notification priority options.
 */
object PriorityOptionsProvider {
    val options: List<PriorityOption> = listOf(
        PriorityOption("HIGH", R.string.priority_high),
        PriorityOption("NORMAL", R.string.priority_normal)
    )
}
