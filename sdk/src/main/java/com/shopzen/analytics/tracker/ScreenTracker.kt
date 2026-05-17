package com.shopzen.analytics.tracker

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.shopzen.analytics.core.AnalyticsSDK

class ScreenTracker : Application.ActivityLifecycleCallbacks {
    private var previousScreen: String? = null
    private var screenStartTime: Long = 0

    override fun onActivityResumed(activity: Activity) {
        val screenName = activity.javaClass.simpleName
        val properties = mutableMapOf<String, Any>("screen_name" to screenName)
        
        previousScreen?.let {
            properties["previous_screen"] = it
        }
        
        screenStartTime = System.currentTimeMillis()
        AnalyticsSDK.track("screen_view", properties)
    }

    override fun onActivityPaused(activity: Activity) {
        val screenName = activity.javaClass.simpleName
        val duration = System.currentTimeMillis() - screenStartTime
        
        // Track the exit of the screen if needed, or just save state
        previousScreen = screenName
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
