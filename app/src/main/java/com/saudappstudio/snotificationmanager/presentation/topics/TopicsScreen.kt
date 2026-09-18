package com.saudappstudio.snotificationmanager.presentation.topics

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.saudappstudio.snotificationmanager.R
import com.saudappstudio.snotificationmanager.core.ui.ToastManager
import com.saudappstudio.snotificationmanager.domain.model.TopicModel
import com.saudappstudio.snotificationmanager.presentation.components.EmptyStateView
import com.saudappstudio.snotificationmanager.presentation.components.EnvironmentBadge
import com.saudappstudio.snotificationmanager.presentation.components.SNotificationConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicsScreen(
    viewModel: TopicsViewModel,
    onNavigateBack: () -> Unit,
    onAddTopicClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var topicToDelete by remember { mutableStateOf<TopicModel?>(null) }
    var showDeleteAllConfirm by remember { mutableStateOf(false) }
    var showDeleteSelectedConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (state.selectedTopicIds.isNotEmpty()) {
                        Text("${state.selectedTopicIds.size} Selected")
                    } else {
                        Text(stringResource(R.string.topics_title))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (state.selectedTopicIds.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Text(
                                text = "Clear",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { showDeleteSelectedConfirm = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.btn_delete),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    } else if (state.topics.isNotEmpty()) {
                        IconButton(onClick = { viewModel.selectAllTopics() }) {
                            Icon(
                                imageVector = Icons.Default.SelectAll,
                                contentDescription = "Select All",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { showDeleteAllConfirm = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Delete All",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTopicClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.btn_add_topic))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.topics_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = {
                    Text(
                        text = "Search topics...",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (state.filteredTopics.isEmpty()) {
                EmptyStateView(
                    title = stringResource(R.string.empty_topics_title),
                    description = stringResource(R.string.empty_topics_desc),
                    actionButtonText = stringResource(R.string.btn_add_topic),
                    onActionClick = onAddTopicClick
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(state.filteredTopics) { topic ->
                        val appName = state.apps.find { it.id == topic.appId }?.name ?: "All Apps"
                        val isSelected = state.selectedTopicIds.contains(topic.id)
                        TopicRowItem(
                            topic = topic,
                            appName = appName,
                            isSelected = isSelected,
                            onToggleSelect = { viewModel.toggleSelectTopic(topic.id) },
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("FCM Topic", topic.name)
                                clipboard.setPrimaryClip(clip)
                                ToastManager.show(context, R.string.msg_copied_to_clipboard)
                            },
                            onDelete = { topicToDelete = topic }
                        )
                    }
                }
            }
        }
    }

    if (topicToDelete != null) {
        SNotificationConfirmDialog(
            title = stringResource(R.string.dialog_delete_title),
            message = stringResource(R.string.dialog_delete_msg, topicToDelete!!.name),
            isDestructive = true,
            confirmText = stringResource(R.string.btn_delete),
            onConfirm = {
                viewModel.deleteTopic(topicToDelete!!.id) {
                    ToastManager.show(context, R.string.msg_item_deleted)
                    topicToDelete = null
                }
            },
            onDismiss = { topicToDelete = null }
        )
    }

    if (showDeleteSelectedConfirm) {
        SNotificationConfirmDialog(
            title = "Delete Selected Topics",
            message = "Are you sure you want to delete ${state.selectedTopicIds.size} selected topic(s)? This action cannot be undone.",
            isDestructive = true,
            confirmText = stringResource(R.string.btn_delete),
            onConfirm = {
                viewModel.deleteSelectedTopics {
                    ToastManager.show(context, R.string.msg_item_deleted)
                    showDeleteSelectedConfirm = false
                }
            },
            onDismiss = { showDeleteSelectedConfirm = false }
        )
    }

    if (showDeleteAllConfirm) {
        SNotificationConfirmDialog(
            title = "Delete All Topics",
            message = "Are you sure you want to delete ALL ${state.topics.size} topic(s)? This action cannot be undone.",
            isDestructive = true,
            confirmText = "Delete All",
            onConfirm = {
                viewModel.deleteAllTopics {
                    ToastManager.show(context, "All topics deleted")
                    showDeleteAllConfirm = false
                }
            },
            onDismiss = { showDeleteAllConfirm = false }
        )
    }
}

@Composable
private fun TopicRowItem(
    topic: TopicModel,
    appName: String,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleSelect() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelect() }
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = topic.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    EnvironmentBadge(environment = topic.environment)
                }
                Spacer(modifier = Modifier.height(3.dp))
                if (topic.description.isNotBlank()) {
                    Text(
                        text = topic.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(
                    text = "App: $appName",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row {
                IconButton(onClick = onCopy) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = stringResource(R.string.btn_copy),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.btn_delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
