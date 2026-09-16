package com.saudappstudio.snotificationmanager.provider

import androidx.annotation.StringRes
import com.saudappstudio.snotificationmanager.R

/**
 * Option item for on-tap click action selection.
 */
data class ActionTypeOption(
    val key: String,
    @StringRes val labelRes: Int
)

/**
 * Provider for notification tap action types.
 */
object ActionTypeOptionsProvider {
    val options: List<ActionTypeOption> = listOf(
        ActionTypeOption("OPEN_APP", R.string.action_open_app),
        ActionTypeOption("DEEP_LINK", R.string.action_open_deep_link),
        ActionTypeOption("OPEN_URL", R.string.action_open_url),
        ActionTypeOption("NONE", R.string.action_none)
    )
}
