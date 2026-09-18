package com.saudappstudio.snotificationmanager.presentation.send

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BasicAlertDialog
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.FragmentActivity
import com.saudappstudio.snotificationmanager.R
import com.saudappstudio.snotificationmanager.core.security.BiometricAuthManager
import com.saudappstudio.snotificationmanager.core.ui.ToastManager
import com.saudappstudio.snotificationmanager.domain.model.Environment
import com.saudappstudio.snotificationmanager.domain.model.TargetType
import com.saudappstudio.snotificationmanager.presentation.components.EnvironmentBadge
import com.saudappstudio.snotificationmanager.presentation.components.KeyValueEditor
import com.saudappstudio.snotificationmanager.presentation.components.NotificationPreviewCard
import com.saudappstudio.snotificationmanager.presentation.components.SNotificationDialog
import com.saudappstudio.snotificationmanager.presentation.components.TestModeBanner
import com.saudappstudio.snotificationmanager.provider.ActionTypeOptionsProvider
import com.saudappstudio.snotificationmanager.provider.InAppMessagingProvider
import com.saudappstudio.snotificationmanager.provider.PriorityOptionsProvider
import com.saudappstudio.snotificationmanager.provider.ScheduleOptionsProvider
import com.saudappstudio.snotificationmanager.provider.ScheduleTimeUnit
import com.saudappstudio.snotificationmanager.provider.ScheduleType
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    var eventDropdownExpanded by remember { mutableStateOf(false) }
    var unitDropdownExpanded by remember { mutableStateOf(false) }
    var advancedExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(initialAppId, initialTemplateId) {
        viewModel.initializeFromParams(initialAppId, initialTemplateId)
    }

    LaunchedEffect(state.sendSuccess) {
        if (state.sendSuccess) {
            ToastManager.show(context, R.string.msg_notification_sent_success)
            snackbarHostState.showSnackbar(context.getString(R.string.msg_notification_sent_success))
            viewModel.resetSuccess()
        }
    }

    LaunchedEffect(state.isScheduleSuccess) {
        if (state.isScheduleSuccess) {
            ToastManager.show(context, R.string.msg_notification_scheduled_success)
            snackbarHostState.showSnackbar(context.getString(R.string.msg_notification_scheduled_success))
            viewModel.resetSuccess()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            val errorMsg = context.getString(R.string.msg_notification_send_failed, error)
            ToastManager.show(context, errorMsg)
            snackbarHostState.showSnackbar(errorMsg)
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

            // STEP 2: NOTIFICATION TYPE & IN-APP MESSAGING
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.field_notification_type),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = state.notificationType == "PUSH",
                            onClick = { viewModel.setNotificationType("PUSH") },
                            label = {
                                Text(
                                    stringResource(R.string.notification_type_push),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = state.notificationType == "IN_APP",
                            onClick = { viewModel.setNotificationType("IN_APP") },
                            label = {
                                Text(
                                    stringResource(R.string.notification_type_in_app),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // In-App Messaging Event Trigger Picker
                    if (state.notificationType == "IN_APP") {
                        Text(
                            text = stringResource(R.string.field_event_trigger),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        ExposedDropdownMenuBox(
                            expanded = eventDropdownExpanded,
                            onExpandedChange = { eventDropdownExpanded = it }
                        ) {
                            val selectedOption = InAppMessagingProvider.eventOptions.find { it.eventKey == state.eventTrigger }
                            val displayLabel = if (selectedOption != null) {
                                stringResource(selectedOption.labelRes)
                            } else {
                                state.eventTrigger
                            }

                            OutlinedTextField(
                                value = displayLabel,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.field_event_trigger)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = eventDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = eventDropdownExpanded,
                                onDismissRequest = { eventDropdownExpanded = false }
                            ) {
                                InAppMessagingProvider.eventOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(stringResource(opt.labelRes)) },
                                        onClick = {
                                            viewModel.setEventTrigger(opt.eventKey)
                                            eventDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        if (state.eventTrigger == "custom") {
                            OutlinedTextField(
                                value = state.customEventName,
                                onValueChange = { viewModel.setCustomEventName(it) },
                                label = { Text("Custom Event Key") },
                                placeholder = { Text("e.g. level_up, cart_abandoned") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Primary Action Button Option for In-App Notification
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.pref_show_primary_button),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Switch(
                                checked = state.showPrimaryButton,
                                onCheckedChange = { viewModel.setShowPrimaryButton(it) }
                            )
                        }

                        AnimatedVisibility(visible = state.showPrimaryButton) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = state.primaryButtonText,
                                    onValueChange = { viewModel.setPrimaryButtonText(it) },
                                    label = { Text(stringResource(R.string.field_primary_button_text)) },
                                    placeholder = { Text(stringResource(R.string.placeholder_primary_button_text)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = state.primaryButtonUrl,
                                    onValueChange = { viewModel.setPrimaryButtonUrl(it) },
                                    label = { Text(stringResource(R.string.field_primary_button_url)) },
                                    placeholder = { Text(stringResource(R.string.placeholder_primary_button_url)) },
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val item = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                                            if (item.isNotBlank()) {
                                                viewModel.setPrimaryButtonUrl(item)
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
                }
            }

            // STEP 3: DISPATCH MODE & FLEXIBLE SCHEDULING
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.field_dispatch_mode),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !state.isScheduled,
                            onClick = { viewModel.setIsScheduled(false) },
                            label = {
                                Text(
                                    stringResource(R.string.dispatch_mode_now),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = state.isScheduled,
                            onClick = { viewModel.setIsScheduled(true) },
                            label = {
                                Text(
                                    stringResource(R.string.dispatch_mode_schedule),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (state.isScheduled) {
                        Text(
                            text = stringResource(R.string.field_schedule_delay),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Mode Selector: Quick Presets / Custom Delay / Specific Date & Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ScheduleType.entries.forEach { mode ->
                                FilterChip(
                                    selected = state.scheduleType == mode,
                                    onClick = { viewModel.setScheduleType(mode) },
                                    label = {
                                        Text(
                                            stringResource(mode.labelRes),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Sub-view based on active ScheduleType
                        when (state.scheduleType) {
                            ScheduleType.PRESETS -> {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    ScheduleOptionsProvider.quickPresets.forEach { preset ->
                                        FilterChip(
                                            selected = state.scheduleDelayMinutes == preset.delayMinutes,
                                            onClick = { viewModel.setScheduleDelayMinutes(preset.delayMinutes) },
                                            label = { Text(stringResource(preset.labelRes), maxLines = 1) },
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }
                                }
                            }
                            ScheduleType.CUSTOM_DELAY -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = state.customDelayValueInput,
                                        onValueChange = { viewModel.setCustomDelayValueInput(it) },
                                        label = { Text(stringResource(R.string.field_custom_delay_value)) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    ExposedDropdownMenuBox(
                                        expanded = unitDropdownExpanded,
                                        onExpandedChange = { unitDropdownExpanded = it },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        OutlinedTextField(
                                            value = stringResource(state.customDelayUnit.labelRes),
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text(stringResource(R.string.field_custom_delay_unit)) },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitDropdownExpanded) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        ExposedDropdownMenu(
                                            expanded = unitDropdownExpanded,
                                            onDismissRequest = { unitDropdownExpanded = false }
                                        ) {
                                            ScheduleOptionsProvider.timeUnits.forEach { unit ->
                                                DropdownMenuItem(
                                                    text = { Text(stringResource(unit.labelRes)) },
                                                    onClick = {
                                                        viewModel.setCustomDelayUnit(unit)
                                                        unitDropdownExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            ScheduleType.EXACT_TIME -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            val cal = Calendar.getInstance().apply {
                                                if (state.selectedScheduledTimestamp > 0) {
                                                    timeInMillis = state.selectedScheduledTimestamp
                                                }
                                            }
                                            DatePickerDialog(
                                                context,
                                                { _, y, m, d -> viewModel.setScheduledDate(y, m, d) },
                                                cal.get(Calendar.YEAR),
                                                cal.get(Calendar.MONTH),
                                                cal.get(Calendar.DAY_OF_MONTH)
                                            ).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(stringResource(R.string.btn_select_date), maxLines = 1)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val cal = Calendar.getInstance().apply {
                                                if (state.selectedScheduledTimestamp > 0) {
                                                    timeInMillis = state.selectedScheduledTimestamp
                                                }
                                            }
                                            TimePickerDialog(
                                                context,
                                                { _, h, m -> viewModel.setScheduledTime(h, m) },
                                                cal.get(Calendar.HOUR_OF_DAY),
                                                cal.get(Calendar.MINUTE),
                                                false
                                            ).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(stringResource(R.string.btn_select_time), maxLines = 1)
                                    }
                                }
                            }
                        }

                        // Target Delivery Scheduled Time Banner
                        if (state.formattedScheduledTime.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "${stringResource(R.string.label_scheduled_target_time)} ${state.formattedScheduledTime}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // STEP 4: AUDIENCE
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = state.targetType == TargetType.TOPIC,
                            onClick = { viewModel.setTargetType(TargetType.TOPIC) },
                            label = { Text(stringResource(R.string.target_type_topic), maxLines = 1) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = state.targetType == TargetType.TOKEN,
                            onClick = { viewModel.setTargetType(TargetType.TOKEN) },
                            label = { Text(stringResource(R.string.target_type_token), maxLines = 1) },
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

            // STEP 5: CONTENT
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

            // STEP 6: ACTION & DEEP LINK
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

            // STEP 7: ADVANCED OPTIONS (Collapsible)
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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

            // STEP 8: LIVE PREVIEW
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
                    channelName = state.channelId,
                    notificationType = state.notificationType,
                    showPrimaryButton = state.showPrimaryButton,
                    primaryButtonText = state.primaryButtonText,
                    primaryButtonUrl = state.primaryButtonUrl
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ACTION BUTTONS: Single Line, Responsive Layout
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
                    Text(
                        text = stringResource(R.string.btn_send_test),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
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
                        val icon = if (state.isScheduled) Icons.Default.Schedule else Icons.Default.Send
                        val actionLabel = if (state.isScheduled) {
                            stringResource(R.string.btn_schedule_notification)
                        } else {
                            stringResource(R.string.btn_send_notification)
                        }
                        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = actionLabel,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    // CONFIRMATION DIALOG (Mandatory confirmation before dispatching push)
    if (state.showConfirmDialog) {
        val titleRes = if (state.pendingIsTest) R.string.dialog_confirm_test_send_title else R.string.dialog_confirm_send_title
        val descRes = if (state.pendingIsTest) R.string.dialog_confirm_test_send_msg else R.string.dialog_confirm_send_msg
        val isProd = state.selectedApp?.environment == Environment.PRODUCTION && !state.pendingIsTest

        SNotificationDialog(
            title = stringResource(titleRes),
            icon = if (isProd) Icons.Default.Warning else Icons.Default.Send,
            description = stringResource(descRes),
            onDismissRequest = { viewModel.dismissConfirmDialog() },
            confirmText = stringResource(R.string.btn_send_now),
            dismissText = stringResource(R.string.btn_cancel),
            isDestructive = isProd,
            onConfirm = { viewModel.confirmProductionSend() }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "App: ${state.selectedApp?.name ?: "Default"}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Audience: ${if (state.targetType == TargetType.TOPIC) state.selectedTopic else state.tokenInput}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Title: ${state.title}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (state.isScheduled && state.formattedScheduledTime.isNotBlank()) {
                    Text(
                        text = "Scheduled: ${state.formattedScheduledTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (state.pendingIsTest) {
                    Text(
                        text = "MODE: TEST DISPATCH",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                } else if (isProd) {
                    Text(
                        text = "ENVIRONMENT: PRODUCTION",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // SENDING PROGRESS OVERLAY DIALOG ("UI like it's sending the Notification")
    if (state.isSending) {
        BasicAlertDialog(
            onDismissRequest = { /* Non-cancelable while sending */ },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )

                    Text(
                        text = stringResource(R.string.progress_sending_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = stringResource(R.string.progress_sending_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer
                    )

                    Text(
                        text = "Dispatching to ${state.selectedApp?.name ?: "Application"}...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
