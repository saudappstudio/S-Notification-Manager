package com.saudappstudio.snotificationmanager.presentation.firebase

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.saudappstudio.snotificationmanager.R
import com.saudappstudio.snotificationmanager.domain.model.FirebaseProjectModel
import com.saudappstudio.snotificationmanager.presentation.components.EmptyStateView
import com.saudappstudio.snotificationmanager.presentation.components.EnvironmentBadge
import com.saudappstudio.snotificationmanager.presentation.components.SNotificationConfirmDialog
import com.saudappstudio.snotificationmanager.presentation.components.SNotificationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirebaseProjectsScreen(
    viewModel: FirebaseProjectsViewModel,
    onNavigateBack: () -> Unit,
    onAddProjectClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var projectToDelete by remember { mutableStateOf<FirebaseProjectModel?>(null) }
    var selectedProjectDetails by remember { mutableStateOf<FirebaseProjectModel?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.firebase_projects_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProjectClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.btn_add_firebase_project))
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
                text = stringResource(R.string.firebase_projects_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Security Notice Banner (Rule 10 of requirements: Never store private keys in APK)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.firebase_security_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.projects.isEmpty()) {
                EmptyStateView(
                    title = stringResource(R.string.empty_projects_title),
                    description = stringResource(R.string.empty_projects_desc),
                    actionButtonText = stringResource(R.string.btn_add_firebase_project),
                    onActionClick = onAddProjectClick
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(state.projects) { project ->
                        FirebaseProjectItem(
                            project = project,
                            onClick = { selectedProjectDetails = project },
                            onDelete = { projectToDelete = project }
                        )
                    }
                }
            }
        }
    }

    // Project Details Dialog
    selectedProjectDetails?.let { proj ->
        val associatedApps = state.apps.filter { it.firebaseProjectId == proj.id }
        val notificationCount = state.notifications.count { n -> associatedApps.any { a -> a.id == n.appId } }

        SNotificationDialog(
            title = proj.name,
            icon = Icons.Default.CloudQueue,
            description = "Detailed mapping configuration and associated application statistics:",
            onDismissRequest = { selectedProjectDetails = null },
            confirmText = "Close",
            onConfirm = { selectedProjectDetails = null }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Environment:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    EnvironmentBadge(environment = proj.environment)
                }

                Text("Project ID: ${proj.projectIdentifier}", style = MaterialTheme.typography.bodySmall)
                Text("Backend Key: ${proj.backendKey}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                Text("Dispatched Pushes: $notificationCount", style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Connected Applications (${associatedApps.size})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (associatedApps.isEmpty()) {
                    Text(
                        text = "No apps connected to this project yet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        associatedApps.forEach { app ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = app.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = app.packageName,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    EnvironmentBadge(environment = app.environment)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    projectToDelete?.let { proj ->
        SNotificationConfirmDialog(
            title = stringResource(R.string.dialog_delete_title),
            message = stringResource(R.string.dialog_delete_msg, proj.name),
            isDestructive = true,
            confirmText = stringResource(R.string.btn_delete),
            onConfirm = {
                viewModel.deleteProject(proj.id) {
                    projectToDelete = null
                }
            },
            onDismiss = { projectToDelete = null }
        )
    }
}

@Composable
private fun FirebaseProjectItem(
    project: FirebaseProjectModel,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
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
                        text = project.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    EnvironmentBadge(environment = project.environment)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Project ID: ${project.projectIdentifier}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Backend Key: ${project.backendKey}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
