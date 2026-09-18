package com.saudappstudio.snotificationmanager.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.saudappstudio.snotificationmanager.R
import java.util.UUID

/**
 * Internal model representing a mutable key-value row with a persistent unique identifier.
 */
private data class KeyValueRow(
    val id: String = UUID.randomUUID().toString(),
    var key: String,
    var value: String
)

/**
 * High-performance Key-Value editor for FCM custom data payload dictionary.
 * Uses persistent row IDs to ensure seamless fast typing without cursor jumping,
 * lag, or focus loss.
 *
 * @param dataMap The current Map of string key-values.
 * @param onDataChange Callback invoked when keys or values are modified.
 * @param modifier Optional modifier for the container layout.
 */
@Composable
fun KeyValueEditor(
    dataMap: Map<String, String>,
    onDataChange: (Map<String, String>) -> Unit,
    modifier: Modifier = Modifier
) {
    // Maintain internal row state with stable IDs so key mutations don't recreate Compose nodes
    var rows by remember {
        mutableStateOf(
            dataMap.entries.map { KeyValueRow(key = it.key, value = it.value) }
        )
    }

    // Sync from external changes only when entry counts or external keys fundamentally differ
    LaunchedEffect(dataMap) {
        val currentMapped = rows.associate { it.key to it.value }
        if (currentMapped != dataMap) {
            rows = dataMap.entries.map { KeyValueRow(key = it.key, value = it.value) }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.field_custom_data),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        rows.forEach { rowItem ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = rowItem.key,
                    onValueChange = { newKey ->
                        rowItem.key = newKey
                        rows = rows.map { if (it.id == rowItem.id) it.copy(key = newKey) else it }
                        val newMap = rows.filter { it.key.isNotBlank() }.associate { it.key to it.value }
                        onDataChange(newMap)
                    },
                    placeholder = { Text("Key") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = rowItem.value,
                    onValueChange = { newValue ->
                        rowItem.value = newValue
                        rows = rows.map { if (it.id == rowItem.id) it.copy(value = newValue) else it }
                        val newMap = rows.filter { it.key.isNotBlank() }.associate { it.key to it.value }
                        onDataChange(newMap)
                    },
                    placeholder = { Text("Value") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                IconButton(
                    onClick = {
                        rows = rows.filterNot { it.id == rowItem.id }
                        val newMap = rows.filter { it.key.isNotBlank() }.associate { it.key to it.value }
                        onDataChange(newMap)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.btn_delete),
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        OutlinedButton(
            onClick = {
                val newRow = KeyValueRow(key = "key_${rows.size + 1}", value = "")
                rows = rows + newRow
                val newMap = rows.filter { it.key.isNotBlank() }.associate { it.key to it.value }
                onDataChange(newMap)
            },
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = stringResource(R.string.btn_add_data_row),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
