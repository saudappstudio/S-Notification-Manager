package com.saudappstudio.snotificationmanager.presentation.send

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.saudappstudio.snotificationmanager.R
import com.saudappstudio.snotificationmanager.core.security.BiometricAuthManager
import com.saudappstudio.snotificationmanager.core.ui.ToastManager
import com.saudappstudio.snotificationmanager.domain.model.Environment
import com.saudappstudio.snotificationmanager.domain.model.TargetType
import com.saudappstudio.snotificationmanager.presentation.components.EnvironmentBadge
import com.saudappstudio.snotificationmanager.presentation.components.KeyValueEditor
import com.saudappstudio.snotificationmanager.presentation.components.NotificationPreviewCard
import com.saudappstudio.snotificationmanager.presentation.components.SNotificationConfirmDialog
import com.saudappstudio.snotificationmanager.presentation.components.SNotificationDialog
import com.saudappstudio.snotificationmanager.presentation.components.TestModeBanner
import com.saudappstudio.snotificationmanager.provider.ActionTypeOptionsProvider
import com.saudappstudio.snotificationmanager.provider.PriorityOptionsProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendNotificationScreen(
    viewModel: SendNotificationViewModel,
    initialAppId: String = "",
    initialTemplateId: String = "",
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var appDropdownExpanded by remember { mutableStateOf(false) }
    var topicDropdownExpanded by remember { mutableStateOf(false) }
    var actionDropdownExpanded by remember { mutableStateOf(false) }
    var priorityDropdownExpanded by remember { mutableStateOf(false) }
    var advancedExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(initialAppId, initialTemplateId) {
        viewModel.initializeFromParams(initialAppId, initialTemplateId)
    }

    LaunchedEffect(state.sendSuccess) {
        if (state.sendSuccess) {
            snackbarHostState.showSnackbar(context.getString(R.string.msg_notification_sent_success))
            viewModel.resetSuccess()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(context.getString(R.string.msg_notification_send_failed, error))
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.send_title)) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Safety Banner if Test Mode Only active
            if (state.userPreferences.testModeOnly) {
                TestModeBanner()
            }

            // STEP 1: APPLICATION SELECTION
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.send_step_app),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    ExposedDropdownMenuBox(
                        expanded = appDropdownExpanded,
                        onExpandedChange = { appDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = state.selectedApp?.name ?: "Select Application",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.field_select_app)) },
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
                                        viewModel.setSelectedApp(app.id)
                                        appDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    state.selectedApp?.let { app ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Firebase: ${state.selectedProject?.name ?: "Default"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(8.dp))
                            EnvironmentBadge(environment = app.environment)
                        }
                    }
                }
            }

            // STEP 2: AUDIENCE
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.send_step_audience),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Target Type Segmented Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = state.targetType == TargetType.TOPIC,
                            onClick = { viewModel.setTargetType(TargetType.TOPIC) },
                            label = { Text(stringResource(R.string.target_type_topic)) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = state.targetType == TargetType.TOKEN,
                            onClick = { viewModel.setTargetType(TargetType.TOKEN) },
                            label = { Text(stringResource(R.string.target_type_token)) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (state.targetType == TargetType.TOPIC) {
                        ExposedDropdownMenuBox(
                            expanded = topicDropdownExpanded,
                            onExpandedChange = { topicDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = state.selectedTopic,
                                onValueChange = { viewModel.setSelectedTopic(it) },
                                label = { Text(stringResource(R.string.field_select_topic)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = topicDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = topicDropdownExpanded,
                                onDismissRequest = { topicDropdownExpanded = false }
                            ) {
                                state.topics.forEach { topic ->
                                    DropdownMenuItem(
                                        text = { Text("${topic.name} (${topic.description})") },
                                        onClick = {
                                            viewModel.setSelectedTopic(topic.name)
                                            topicDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = state.tokenInput,
                            onValueChange = { viewModel.setTokenInput(it) },
                            label = { Text(stringResource(R.string.field_device_token)) },
                            placeholder = { Text("Paste FCM registration token...") },
                            trailingIcon = {
                                IconButton(onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val item = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                                    if (item.isNotBlank()) {
                                        viewModel.setTokenInput(item)
                                    }
                                }) {
                                    Icon(Icons.Default.ContentPaste, contentDescription = stringResource(R.string.btn_paste))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // STEP 3: CONTENT
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.send_step_content),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = state.title,
                        onValueChange = { viewModel.setTitle(it) },
                        label = { Text(stringResource(R.string.field_notification_title)) },
                        placeholder = { Text("e.g. Word of the Day") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = state.message,
                        onValueChange = { viewModel.setMessage(it) },
                        label = { Text(stringResource(R.string.field_notification_message)) },
                        placeholder = { Text("Discover a new advanced English word today.") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 6,
                        supportingText = {
                            Text(stringResource(R.string.char_count_format, state.message.length, 250))
                        },
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = state.imageUrl,
                        onValueChange = { viewModel.setImageUrl(it) },
                        label = { Text(stringResource(R.string.field_image_url)) },
                        placeholder = { Text("https://example.com/banner.png") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // STEP 4: ACTION & DEEP LINK
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.send_step_action),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    ExposedDropdownMenuBox(
                        expanded = actionDropdownExpanded,
                        onExpandedChange = { actionDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = state.clickAction,
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
                                        viewModel.setClickAction(opt.key)
                                        actionDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (state.clickAction == "DEEP_LINK" || state.clickAction == "OPEN_URL") {
                        OutlinedTextField(
                            value = state.deepLink,
                            onValueChange = { viewModel.setDeepLink(it) },
                            label = { Text(stringResource(R.string.field_deep_link)) },
                            placeholder = { Text("sauddictionary://word/serendipity") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // STEP 5: ADVANCED OPTIONS (Collapsible)
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.send_step_advanced),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { advancedExpanded = !advancedExpanded }) {
                            Icon(
                                imageVector = if (advancedExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                        }
                    }

                    AnimatedVisibility(visible = advancedExpanded) {
                        Column(
                            modifier = Modifier.padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = state.channelId,
                                onValueChange = { viewModel.setChannelId(it) },
                                label = { Text(stringResource(R.string.field_channel_id)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenuBox(
                                expanded = priorityDropdownExpanded,
                                onExpandedChange = { priorityDropdownExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = state.priority,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(stringResource(R.string.field_priority)) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityDropdownExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = priorityDropdownExpanded,
                                    onDismissRequest = { priorityDropdownExpanded = false }
                                ) {
                                    PriorityOptionsProvider.options.forEach { opt ->
                                        DropdownMenuItem(
                                            text = { Text(stringResource(opt.labelRes)) },
                                            onClick = {
                                                viewModel.setPriority(opt.key)
                                                priorityDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            KeyValueEditor(
                                dataMap = state.customData,
                                onDataChange = { viewModel.setCustomData(it) }
                            )
                        }
                    }
                }
            }

            // STEP 6: LIVE PREVIEW
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.send_step_preview),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                NotificationPreviewCard(
                    appName = state.selectedApp?.name ?: "App Name",
                    title = state.title,
                    message = state.message,
                    imageUrl = state.imageUrl,
                    channelName = state.channelId
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ACTION BUTTONS: Test Send & Send Now
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.sendTest() },
                    enabled = !state.isSending,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.btn_send_test))
                }

                Button(
                    onClick = {
                        if (state.userPreferences.requireBiometricForProd && state.selectedApp?.environment == Environment.PRODUCTION) {
                            val activity = context as? FragmentActivity
                            val biometric = BiometricAuthManager(context)
                            if (activity != null && biometric.canAuthenticate()) {
                                biometric.authenticate(
                                    activity = activity,
                                    onAuthenticated = { viewModel.onSendClicked() },
                                    onError = { err -> ToastManager.show(context, err) }
                                )
                            } else {
                                viewModel.onSendClicked()
                            }
                        } else {
                            viewModel.onSendClicked()
                        }
                    },
                    enabled = !state.isSending,
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.btn_send_notification))
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    // PRODUCTION CONFIRMATION DIALOG (Mandatory before live push)
    if (state.showConfirmDialog) {
        SNotificationDialog(
            title = stringResource(R.string.dialog_confirm_production_title),
            onDismissRequest = { viewModel.dismissConfirmDialog() },
            confirmText = stringResource(R.string.btn_send_now),
            dismissText = stringResource(R.string.btn_cancel),
            isDestructive = true,
            onConfirm = { viewModel.confirmProductionSend() }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.dialog_confirm_production_msg),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Send to: ${state.selectedApp?.name ?: ""}", fontWeight = FontWeight.SemiBold)
                Text("Audience: ${if (state.targetType == TargetType.TOPIC) state.selectedTopic else state.tokenInput}")
                Text("Message: ${state.title}")
                Text(
                    text = "ENVIRONMENT: PRODUCTION",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
