package com.saudappstudio.snotificationmanager.presentation.topics

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
import androidx.compose.ui.unit.dp
import com.saudappstudio.snotificationmanager.R
import com.saudappstudio.snotificationmanager.core.ui.ToastManager
import com.saudappstudio.snotificationmanager.domain.model.Environment
import com.saudappstudio.snotificationmanager.domain.model.TopicModel
import com.saudappstudio.snotificationmanager.provider.EnvironmentOptionsProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTopicScreen(
    viewModel: TopicsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var topicName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedAppId by remember { mutableStateOf(state.apps.firstOrNull()?.id ?: "") }
    var selectedEnv by remember { mutableStateOf(Environment.PRODUCTION) }

    var appDropdownExpanded by remember { mutableStateOf(false) }
    var envDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_topic_title)) },
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

            val isNameValid = topicName.isBlank() || viewModel.isValidTopicName(topicName)
            OutlinedTextField(
                value = topicName,
                onValueChange = { topicName = it },
                label = { Text(stringResource(R.string.field_topic_name)) },
                placeholder = { Text("e.g. dictionary_word_of_day") },
                isError = !isNameValid,
                supportingText = if (!isNameValid) {
                    { Text(stringResource(R.string.topic_validation_error)) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.field_topic_description)) },
                placeholder = { Text("Describe intended audience...") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            // App Selector
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

            Spacer(modifier = Modifier.height(16.dp))

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
                        if (!viewModel.isValidTopicName(topicName)) {
                            ToastManager.show(context, R.string.topic_validation_error)
                            return@Button
                        }
                        val topic = TopicModel(
                            id = "topic_${System.currentTimeMillis()}",
                            name = topicName.trim(),
                            description = description.trim(),
                            appId = selectedAppId,
                            environment = selectedEnv,
                            enabled = true
                        )
                        viewModel.saveTopic(topic) {
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
        }
    }
}
