package com.saudappstudio.snotificationmanager.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Room TypeConverters for serializing complex fields (e.g. Map<String, String>) to JSON strings.
 */
class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringMap(map: Map<String, String>?): String {
        return if (map == null) "{}" else gson.toJson(map)
    }

    @TypeConverter
    fun toStringMap(json: String?): Map<String, String> {
        if (json.isNullOrBlank()) return emptyMap()
        val type = object : TypeToken<Map<String, String>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyMap()
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
