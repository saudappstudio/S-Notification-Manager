package com.saudappstudio.snotificationmanager.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.unit.dp
import com.saudappstudio.snotificationmanager.R
import com.saudappstudio.snotificationmanager.core.ui.ToastManager
import com.saudappstudio.snotificationmanager.domain.model.NotificationHistoryModel
import com.saudappstudio.snotificationmanager.presentation.components.EmptyStateView
import com.saudappstudio.snotificationmanager.presentation.components.EnvironmentBadge
import com.saudappstudio.snotificationmanager.presentation.components.SNotificationConfirmDialog
import com.saudappstudio.snotificationmanager.presentation.components.StatusBadge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToDetails: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history_title)) },
                actions = {
                    if (state.historyItems.isNotEmpty()) {
                        IconButton(onClick = { showClearConfirmDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = stringResource(R.string.btn_clear_history)
                            )
                        }
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
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.history_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text(stringResource(R.string.history_search_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filters Row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = state.activeFilter == HistoryFilter.ALL,
                        onClick = { viewModel.setFilter(HistoryFilter.ALL) },
                        label = { Text(stringResource(R.string.filter_all)) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                item {
                    FilterChip(
                        selected = state.activeFilter == HistoryFilter.SENT,
                        onClick = { viewModel.setFilter(HistoryFilter.SENT) },
                        label = { Text(stringResource(R.string.filter_sent)) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                item {
                    FilterChip(
                        selected = state.activeFilter == HistoryFilter.FAILED,
                        onClick = { viewModel.setFilter(HistoryFilter.FAILED) },
                        label = { Text(stringResource(R.string.filter_failed)) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                item {
                    FilterChip(
                        selected = state.activeFilter == HistoryFilter.PRODUCTION,
                        onClick = { viewModel.setFilter(HistoryFilter.PRODUCTION) },
                        label = { Text(stringResource(R.string.filter_production)) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                item {
                    FilterChip(
                        selected = state.activeFilter == HistoryFilter.TESTING,
                        onClick = { viewModel.setFilter(HistoryFilter.TESTING) },
                        label = { Text(stringResource(R.string.filter_testing)) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (state.filteredItems.isEmpty()) {
                EmptyStateView(
                    title = stringResource(R.string.empty_history_title),
                    description = stringResource(R.string.empty_history_desc)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(state.filteredItems) { item ->
                        HistoryCardItem(
                            item = item,
                            onClick = { onNavigateToDetails(item.id) }
                        )
                    }
                }
            }
        }
    }

    if (showClearConfirmDialog) {
        SNotificationConfirmDialog(
            title = stringResource(R.string.dialog_clear_history_title),
            message = stringResource(R.string.dialog_clear_history_msg),
            isDestructive = true,
            confirmText = stringResource(R.string.btn_delete),
            onConfirm = {
                viewModel.clearAllHistory {
                    ToastManager.show(context, R.string.msg_history_cleared)
                    showClearConfirmDialog = false
                }
            },
            onDismiss = { showClearConfirmDialog = false }
        )
    }
}

@Composable
private fun HistoryCardItem(
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
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    EnvironmentBadge(environment = item.environment)
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${item.appName} • ${item.target}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                val dateFormatted = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(item.sentAt))
                Text(
                    text = dateFormatted,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            StatusBadge(status = item.status)
        }
    }
}
