package com.saudappstudio.snotificationmanager.provider

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.graphics.vector.ImageVector
import com.saudappstudio.snotificationmanager.R

/**
 * Descriptor for a Home Dashboard Quick Action item.
 */
data class QuickActionItem(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val icon: ImageVector,
    val route: String
)

/**
 * Provider generating static dashboard quick action items.
 */
object QuickActionProvider {
    val actions: List<QuickActionItem> = listOf(
        QuickActionItem(
            id = "send",
            titleRes = R.string.home_qa_send_title,
            descriptionRes = R.string.home_qa_send_desc,
            icon = Icons.Default.Send,
            route = "send_notification"
        ),
        QuickActionItem(
            id = "templates",
            titleRes = R.string.home_qa_templates_title,
            descriptionRes = R.string.home_qa_templates_desc,
            icon = Icons.Default.Message,
            route = "templates"
        ),
        QuickActionItem(
            id = "apps",
            titleRes = R.string.home_qa_apps_title,
            descriptionRes = R.string.home_qa_apps_desc,
            icon = Icons.Default.Apps,
            route = "apps"
        ),
        QuickActionItem(
            id = "history",
            titleRes = R.string.home_qa_history_title,
            descriptionRes = R.string.home_qa_history_desc,
            icon = Icons.Default.History,
            route = "history"
        )
    )
}
