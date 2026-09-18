package com.saudappstudio.snotificationmanager.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.saudappstudio.snotificationmanager.R

/**
 * Reusable confirmation dialog for critical or destructive operations (e.g. production sending, deleting).
 * Features icon, title, description, and single-line buttons adhering to Rule 4.
 *
 * @param title Headline text for the confirmation dialog.
 * @param message Explanatory description text detailing the consequences of the action.
 * @param onConfirm Action executed upon confirmation.
 * @param onDismiss Action executed upon cancellation or dismiss.
 * @param icon Optional leading icon; defaults to a destructive or informative icon based on [isDestructive].
 * @param iconTint Optional custom tint for the icon.
 * @param confirmText Text on the confirm button.
 * @param dismissText Text on the dismiss button.
 * @param isDestructive Flag indicating whether the action is destructive (affects styling).
 */
@Composable
fun SNotificationConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    icon: ImageVector? = null,
    iconTint: Color? = null,
    confirmText: String = stringResource(R.string.btn_continue),
    dismissText: String = stringResource(R.string.btn_cancel),
    isDestructive: Boolean = false
) {
    val resolvedIcon = icon ?: if (isDestructive) Icons.Default.Delete else Icons.Default.Info

    SNotificationDialog(
        title = title,
        icon = resolvedIcon,
        iconTint = iconTint,
        description = message,
        onDismissRequest = onDismiss,
        confirmText = confirmText,
        dismissText = dismissText,
        onConfirm = onConfirm,
        isDestructive = isDestructive
    )
}
