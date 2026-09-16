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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.saudappstudio.snotificationmanager.R

/**
 * Key-Value editor for FCM custom data payload dictionary.
 */
@Composable
fun KeyValueEditor(
    dataMap: Map<String, String>,
    onDataChange: (Map<String, String>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.field_custom_data),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        dataMap.entries.forEach { entry ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = entry.key,
                    onValueChange = { newKey ->
                        val updated = dataMap.toMutableMap()
                        val value = updated.remove(entry.key) ?: ""
                        updated[newKey] = value
                        onDataChange(updated)
                    },
                    placeholder = { Text("Key") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = entry.value,
                    onValueChange = { newValue ->
                        val updated = dataMap.toMutableMap()
                        updated[entry.key] = newValue
                        onDataChange(updated)
                    },
                    placeholder = { Text("Value") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                IconButton(
                    onClick = {
                        val updated = dataMap.toMutableMap()
                        updated.remove(entry.key)
                        onDataChange(updated)
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
                val newKey = "key_"
                val updated = dataMap.toMutableMap()
                updated[newKey] = ""
                onDataChange(updated)
            },
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.size(4.dp))
            Text(stringResource(R.string.btn_add_data_row))
        }
    }
}
