package com.saudappstudio.snotificationmanager.presentation.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.saudappstudio.snotificationmanager.presentation.components.SNotificationConfirmDialog
import com.saudappstudio.snotificationmanager.presentation.components.SNotificationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val prefs = state.userPreferences

    // Stable local text field states to avoid lag/dropped characters during fast typing
    var backendUrlInput by remember { mutableStateOf(prefs.backendUrl) }
    var apiTokenInput by remember { mutableStateOf(prefs.apiToken) }
    var cloudNameInput by remember { mutableStateOf(prefs.cloudinaryCloudName) }
    var uploadPresetInput by remember { mutableStateOf(prefs.cloudinaryUploadPreset) }

    LaunchedEffect(prefs.backendUrl) {
        if (backendUrlInput != prefs.backendUrl && backendUrlInput.isEmpty()) {
            backendUrlInput = prefs.backendUrl
        }
    }

    LaunchedEffect(prefs.apiToken) {
        if (apiTokenInput != prefs.apiToken && apiTokenInput.isEmpty()) {
            apiTokenInput = prefs.apiToken
        }
    }

    LaunchedEffect(prefs.cloudinaryCloudName) {
        if (cloudNameInput != prefs.cloudinaryCloudName && cloudNameInput.isEmpty()) {
            cloudNameInput = prefs.cloudinaryCloudName
        }
    }

    LaunchedEffect(prefs.cloudinaryUploadPreset) {
        if (uploadPresetInput != prefs.cloudinaryUploadPreset && uploadPresetInput.isEmpty()) {
            uploadPresetInput = prefs.cloudinaryUploadPreset
        }
    }

    var showClearConfirm by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
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
            Spacer(modifier = Modifier.height(4.dp))

            // SECTION 1: APPEARANCE
            SectionCard(title = stringResource(R.string.settings_section_appearance)) {
                Text(
                    text = stringResource(R.string.pref_theme_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = prefs.themeMode == "SYSTEM",
                        onClick = { viewModel.setThemeMode("SYSTEM") },
                        label = {
                            Text(
                                text = stringResource(R.string.theme_system),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = prefs.themeMode == "LIGHT",
                        onClick = { viewModel.setThemeMode("LIGHT") },
                        label = {
                            Text(
                                text = stringResource(R.string.theme_light),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = prefs.themeMode == "DARK",
                        onClick = { viewModel.setThemeMode("DARK") },
                        label = {
                            Text(
                                text = stringResource(R.string.theme_dark),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // SECTION 2: BACKEND & API CONFIGURATION
            SectionCard(title = stringResource(R.string.settings_section_backend)) {
                OutlinedTextField(
                    value = backendUrlInput,
                    onValueChange = {
                        backendUrlInput = it
                        viewModel.setBackendUrl(it)
                    },
                    label = { Text(stringResource(R.string.backend_url_title)) },
                    placeholder = { Text(stringResource(R.string.backend_url_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = apiTokenInput,
                    onValueChange = {
                        apiTokenInput = it
                        viewModel.setApiToken(it)
                    },
                    label = { Text(stringResource(R.string.api_token_title)) },
                    placeholder = { Text(stringResource(R.string.api_token_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Responsive Connection Test layout
                Column(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { viewModel.testBackendConnection() },
                        enabled = !state.isTestingConnection,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isTestingConnection) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = stringResource(R.string.btn_test_connection),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    state.connectionResult?.let { msg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (state.isConnectionSuccess) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (state.isConnectionSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (state.isConnectionSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // SECTION: CLOUDINARY IMAGE STORAGE
            SectionCard(title = stringResource(R.string.settings_section_cloudinary)) {
                OutlinedTextField(
                    value = cloudNameInput,
                    onValueChange = {
                        cloudNameInput = it
                        viewModel.setCloudinaryConfig(it, uploadPresetInput)
                    },
                    label = { Text(stringResource(R.string.field_cloudinary_cloud_name)) },
                    placeholder = { Text("dvyx3z9vp") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = uploadPresetInput,
                    onValueChange = {
                        uploadPresetInput = it
                        viewModel.setCloudinaryConfig(cloudNameInput, it)
                    },
                    label = { Text(stringResource(R.string.field_cloudinary_upload_preset)) },
                    placeholder = { Text("saud_preset") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // SECTION 3: SAFETY & PRODUCTION CONTROLS
            SectionCard(title = stringResource(R.string.settings_section_safety)) {
                SettingsSwitchRow(
                    title = stringResource(R.string.pref_test_mode_only),
                    subtitle = stringResource(R.string.pref_test_mode_only_desc),
                    checked = prefs.testModeOnly,
                    onCheckedChange = { viewModel.setTestModeOnly(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SettingsSwitchRow(
                    title = stringResource(R.string.pref_require_confirm_prod),
                    subtitle = "Always show confirmation modal before sending to production",
                    checked = prefs.confirmBeforeProdSend,
                    onCheckedChange = { viewModel.setConfirmBeforeProdSend(it) }
                )
            }

            // SECTION 4: SECURITY & BIOMETRICS
            SectionCard(title = stringResource(R.string.settings_section_security)) {
                SettingsSwitchRow(
                    title = "Biometric App Lock",
                    subtitle = "Require biometric / device auth on app open",
                    checked = prefs.requireBiometricOnAppOpen,
                    onCheckedChange = { viewModel.setRequireBiometricOnAppOpen(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SettingsSwitchRow(
                    title = stringResource(R.string.biometric_lock_title),
                    subtitle = stringResource(R.string.biometric_lock_desc),
                    checked = prefs.requireBiometricForProd,
                    onCheckedChange = { viewModel.setRequireBiometricForProd(it) }
                )
            }

            // SECTION 5: DATA MANAGEMENT (Properly Aligned & Single-line Buttons)
            SectionCard(title = stringResource(R.string.settings_section_data)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.exportConfiguration { json ->
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Saud Notification Manager Backup", json)
                                clipboard.setPrimaryClip(clip)
                                ToastManager.show(context, R.string.msg_config_exported)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.btn_export_config),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    OutlinedButton(
                        onClick = { showImportDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.btn_import_config),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    OutlinedButton(
                        onClick = { showClearConfirm = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.btn_clear_history),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    OutlinedButton(
                        onClick = { showResetConfirm = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.btn_reset_settings),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // SECTION 6: ABOUT
            SectionCard(title = stringResource(R.string.settings_section_about)) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.app_version_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.app_internal_admin_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Standardized Dialogs conforming strictly to Rule 4
    if (showClearConfirm) {
        SNotificationConfirmDialog(
            title = stringResource(R.string.dialog_clear_history_title),
            icon = Icons.Default.Delete,
            message = stringResource(R.string.dialog_clear_history_msg),
            isDestructive = true,
            confirmText = stringResource(R.string.btn_delete),
            onConfirm = {
                viewModel.clearAllHistory {
                    ToastManager.show(context, R.string.msg_history_cleared)
                    showClearConfirm = false
                }
            },
            onDismiss = { showClearConfirm = false }
        )
    }

    if (showResetConfirm) {
        SNotificationConfirmDialog(
            title = stringResource(R.string.dialog_reset_settings_title),
            icon = Icons.Default.Refresh,
            message = stringResource(R.string.dialog_reset_settings_msg),
            isDestructive = true,
            confirmText = stringResource(R.string.btn_continue),
            onConfirm = {
                viewModel.resetSettings {
                    backendUrlInput = ""
                    apiTokenInput = ""
                    ToastManager.show(context, R.string.msg_settings_reset)
                    showResetConfirm = false
                }
            },
            onDismiss = { showResetConfirm = false }
        )
    }

    if (showImportDialog) {
        SNotificationDialog(
            title = stringResource(R.string.btn_import_config),
            icon = Icons.Default.Info,
            description = "Paste your exported JSON configuration below to restore your apps and configuration:",
            onDismissRequest = { showImportDialog = false },
            confirmText = stringResource(R.string.btn_continue),
            onConfirm = {
                viewModel.importConfiguration(importJsonInput) { success ->
                    if (success) {
                        ToastManager.show(context, R.string.msg_config_imported)
                        showImportDialog = false
                    } else {
                        ToastManager.show(context, "Failed to import JSON data")
                    }
                }
            }
        ) {
            OutlinedTextField(
                value = importJsonInput,
                onValueChange = { importJsonInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text("{ ... }") },
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
