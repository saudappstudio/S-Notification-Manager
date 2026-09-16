package com.saudappstudio.snotificationmanager.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.saudappstudio.snotificationmanager.R

/**
 * Reusable confirmation dialog for critical or destructive operations (e.g. production sending, deleting).
 */
@Composable
fun SNotificationConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = stringResource(R.string.btn_continue),
    dismissText: String = stringResource(R.string.btn_cancel),
    isDestructive: Boolean = false
) {
    SNotificationDialog(
        title = title,
        onDismissRequest = onDismiss,
        confirmText = confirmText,
        dismissText = dismissText,
        onConfirm = onConfirm,
        isDestructive = isDestructive
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
