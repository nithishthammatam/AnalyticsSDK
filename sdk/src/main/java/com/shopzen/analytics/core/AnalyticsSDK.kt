package com.shopzen.analytics.core

import android.content.Context
import android.util.Log
import com.shopzen.analytics.storage.EventDatabase
import com.shopzen.analytics.storage.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.shopzen.analytics.session.SessionManager
import com.shopzen.analytics.session.UserIdentity
import com.shopzen.analytics.network.UploadWorker
import com.shopzen.analytics.tracker.ScreenTracker
import com.shopzen.analytics.util.DebugLogger
import com.shopzen.analytics.util.DeviceInfo
import java.util.concurrent.TimeUnit
import android.app.Application

object AnalyticsSDK {
    private var isInitialized = false
    private lateinit var config: SDKConfig
    private lateinit var eventRepository: EventRepository
    private lateinit var userIdentity: UserIdentity
    private lateinit var sessionManager: SessionManager
    private lateinit var appContext: Context
    private var isOptedOut: Boolean = false
    
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun init(context: Context, config: SDKConfig) {
        if (isInitialized) {
            logDebug("AnalyticsSDK is already initialized.")
            return
        }
        this.appContext = context.applicationContext
        this.config = config
        
        // Initialize Room DB and Repository
        val database = EventDatabase.getDatabase(appContext)
        eventRepository = EventRepository(database.eventDao())
        
        // Initialize Session & Identity
        userIdentity = UserIdentity(appContext)
        sessionManager = SessionManager()
        try {
            ProcessLifecycleOwner.get().lifecycle.addObserver(sessionManager)
        } catch (e: IllegalStateException) {
            // Ignore in test environments where ProcessLifecycleOwner is not initialized
        }
        
        // Auto-track screens if context is an Application
        if (appContext is Application) {
            (appContext as Application).registerActivityLifecycleCallbacks(ScreenTracker())
        }
        
        this.isInitialized = true
        logDebug("AnalyticsSDK initialized with endpoint: ${config.endpoint}")
        
        startUploadWorker(appContext, config)
        startForegroundFlusher()
    }

    private fun startUploadWorker(context: Context, config: SDKConfig) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = Data.Builder()
            .putString("endpoint", config.endpoint)
            .putInt("batch_size", config.batchSize)
            .build()

        // WorkManager minimum periodic interval is 15 minutes. 
        // We use the configured interval, but Android will clamp it to 15m.
        val request = PeriodicWorkRequestBuilder<UploadWorker>(
            config.flushIntervalSeconds.toLong(), TimeUnit.SECONDS
        )
            .setConstraints(constraints)
            .setInputData(inputData)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "AnalyticsUploadWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun startForegroundFlusher() {
        coroutineScope.launch {
            while (true) {
                delay(config.flushIntervalSeconds * 1000L)
                if (config.enabled) {
                    flush()
                }
            }
        }
    }

    fun flush() {
        if (!checkInitialized()) return
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = Data.Builder()
            .putString("endpoint", config.endpoint)
            .putInt("batch_size", config.batchSize)
            .build()

        val request = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputData(inputData)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(appContext).enqueueUniqueWork(
            "AnalyticsUploadWorker_Manual",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun track(eventName: String, properties: Map<String, Any>? = null) {
        if (!checkInitialized()) return
        if (isOptedOut) return
        
        if (config.debug) {
            DebugLogger.logEvent(eventName, properties)
        }
        
        coroutineScope.launch {
            val event = Event(
                name = eventName,
                timestamp = System.currentTimeMillis(),
                properties = properties,
                userId = userIdentity.userId,
                sessionId = sessionManager.getSessionId(),
                anonymousId = userIdentity.anonymousId,
                deviceInfo = DeviceInfo.get(appContext)
            )
            eventRepository.saveEvent(event)
            logDebug("Saved event to database: ${event.name}")
        }
    }

    fun trackScreen(screenName: String, properties: Map<String, Any>? = null) {
        val props = properties?.toMutableMap() ?: mutableMapOf()
        props["screen_name"] = screenName
        track("screen_view", props)
    }

    fun identify(userId: String, properties: Map<String, Any>? = null) {
        if (!checkInitialized()) return
        
        userIdentity.identify(userId)
        logDebug("Identify user: $userId, properties: $properties")
        
        val props = properties?.toMutableMap() ?: mutableMapOf()
        props["user_id"] = userId
        track("user_identified", props)
    }
    
    fun optOut() {
        isOptedOut = true
        logDebug("User opted out of tracking.")
    }

    fun optIn() {
        isOptedOut = false
        logDebug("User opted in to tracking.")
    }
    
    fun reset() {
        if (!checkInitialized()) return
        logDebug("Resetting SDK state")
        userIdentity.clear()
        sessionManager.forceNewSession()
    }

    private fun checkInitialized(): Boolean {
        if (!isInitialized) {
            Log.e("AnalyticsSDK", "SDK not initialized. Call init() first.")
            return false
        }
        return config.enabled
    }

    private fun logDebug(message: String) {
        if (isInitialized && config.debug) {
            DebugLogger.log(message)
        }
    }
}

