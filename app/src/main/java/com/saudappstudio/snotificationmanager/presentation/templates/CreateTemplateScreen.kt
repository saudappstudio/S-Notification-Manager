package com.saudappstudio.snotificationmanager.presentation.templates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.saudappstudio.snotificationmanager.R
import com.saudappstudio.snotificationmanager.core.ui.ToastManager
import com.saudappstudio.snotificationmanager.domain.model.TemplateModel
import com.saudappstudio.snotificationmanager.presentation.components.ImagePickerUploadField
import com.saudappstudio.snotificationmanager.presentation.components.KeyValueEditor
import com.saudappstudio.snotificationmanager.presentation.components.VariableChipSelector
import com.saudappstudio.snotificationmanager.provider.ActionTypeOptionsProvider
import com.saudappstudio.snotificationmanager.provider.VariableChipsProvider
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTemplateScreen(
    viewModel: TemplatesViewModel,
    onNavigateBack: () -> Unit,
    onSaveAndSend: (TemplateModel) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var selectedAppId by remember { mutableStateOf(state.apps.firstOrNull()?.id ?: "") }
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var clickAction by remember { mutableStateOf("OPEN_APP") }
    var deepLink by remember { mutableStateOf("") }
    var customData by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    var appDropdownExpanded by remember { mutableStateOf(false) }
    var actionDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_template_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
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
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.field_template_name)) },
                placeholder = { Text("e.g. Word of the Day") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // App Dropdown
            ExposedDropdownMenuBox(
                expanded = appDropdownExpanded,
                onExpandedChange = { appDropdownExpanded = it }
            ) {
                val currentApp = state.apps.find { it.id == selectedAppId }
                OutlinedTextField(
                    value = currentApp?.name ?: "Select Application",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.field_associated_app)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = appDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = appDropdownExpanded,
                    onDismissRequest = { appDropdownExpanded = false }
                ) {
                    state.apps.forEach { app ->
                        DropdownMenuItem(
                            text = { Text(app.name) },
                            onClick = {
                                selectedAppId = app.id
                                appDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Clickable Variable Chips helper for inserting tokens
            VariableChipSelector(
                variables = VariableChipsProvider.variables,
                onVariableClick = { token ->
                    message = if (message.isBlank()) token else "$message $token"
                }
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.field_notification_title)) },
                placeholder = { Text("e.g. Word of the Day: {{word}}") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text(stringResource(R.string.field_notification_message)) },
                placeholder = { Text("e.g. Today's word is {{word}} meaning {{definition}}") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                supportingText = {
                    Text(stringResource(R.string.char_count_format, message.length, 250))
                },
                shape = RoundedCornerShape(12.dp)
            )

            ImagePickerUploadField(
                imageUrl = imageUrl,
                onUrlChange = { imageUrl = it },
                cloudinaryRepository = viewModel.cloudinaryRepository,
                cloudName = state.userPreferences.cloudinaryCloudName,
                uploadPreset = state.userPreferences.cloudinaryUploadPreset
            )

            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text(stringResource(R.string.field_select_topic)) },
                placeholder = { Text("dictionary_word_of_day") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Click Action Dropdown
            ExposedDropdownMenuBox(
                expanded = actionDropdownExpanded,
                onExpandedChange = { actionDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = clickAction,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.field_tap_action)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = actionDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = actionDropdownExpanded,
                    onDismissRequest = { actionDropdownExpanded = false }
                ) {
                    ActionTypeOptionsProvider.options.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(stringResource(opt.labelRes)) },
                            onClick = {
                                clickAction = opt.key
                                actionDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            if (clickAction == "DEEP_LINK" || clickAction == "OPEN_URL") {
                OutlinedTextField(
                    value = deepLink,
                    onValueChange = { deepLink = it },
                    label = { Text(stringResource(R.string.field_deep_link)) },
                    placeholder = { Text("sauddictionary://word/{{word}}") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            KeyValueEditor(
                dataMap = customData,
                onDataChange = { customData = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (name.isBlank() || title.isBlank() || message.isBlank()) {
                            ToastManager.show(context, R.string.msg_validation_missing_fields)
                            return@OutlinedButton
                        }
                        val template = TemplateModel(
                            id = UUID.randomUUID().toString(),
                            name = name.trim(),
                            appId = selectedAppId,
                            title = title.trim(),
                            message = message.trim(),
                            imageUrl = imageUrl.trim(),
                            topic = topic.trim(),
                            clickAction = clickAction,
                            deepLink = deepLink.trim(),
                            customData = customData
                        )
                        viewModel.saveTemplate(template) {
                            ToastManager.show(context, R.string.msg_item_saved)
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.btn_save),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = {
                        if (name.isBlank() || title.isBlank() || message.isBlank()) {
                            ToastManager.show(context, R.string.msg_validation_missing_fields)
                            return@Button
                        }
                        val template = TemplateModel(
                            id = UUID.randomUUID().toString(),
                            name = name.trim(),
                            appId = selectedAppId,
                            title = title.trim(),
                            message = message.trim(),
                            imageUrl = imageUrl.trim(),
                            topic = topic.trim(),
                            clickAction = clickAction,
                            deepLink = deepLink.trim(),
                            customData = customData
                        )
                        viewModel.saveTemplate(template) {
                            onSaveAndSend(template)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.btn_save_and_send),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
