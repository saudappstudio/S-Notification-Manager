package com.saudappstudio.snotificationmanager.presentation.apps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.saudappstudio.snotificationmanager.R
import com.saudappstudio.snotificationmanager.core.ui.ToastManager
import com.saudappstudio.snotificationmanager.domain.model.AppModel
import com.saudappstudio.snotificationmanager.domain.model.Environment
import com.saudappstudio.snotificationmanager.provider.EnvironmentOptionsProvider
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppScreen(
    viewModel: AppsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var appName by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("") }
    var appId by remember { mutableStateOf("") }
    var selectedProjectId by remember { mutableStateOf(state.projects.firstOrNull()?.id ?: "") }
    var defaultTopic by remember { mutableStateOf("") }
    var selectedEnv by remember { mutableStateOf(Environment.PRODUCTION) }
    var description by remember { mutableStateOf("") }

    var allowPush by remember { mutableStateOf(true) }
    var allowTopic by remember { mutableStateOf(true) }
    var allowToken by remember { mutableStateOf(true) }
    var allowImage by remember { mutableStateOf(true) }
    var allowDeepLinks by remember { mutableStateOf(true) }

    var projectDropdownExpanded by remember { mutableStateOf(false) }
    var envDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_app_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.btn_cancel)
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = appName,
                onValueChange = { appName = it },
                label = { Text(stringResource(R.string.field_app_name)) },
                placeholder = { Text("e.g. Advanced English Dictionary") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = packageName,
                onValueChange = { packageName = it },
                label = { Text(stringResource(R.string.field_package_name)) },
                placeholder = { Text("com.saudappstudio.dictionary") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = appId,
                onValueChange = { appId = it },
                label = { Text(stringResource(R.string.field_app_id)) },
                placeholder = { Text("dictionary_app_01") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Firebase Project Dropdown
            ExposedDropdownMenuBox(
                expanded = projectDropdownExpanded,
                onExpandedChange = { projectDropdownExpanded = it }
            ) {
                val currentProject = state.projects.find { it.id == selectedProjectId }
                OutlinedTextField(
                    value = currentProject?.name ?: "Select Project",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.field_firebase_project)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = projectDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = projectDropdownExpanded,
                    onDismissRequest = { projectDropdownExpanded = false }
                ) {
                    state.projects.forEach { proj ->
                        DropdownMenuItem(
                            text = { Text(proj.name) },
                            onClick = {
                                selectedProjectId = proj.id
                                projectDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Environment Dropdown
            ExposedDropdownMenuBox(
                expanded = envDropdownExpanded,
                onExpandedChange = { envDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedEnv.key,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.field_environment)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = envDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = envDropdownExpanded,
                    onDismissRequest = { envDropdownExpanded = false }
                ) {
                    EnvironmentOptionsProvider.options.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(stringResource(opt.labelRes)) },
                            onClick = {
                                selectedEnv = opt.environment
                                envDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = defaultTopic,
                onValueChange = { defaultTopic = it },
                label = { Text(stringResource(R.string.field_default_topic)) },
                placeholder = { Text("e.g. dictionary_all") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.field_description)) },
                placeholder = { Text("App notification purpose...") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            // Behavior Toggles Section
            Text(
                text = stringResource(R.string.section_default_behavior),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            CheckboxRow(text = stringResource(R.string.pref_allow_push), checked = allowPush, onCheckedChange = { allowPush = it })
            CheckboxRow(text = stringResource(R.string.pref_allow_topic), checked = allowTopic, onCheckedChange = { allowTopic = it })
            CheckboxRow(text = stringResource(R.string.pref_allow_token), checked = allowToken, onCheckedChange = { allowToken = it })
            CheckboxRow(text = stringResource(R.string.pref_allow_image), checked = allowImage, onCheckedChange = { allowImage = it })
            CheckboxRow(text = stringResource(R.string.pref_allow_deep_links), checked = allowDeepLinks, onCheckedChange = { allowDeepLinks = it })

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.btn_cancel))
                }

                Button(
                    onClick = {
                        if (appName.isBlank() || packageName.isBlank()) {
                            ToastManager.show(context, R.string.msg_validation_missing_fields)
                            return@Button
                        }
                        val newApp = AppModel(
                            id = UUID.randomUUID().toString(),
                            name = appName.trim(),
                            packageName = packageName.trim(),
                            appId = appId.ifBlank { "app_${System.currentTimeMillis()}" },
                            firebaseProjectId = selectedProjectId.ifBlank { "proj_default" },
                            environment = selectedEnv,
                            defaultTopic = defaultTopic.trim(),
                            allowPush = allowPush,
                            allowTopic = allowTopic,
                            allowToken = allowToken,
                            allowImage = allowImage,
                            allowDeepLinks = allowDeepLinks,
                            description = description.trim()
                        )
                        viewModel.saveApp(newApp) {
                            ToastManager.show(context, R.string.msg_item_saved)
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.btn_save))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CheckboxRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
