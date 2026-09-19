package com.saudappstudio.snotificationmanager.presentation.fiam

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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.OpenInNew
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Image
import com.saudappstudio.snotificationmanager.R
import com.saudappstudio.snotificationmanager.core.ui.ToastManager
import com.saudappstudio.snotificationmanager.domain.model.TargetType
import com.saudappstudio.snotificationmanager.presentation.components.EnvironmentBadge
import com.saudappstudio.snotificationmanager.presentation.components.ImagePickerUploadField
import com.saudappstudio.snotificationmanager.presentation.components.KeyValueEditor
import com.saudappstudio.snotificationmanager.presentation.components.TestModeBanner
import com.saudappstudio.snotificationmanager.provider.InAppMessagingProvider

/**
 * Dedicated Compose screen for creating, customizing, and dispatching Firebase In-App Messages (FIAM).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InAppMessagingScreen(
    viewModel: InAppMessagingViewModel,
    initialAppId: String = "",
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var appDropdownExpanded by remember { mutableStateOf(false) }
    var topicDropdownExpanded by remember { mutableStateOf(false) }
    var eventDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(initialAppId) {
        viewModel.initializeWithApp(initialAppId)
    }

    LaunchedEffect(state.sendSuccess) {
        if (state.sendSuccess) {
            ToastManager.show(context, R.string.msg_notification_sent_success)
            snackbarHostState.showSnackbar(context.getString(R.string.msg_notification_sent_success))
            viewModel.resetSuccess()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { err ->
            val msg = context.getString(R.string.msg_notification_send_failed, err)
            ToastManager.show(context, msg)
            snackbarHostState.showSnackbar(msg)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.fiam_title))
                    }
                },
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
            if (state.userPreferences.testModeOnly) {
                TestModeBanner()
            }

            // SECTION 1: TARGET APPLICATION
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Firebase: ${state.selectedProject?.name ?: "Default"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            EnvironmentBadge(environment = app.environment)
                        }
                    }
                }
            }

            // SECTION 2: FIAM LAYOUT FORMAT & TRIGGER
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.fiam_layout_type),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Format Chips Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FiamLayoutType.entries.forEach { fmt ->
                            val labelRes = when (fmt) {
                                FiamLayoutType.CARD -> R.string.fiam_layout_card
                                FiamLayoutType.MODAL -> R.string.fiam_layout_modal
                                FiamLayoutType.BANNER -> R.string.fiam_layout_banner
                                FiamLayoutType.IMAGE_ONLY -> R.string.fiam_layout_image_only
                            }
                            FilterChip(
                                selected = state.layoutType == fmt,
                                onClick = { viewModel.setLayoutType(fmt) },
                                label = {
                                    Text(
                                        stringResource(labelRes),
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
                            placeholder = { Text("e.g. level_up, streak_achieved") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // SECTION 3: AUDIENCE TARGETING
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
                            placeholder = { Text("Paste FCM registration token…") },
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

            // SECTION 4: IN-APP MESSAGE CONTENT & CLOUDINARY UPLOAD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.send_step_content),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (state.layoutType != FiamLayoutType.IMAGE_ONLY) {
                        OutlinedTextField(
                            value = state.title,
                            onValueChange = { viewModel.setTitle(it) },
                            label = { Text(stringResource(R.string.field_notification_title)) },
                            placeholder = { Text("e.g. Special Offer Inside!") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = state.body,
                            onValueChange = { viewModel.setBody(it) },
                            label = { Text(stringResource(R.string.field_notification_message)) },
                            placeholder = { Text("Unlock premium features today with a 50% discount.") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 5,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Cloudinary Image Upload Component
                    ImagePickerUploadField(
                        imageUrl = state.imageUrl,
                        onUrlChange = { viewModel.setImageUrl(it) },
                        cloudinaryRepository = viewModel.cloudinaryRepository,
                        cloudName = state.userPreferences.cloudinaryCloudName,
                        uploadPreset = state.userPreferences.cloudinaryUploadPreset
                    )

                    // Primary Action Button Controls
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
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Secondary Action Button Controls for CARD layout
                    if (state.layoutType == FiamLayoutType.CARD) {
                        OutlinedTextField(
                            value = state.secondaryButtonText,
                            onValueChange = { viewModel.setSecondaryButtonText(it) },
                            label = { Text(stringResource(R.string.fiam_field_secondary_button_text)) },
                            placeholder = { Text("e.g. Maybe Later") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = state.secondaryButtonUrl,
                            onValueChange = { viewModel.setSecondaryButtonUrl(it) },
                            label = { Text(stringResource(R.string.fiam_field_secondary_button_url)) },
                            placeholder = { Text("e.g. app://dismiss") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Key / Value Custom Data
                    Text(
                        text = stringResource(R.string.field_custom_data),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    KeyValueEditor(
                        dataMap = state.customData,
                        onDataChange = { viewModel.setCustomData(it) }
                    )
                }
            }

            // SECTION 5: LIVE IN-APP DIALOG PREVIEW
            Text(
                text = stringResource(R.string.in_app_preview_badge),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            FiamLivePreviewCard(state = state)

            // DISPATCH BUTTON
            Button(
                onClick = { viewModel.sendInAppMessage() },
                enabled = !state.isSending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (state.isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Dispatching In-App Popup…")
                } else {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_send_fiam))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FiamLivePreviewCard(state: InAppMessagingUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state.layoutType) {
                FiamLayoutType.CARD -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 6.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            if (state.imageUrl.isNotBlank()) {
                                FiamImagePreviewBox(
                                    imageUrl = state.imageUrl,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                )
                            }
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = state.title.ifBlank { "In-App Dialog Title" },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = state.body.ifBlank { "In-app popup notification message content body text..." },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    if (state.secondaryButtonText.isNotBlank()) {
                                        OutlinedButton(
                                            onClick = {},
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(state.secondaryButtonText)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Button(
                                        onClick = {},
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(state.primaryButtonText.ifBlank { "Action" })
                                    }
                                }
                            }
                        }
                    }
                }
                FiamLayoutType.MODAL -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 6.dp,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (state.imageUrl.isNotBlank()) {
                                FiamImagePreviewBox(
                                    imageUrl = state.imageUrl,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            Text(
                                text = state.title.ifBlank { "Modal Title" },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = state.body.ifBlank { "Modal body text description goes here..." },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {},
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(state.primaryButtonText.ifBlank { "OK" })
                            }
                        }
                    }
                }
                FiamLayoutType.BANNER -> {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (state.imageUrl.isNotBlank()) {
                                FiamImagePreviewBox(
                                    imageUrl = state.imageUrl,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = state.title.ifBlank { "Banner Alert" },
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = state.body.ifBlank { "Compact top banner message text..." },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                FiamLayoutType.IMAGE_ONLY -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 6.dp,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        if (state.imageUrl.isNotBlank()) {
                            FiamImagePreviewBox(
                                imageUrl = state.imageUrl,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Upload or enter Image URL for Image-Only popup",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FiamImagePreviewBox(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Banner Image",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
