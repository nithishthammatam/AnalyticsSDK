package com.shopzen.analytics.util

import android.util.Log
import org.json.JSONObject

object DebugLogger {
    fun logEvent(eventName: String, properties: Map<String, Any>?) {
        try {
            val json = JSONObject(properties ?: emptyMap<String, Any>()).toString(4)
            Log.d("AnalyticsSDK", "Event: $eventName\nProperties:\n$json")
        } catch (e: Exception) {
            Log.d("AnalyticsSDK", "Event: $eventName, Properties: $properties")
        }
    }
    
    fun log(message: String) {
        Log.d("AnalyticsSDK", message)
    }
}
