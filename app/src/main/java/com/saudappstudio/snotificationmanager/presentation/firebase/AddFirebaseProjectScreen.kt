package com.saudappstudio.snotificationmanager.presentation.firebase

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
import com.saudappstudio.snotificationmanager.domain.model.FirebaseProjectModel
import com.saudappstudio.snotificationmanager.provider.EnvironmentOptionsProvider
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFirebaseProjectScreen(
    viewModel: FirebaseProjectsViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var projectIdentifier by remember { mutableStateOf("") }
    var backendKey by remember { mutableStateOf("") }
    var selectedEnv by remember { mutableStateOf(Environment.PRODUCTION) }
    var envDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_firebase_project_title)) },
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
                label = { Text(stringResource(R.string.field_project_display_name)) },
                placeholder = { Text("e.g. Saud Dictionary") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = projectIdentifier,
                onValueChange = { projectIdentifier = it },
                label = { Text(stringResource(R.string.field_project_identifier)) },
                placeholder = { Text("saud-dictionary-prod") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = backendKey,
                onValueChange = { backendKey = it },
                label = { Text(stringResource(R.string.field_backend_config_key)) },
                placeholder = { Text(stringResource(R.string.firebase_key_hint)) },
                supportingText = { Text("Matches Netlify FIREBASE_<KEY>_SERVICE_ACCOUNT") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

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
                        if (name.isBlank() || projectIdentifier.isBlank() || backendKey.isBlank()) {
                            ToastManager.show(context, R.string.msg_validation_missing_fields)
                            return@Button
                        }
                        val project = FirebaseProjectModel(
                            id = "proj_",
                            name = name.trim(),
                            projectIdentifier = projectIdentifier.trim(),
                            environment = selectedEnv,
                            backendKey = backendKey.trim().lowercase(),
                            enabled = true
                        )
                        viewModel.saveProject(project) {
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
