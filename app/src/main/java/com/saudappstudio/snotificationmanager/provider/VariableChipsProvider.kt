package com.saudappstudio.snotificationmanager.provider

import androidx.annotation.StringRes
import com.saudappstudio.snotificationmanager.R

/**
 * Data item representing an insertable template variable token.
 */
data class VariableChipItem(
    val token: String,
    val label: String
)

/**
 * Static provider for template interpolation variable chips.
 */
object VariableChipsProvider {
    val variables: List<VariableChipItem> = listOf(
        VariableChipItem("{{word}}", "word"),
        VariableChipItem("{{definition}}", "definition"),
        VariableChipItem("{{app_name}}", "app_name"),
        VariableChipItem("{{date}}", "date"),
        VariableChipItem("{{time}}", "time"),
        VariableChipItem("{{user_name}}", "user_name")
    )
}
