package com.saudappstudio.snotificationmanager.presentation.apps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.saudappstudio.snotificationmanager.R
import com.saudappstudio.snotificationmanager.core.ui.ToastManager
import com.saudappstudio.snotificationmanager.domain.model.NotificationHistoryModel
import com.saudappstudio.snotificationmanager.presentation.components.EnvironmentBadge
import com.saudappstudio.snotificationmanager.presentation.components.SNotificationConfirmDialog
import com.saudappstudio.snotificationmanager.presentation.components.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailsScreen(
    appId: String,
    viewModel: AppsViewModel,
    onNavigateBack: () -> Unit,
    onEditApp: (String) -> Unit = {},
    onSendNotificationForApp: (String) -> Unit,
    onNotificationClick: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val app = state.selectedApp

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showProdWarningDialog by remember { mutableStateOf(false) }

    LaunchedEffect(appId) {
        viewModel.loadAppDetails(appId)
    }

    if (app == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_details_title)) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Loading application details...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = app.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { onEditApp(app.id) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.btn_edit),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.btn_delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Identity Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = app.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        EnvironmentBadge(environment = app.environment)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = app.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "App ID: ${app.appId}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (app.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = app.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Quick Send Button for this specific app
            Button(
                onClick = { onSendNotificationForApp(app.id) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text(
                    text = "Compose Push to ${app.name}",
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Notification Configuration Section
            Text(
                text = "Default Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Default Topic: ${app.defaultTopic.ifBlank { "None" }}", style = MaterialTheme.typography.bodyMedium)
                    Text("Default Action: ${app.defaultClickAction}", style = MaterialTheme.typography.bodyMedium)
                    Text("Notification Channel: ${app.defaultChannelId}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Safety Controls Section
            Text(
                text = stringResource(R.string.section_app_controls),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ControlSwitchRow(
                        title = stringResource(R.string.pref_enable_notifications),
                        checked = app.enabled,
                        onCheckedChange = { checked ->
                            viewModel.updateAppControls(app.id, checked, app.testMode, app.allowPush, app.requireConfirmForProd)
                        }
                    )

                    ControlSwitchRow(
                        title = stringResource(R.string.pref_enable_test_mode),
                        checked = app.testMode,
                        onCheckedChange = { checked ->
                            viewModel.updateAppControls(app.id, app.enabled, checked, app.allowPush, app.requireConfirmForProd)
                        }
                    )

                    ControlSwitchRow(
                        title = stringResource(R.string.pref_allow_production_sends),
                        checked = app.allowPush,
                        onCheckedChange = { checked ->
                            if (checked) {
                                showProdWarningDialog = true
                            } else {
                                viewModel.updateAppControls(app.id, app.enabled, app.testMode, false, app.requireConfirmForProd)
                            }
                        }
                    )

                    ControlSwitchRow(
                        title = stringResource(R.string.pref_require_confirm_prod),
                        checked = app.requireConfirmForProd,
                        onCheckedChange = { checked ->
                            viewModel.updateAppControls(app.id, app.enabled, app.testMode, app.allowPush, checked)
                        }
                    )
                }
            }

            // App Notifications History Section
            Text(
                text = "Notifications for ${app.name} (${state.appNotifications.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (state.appNotifications.isEmpty()) {
                Text(
                    text = "No notifications sent or scheduled for this application yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.appNotifications.forEach { item ->
                        AppNotificationHistoryRow(
                            item = item,
                            onClick = { onNotificationClick(item.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Delete Confirmation Dialog (Unified, Theme-Aware)
    if (showDeleteConfirmDialog) {
        SNotificationConfirmDialog(
            title = stringResource(R.string.dialog_delete_title),
            icon = Icons.Default.Delete,
            message = stringResource(R.string.dialog_delete_msg, app.name),
            isDestructive = true,
            confirmText = stringResource(R.string.btn_delete),
            onConfirm = {
                viewModel.deleteApp(app.id) {
                    ToastManager.show(context, R.string.msg_item_deleted)
                    showDeleteConfirmDialog = false
                    onNavigateBack()
                }
            },
            onDismiss = { showDeleteConfirmDialog = false }
        )
    }

    // Production Warning Confirmation Dialog
    if (showProdWarningDialog) {
        SNotificationConfirmDialog(
            title = stringResource(R.string.dialog_confirm_production_title),
            icon = Icons.Default.Warning,
            message = "Enabling live production notifications allows pushing real alerts to all end users. Are you sure?",
            confirmText = stringResource(R.string.btn_continue),
            onConfirm = {
                viewModel.updateAppControls(app.id, app.enabled, app.testMode, true, app.requireConfirmForProd)
                showProdWarningDialog = false
            },
            onDismiss = { showProdWarningDialog = false }
        )
    }
}

@Composable
private fun AppNotificationHistoryRow(
    item: NotificationHistoryModel,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                val targetInfo = if (item.notificationType == "IN_APP") {
                    "In-App: ${item.eventTrigger}"
                } else {
                    "${item.targetType.key}: ${item.target}"
                }
                Text(
                    text = targetInfo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(status = item.status)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ControlSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
