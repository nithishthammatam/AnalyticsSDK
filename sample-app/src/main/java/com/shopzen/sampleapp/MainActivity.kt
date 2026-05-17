package com.example.sampleapp

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shopzen.analytics.core.AnalyticsSDK
import com.shopzen.analytics.core.SDKConfig
import com.shopzen.analytics.tracker.EcommerceTracker
import com.shopzen.analytics.tracker.trackClick

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this).apply { 
            orientation = LinearLayout.VERTICAL 
            setPadding(50, 50, 50, 50)
        }
        
        val btnAddToCart = Button(this).apply { text = "1. Simulate Full Checkout Funnel" }
        val btnOptOut = Button(this).apply { text = "2. GDPR Opt-Out" }
        val btnOptIn = Button(this).apply { text = "3. GDPR Opt-In" }
        val btnTrackEvent = Button(this).apply { text = "4. Track Generic Event" }
        val btnFlush = Button(this).apply { text = "5. Force Flush (Upload to Webhook)" }
        
        layout.addView(btnAddToCart)
        layout.addView(btnOptOut)
        layout.addView(btnOptIn)
        layout.addView(btnTrackEvent)
        layout.addView(btnFlush)
        setContentView(layout)

        // Using a webhook.site URL to easily verify payload matching schema
        val config = SDKConfig.Builder("https://webhook.site/38557180-03fa-4fcc-848e-00fb23ada600")
            .setDebug(true)
            .setBatchSize(5)
            .build()
            
        AnalyticsSDK.init(applicationContext, config)
        AnalyticsSDK.trackScreen("Home_Screen") 
        
        btnAddToCart.trackClick("test_button_clicked") {
            EcommerceTracker.trackProductView("SHOE_123", "Nike Air Max", "Shoes", 120.00, "USD")
            EcommerceTracker.trackAddToCart("SHOE_123", "Nike Air Max", 2, 120.00, 240.00, "USD")
            EcommerceTracker.trackCheckoutStarted(240.00, 2, "USD")
            EcommerceTracker.trackPaymentAttempted(240.00, "USD", "Credit Card", "Stripe")
            EcommerceTracker.trackPaymentSuccess("ORDER_999", 240.00, "USD", "Credit Card")
            EcommerceTracker.trackOrderCompleted("ORDER_999", 240.00, 2, "USD", "Credit Card")
            EcommerceTracker.trackFunnelStep("Checkout_Flow", "Shipping_Details", 2)
            
            Toast.makeText(this, "Full Funnel tracked! Check Logcat.", Toast.LENGTH_LONG).show()
        }

        btnOptOut.setOnClickListener {
            AnalyticsSDK.optOut()
            Toast.makeText(this, "Opted OUT! Events are now disabled.", Toast.LENGTH_SHORT).show()
        }

        btnOptIn.setOnClickListener {
            AnalyticsSDK.optIn()
            Toast.makeText(this, "Opted IN! Events are enabled again.", Toast.LENGTH_SHORT).show()
        }

        btnTrackEvent.setOnClickListener {
            AnalyticsSDK.track("generic_test_event", mapOf("test_param" to "test_value"))
            Toast.makeText(this, "Fired event. Check Logcat!", Toast.LENGTH_SHORT).show()
        }

        btnFlush.setOnClickListener {
            AnalyticsSDK.flush()
            Toast.makeText(this, "Flush triggered! Check webhook.site.", Toast.LENGTH_SHORT).show()
        }
    }
}
