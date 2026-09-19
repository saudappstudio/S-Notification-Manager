package com.saudappstudio.snotificationmanager.presentation.analytics

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.saudappstudio.snotificationmanager.R
import com.saudappstudio.snotificationmanager.core.ui.ToastManager
import com.saudappstudio.snotificationmanager.core.ui.WindowSizeClass
import com.saudappstudio.snotificationmanager.core.ui.WindowSizeClassHelper
import com.saudappstudio.snotificationmanager.domain.model.AnalyticsEventModel
import com.saudappstudio.snotificationmanager.domain.model.AnalyticsMetricsSummary
import com.saudappstudio.snotificationmanager.domain.model.AppModel
import com.saudappstudio.snotificationmanager.domain.model.DemographicItem
import com.saudappstudio.snotificationmanager.presentation.components.EmptyStateView
import com.saudappstudio.snotificationmanager.provider.AnalyticsProvider
import java.util.Locale

/**
 * Main Composable for the Firebase Analytics Dashboard Screen (Real GA4 Data API Integration).
 *
 * @param viewModel AnalyticsViewModel instance managing state.
 * @param onNavigateUp Callback triggered when back action is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDashboardScreen(
    viewModel: AnalyticsViewModel,
    onNavigateUp: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val events by viewModel.analyticsEvents.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val windowSizeClass = WindowSizeClassHelper.rememberWindowSizeClass()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.analytics_title),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = stringResource(R.string.analytics_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.btn_cancel)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val jsonReport = viewModel.exportAnalyticsReport()
                        clipboardManager.setText(AnnotatedString(jsonReport))
                        ToastManager.show(context, R.string.msg_analytics_exported)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.analytics_export_report)
                        )
                    }
                    IconButton(onClick = { viewModel.updateMetrics() }) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.btn_refresh)
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
        ) {
            // App Filter Tabs
            item {
                AppFilterBar(
                    apps = state.appsList,
                    selectedAppId = state.selectedAppId,
                    onAppSelected = { viewModel.onAppSelected(it) }
                )
            }

            // Timeframe Selector Chips
            item {
                TimeRangeSelector(
                    selectedKey = state.selectedTimeRange,
                    onSelected = { viewModel.onTimeRangeSelected(it) }
                )
            }

            // Error Notice Banner if backend or credentials returned an error
            state.errorMessage?.let { error ->
                item {
                    AnalyticsErrorCard(errorMessage = error)
                }
            }

            // Realtime Active Users Live Card
            item {
                RealtimeActiveUsersCard(
                    activeCount = state.metricsSummary.realtimeActiveUsers
                )
            }

            // Summary Metrics Grid (Responsive Layout)
            item {
                AnalyticsMetricsGrid(
                    metrics = state.metricsSummary,
                    windowSizeClass = windowSizeClass
                )
            }

            // Search Bar for Events
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.analytics_search_placeholder)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Tracked Events Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.analytics_section_events),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${events.size} ${stringResource(R.string.analytics_event_name_header)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Events List or Empty View
            if (events.isEmpty() && !state.isLoading) {
                item {
                    EmptyStateView(
                        title = stringResource(R.string.analytics_empty_title),
                        description = stringResource(R.string.analytics_empty_desc),
                        icon = Icons.Default.Analytics
                    )
                }
            } else {
                items(events, key = { it.id }) { event ->
                    AnalyticsEventCard(event = event)
                }
            }

            // OS & Device Demographics Section
            if (state.osDemographics.isNotEmpty()) {
                item {
                    DemographicsSection(
                        title = stringResource(R.string.analytics_section_demographics),
                        osItems = state.osDemographics,
                        deviceItems = state.deviceDemographics
                    )
                }
            }

            // Firebase Web Console Launcher Button
            item {
                OutlinedCard(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://console.firebase.google.com"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.analytics_open_console),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Filter bar for toggling between applications.
 */
@Composable
private fun AppFilterBar(
    apps: List<AppModel>,
    selectedAppId: String,
    onAppSelected: (String) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = if (selectedAppId.isEmpty()) 0 else apps.indexOfFirst { it.id == selectedAppId } + 1,
        edgePadding = 0.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Tab(
            selected = selectedAppId.isEmpty(),
            onClick = { onAppSelected("") },
            text = { Text(stringResource(R.string.crash_filter_all_apps)) }
        )
        apps.forEach { app ->
            Tab(
                selected = selectedAppId == app.id,
                onClick = { onAppSelected(app.id) },
                text = { Text(app.name, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}

/**
 * Filter bar for time range options (Today, 7D, 30D, 90D).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TimeRangeSelector(
    selectedKey: String,
    onSelected: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        AnalyticsProvider.timeRangeOptions.forEach { option ->
            val isSelected = selectedKey == option.key
            FilterChip(
                selected = isSelected,
                onClick = { onSelected(option.key) },
                label = { Text(stringResource(option.labelRes)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

/**
 * Error card informing the user of backend or GA4 Data API issues.
 */
@Composable
private fun AnalyticsErrorCard(errorMessage: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Firebase GA4 Analytics Connection Notice",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

/**
 * Card displaying real-time active users in the last 30 minutes.
 */
@Composable
private fun RealtimeActiveUsersCard(
    activeCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.analytics_realtime_active),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$activeCount",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Icon(
                imageVector = Icons.Default.Analytics,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

/**
 * Grid of summary metric cards (DAU, WAU, MAU, Total Events, Open Rate).
 */
@Composable
private fun AnalyticsMetricsGrid(
    metrics: AnalyticsMetricsSummary,
    windowSizeClass: WindowSizeClass
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = stringResource(R.string.analytics_metric_dau),
                value = "${metrics.dau}",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = stringResource(R.string.analytics_metric_wau),
                value = "${metrics.wau}",
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = stringResource(R.string.analytics_metric_mau),
                value = "${metrics.mau}",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = stringResource(R.string.analytics_metric_total_events),
                value = "${metrics.totalEvents}",
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = stringResource(R.string.analytics_metric_avg_session),
                value = "${metrics.avgSessionDurationSeconds}s",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = stringResource(R.string.analytics_metric_conversion_rate),
                value = String.format(Locale.getDefault(), "%.1f%%", metrics.notificationOpenRate),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Individual metric tile card.
 */
@Composable
private fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Card displaying details of an individual tracked event.
 */
@Composable
private fun AnalyticsEventCard(
    event: AnalyticsEventModel
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = event.eventName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CategoryBadge(category = event.category)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${event.eventCount} ${stringResource(R.string.analytics_event_count_header)} • ${event.uniqueUsers} ${stringResource(R.string.analytics_event_users_header)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                val isPositive = event.growthTrendPercentage >= 0
                val icon = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
                val color = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = String.format(Locale.getDefault(), "%.1f%%", event.growthTrendPercentage),
                    style = MaterialTheme.typography.labelLarge,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Category tag badge.
 */
@Composable
private fun CategoryBadge(category: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Section presenting user device & OS demographics.
 */
@Composable
private fun DemographicsSection(
    title: String,
    osItems: List<DemographicItem>,
    deviceItems: List<DemographicItem>
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.crash_os_version),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                osItems.forEach { item ->
                    DemographicProgressRow(item = item)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.crash_device_model),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                deviceItems.forEach { item ->
                    DemographicProgressRow(item = item)
                }
            }
        }
    }
}

/**
 * Row composable displaying a demographic item progress bar.
 */
@Composable
private fun DemographicProgressRow(item: DemographicItem) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = String.format(Locale.getDefault(), "%.1f%%", item.percentage),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = { item.percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
