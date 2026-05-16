package com.example.analytics.storage

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromMap(value: Map<String, Any>?): String? {
        if (value == null) return null
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMap(value: String?): Map<String, Any>? {
        if (value == null) return null
        val type = object : TypeToken<Map<String, Any>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String? {
        if (value == null) return null
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringMap(value: String?): Map<String, String>? {
        if (value == null) return null
        val type = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, type)
    }
}
