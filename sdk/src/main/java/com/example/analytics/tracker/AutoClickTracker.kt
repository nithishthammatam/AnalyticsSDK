package com.example.analytics.tracker

import android.view.View
import com.example.analytics.core.AnalyticsSDK

/**
 * Extension function to automatically track a click event and still execute your click logic.
 * 
 * Usage:
 * myButton.trackClick("checkout_button_clicked") {
 *     // Your normal click logic here
 * }
 */
fun View.trackClick(
    eventName: String, 
    properties: Map<String, Any>? = null, 
    onClick: ((View) -> Unit)? = null
) {
    this.setOnClickListener { view ->
        AnalyticsSDK.track(eventName, properties)
        onClick?.invoke(view)
    }
}
