package com.shopzen.analytics.core

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val timestamp: Long,
    val properties: Map<String, Any>?,
    val userId: String?,
    val sessionId: String?,
    val anonymousId: String?,
    val deviceInfo: Map<String, String>? // We'll serialize this for now
)
