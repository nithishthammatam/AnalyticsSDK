package com.shopzen.analytics.session

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.shopzen.analytics.core.AnalyticsSDK
import java.util.UUID

class SessionManager : DefaultLifecycleObserver {
    private var currentSessionId: String? = null
    private var lastBackgroundTime: Long = 0
    private val SESSION_TIMEOUT_MS = 30 * 60 * 1000L // 30 minutes

    fun getSessionId(): String? = currentSessionId

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        val now = System.currentTimeMillis()
        
        // If no session exists or 30 minutes have passed since we went to background
        if (currentSessionId == null || (lastBackgroundTime > 0 && now - lastBackgroundTime > SESSION_TIMEOUT_MS)) {
            startNewSession()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        lastBackgroundTime = System.currentTimeMillis()
        AnalyticsSDK.track("app_backgrounded", mapOf("timestamp" to lastBackgroundTime))
    }

    private fun startNewSession() {
        val oldSessionId = currentSessionId
        if (oldSessionId != null) {
            AnalyticsSDK.track("session_end", mapOf("session_id" to oldSessionId))
        }
        
        val newSessionId = "sess_" + UUID.randomUUID().toString()
        currentSessionId = newSessionId
        val isFirstSession = (oldSessionId == null) 
        AnalyticsSDK.track("session_start", mapOf("session_id" to newSessionId, "is_first_session" to isFirstSession))
    }

    fun forceNewSession() {
        startNewSession()
    }
}
