package com.saudappstudio.snotificationmanager.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.saudappstudio.snotificationmanager.R

/**
 * Reusable, theme-aware standard dialog conforming strictly to Rule 4.
 * Features an optional icon, title, description, custom content slot, and single-line action buttons.
 *
 * @param title Dialog headline text.
 * @param onDismissRequest Callback invoked when the user cancels or taps outside the dialog.
 * @param modifier Optional modifier for the dialog surface container.
 * @param icon Optional leading icon vector displayed at the top of the dialog.
 * @param iconTint Optional custom tint for the dialog icon.
 * @param description Optional descriptive body text.
 * @param confirmText Text for the primary confirm button.
 * @param dismissText Text for the optional dismiss button.
 * @param isDestructive When true, styles the confirm button with the theme error color.
 * @param onConfirm Callback executed on primary button tap.
 * @param content Optional custom composable content slot rendered beneath the description.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SNotificationDialog(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color? = null,
    description: String? = null,
    confirmText: String = stringResource(R.string.btn_continue),
    dismissText: String? = stringResource(R.string.btn_cancel),
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    content: (@Composable () -> Unit)? = null
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header: Icon + Title
                if (icon != null) {
                    val resolvedTint = iconTint ?: if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = resolvedTint,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(bottom = 8.dp)
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Optional Description text
                if (!description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Custom Content Slot
                if (content != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    content()
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Proper single-line action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (dismissText != null) {
                        OutlinedButton(
                            onClick = onDismissRequest,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = dismissText,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(12.dp),
                        colors = if (isDestructive) {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        } else {
                            ButtonDefaults.buttonColors()
                        }
                    ) {
                        Text(
                            text = confirmText,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
