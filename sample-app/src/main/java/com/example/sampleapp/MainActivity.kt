package com.example.sampleapp

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.analytics.core.AnalyticsSDK
import com.example.analytics.core.SDKConfig
import com.example.analytics.tracker.EcommerceTracker
import com.example.analytics.tracker.trackClick

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Create a dashboard UI dynamically for testing
        val layout = LinearLayout(this).apply { 
            orientation = LinearLayout.VERTICAL 
            setPadding(50, 50, 50, 50)
        }
        
        val btnAddToCart = Button(this).apply { text = "1. Simulate Full Checkout Funnel (Task 6)" }
        val btnOptOut = Button(this).apply { text = "2. GDPR Opt-Out (Task 7)" }
        val btnOptIn = Button(this).apply { text = "3. GDPR Opt-In (Task 7)" }
        val btnTrackEvent = Button(this).apply { text = "4. Track Generic Event" }
        
        layout.addView(btnAddToCart)
        layout.addView(btnOptOut)
        layout.addView(btnOptIn)
        layout.addView(btnTrackEvent)
        setContentView(layout)

        // 2. Initialize SDK (Task 7: config.debug = true enables pretty JSON)
        val config = SDKConfig.Builder("https://api.yourbackend.com/events")
            .setDebug(true)
            .setBatchSize(5)
            .build()
            
        AnalyticsSDK.init(applicationContext, config)
        AnalyticsSDK.trackScreen("Home_Screen") 
        
        // --- BUTTON 1: Tasks 5 & 6 (AutoClick & E-commerce) ---
        btnAddToCart.trackClick("test_button_clicked") {
            // 1. User views a product
            EcommerceTracker.trackProductView("SHOE_123", "Nike Air Max", 120.00)
            // 2. User adds 2 of them to the cart
            EcommerceTracker.trackAddToCart("SHOE_123", "Nike Air Max", 120.00, 2)
            // 3. User begins checkout
            EcommerceTracker.trackCheckoutStarted(240.00, 2)
            // 4. User attempts to pay
            EcommerceTracker.trackPaymentAttempted(240.00, "Credit Card")
            // 5. Order is successful
            EcommerceTracker.trackOrderCompleted("ORDER_999", 240.00)
            // (Or if the order failed)
            EcommerceTracker.trackOrderFailed("ORDER_999", "Insufficient Funds")
            // 6. Explicit funnel tracking
            EcommerceTracker.trackFunnelStep("Checkout_Flow", "Shipping_Details", 2)
            
            Toast.makeText(this, "Full Funnel tracked! Check Logcat.", Toast.LENGTH_LONG).show()
        }

        // --- BUTTON 2: Task 7 (GDPR Consent Opt-Out) ---
        btnOptOut.setOnClickListener {
            AnalyticsSDK.optOut()
            Toast.makeText(this, "Opted OUT! Events are now disabled.", Toast.LENGTH_SHORT).show()
        }

        // --- BUTTON 3: Task 7 (GDPR Consent Opt-In) ---
        btnOptIn.setOnClickListener {
            AnalyticsSDK.optIn()
            Toast.makeText(this, "Opted IN! Events are enabled again.", Toast.LENGTH_SHORT).show()
        }

        // --- BUTTON 4: Task 7 (Verify Consent State & Debug Logger) ---
        btnTrackEvent.setOnClickListener {
            AnalyticsSDK.track("generic_test_event", mapOf("test_param" to "test_value"))
            Toast.makeText(this, "Fired event. Check Logcat!", Toast.LENGTH_SHORT).show()
        }
    }
}
