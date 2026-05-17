# Android Analytics SDK (v1.0.0)

A robust, modular, offline-first Analytics SDK for Android. It supports automatic session management, UI interaction tracking, e-commerce funnels, GDPR compliance, and offline batch queueing via Room and WorkManager.

## Features
*   **Offline Persistence:** Uses Room Database to queue events when the device is offline.
*   **Background Sync:** Uses WorkManager to batch upload events with exponential backoff retries.
*   **Auto Tracking:** Automatically tracks Screen Views and Session Start/End.
*   **E-commerce Module:** Strongly-typed helper for standard retail funnels.
*   **Privacy First:** Built-in `optOut()` and `optIn()` controls for GDPR.

---

## 1. Setup & Installation

The SDK is packaged as a `.aar` via Maven Local. 
In your main app's `build.gradle.kts`, add:
```kotlin
repositories {
    mavenLocal()
}
dependencies {
    implementation("com.shopzen.analytics:analytics-sdk:1.0.0")
}
```

### Initialization
Initialize the SDK in your `Application` class:
```kotlin
val config = SDKConfig.Builder("https://api.yourbackend.com/events")
    .setDebug(true)           // Pretty JSON in Logcat
    .setBatchSize(10)         // Upload every 10 events
    .setFlushIntervalSeconds(900) // Or every 15 minutes
    .build()

AnalyticsSDK.init(applicationContext, config)
```

---

## 2. Public API Reference

### Core Tracking
```kotlin
// Track a custom event
AnalyticsSDK.track("video_played", mapOf("video_id" to "V123", "length" to 120))

// Identify a logged-in user
AnalyticsSDK.identify("USER_888", mapOf("plan" to "premium"))
```

### Privacy (GDPR)
```kotlin
AnalyticsSDK.optOut() // Disables all tracking immediately
AnalyticsSDK.optIn()  // Re-enables tracking
```

### E-Commerce Tracking (`EcommerceTracker`)
Strongly-typed methods that automatically format the payload.
```kotlin
// 1. View Product
EcommerceTracker.trackProductView("SHOE_123", "Nike Air Max", 120.00)

// 2. Add to Cart
EcommerceTracker.trackAddToCart("SHOE_123", "Nike Air Max", 120.00, quantity = 2)

// 3. Checkout
EcommerceTracker.trackCheckoutStarted(cartValue = 240.00, itemCount = 2)

// 4. Purchase
EcommerceTracker.trackOrderCompleted("ORDER_999", totalValue = 240.00)
```

### UI & Screen Tracking
Screen views are tracked automatically. For buttons, use the Kotlin extension:
```kotlin
import com.shopzen.analytics.tracker.trackClick

myButton.trackClick("signup_button_clicked") {
    // Original click logic here
}
```

---

## 3. Architecture & Event Schema
Every event sent to your backend will follow this exact JSON schema:
```json
{
  "id": 1,
  "name": "add_to_cart",
  "timestamp": 1715852342000,
  "user_id": "USER_888",
  "anonymous_id": "anon-1234-uuid",
  "session_id": "sess-5678-uuid",
  "properties": {
    "product_id": "SHOE_123",
    "quantity": 2
  },
  "device_info": {
    "os_version": "Android 13",
    "model": "Pixel 7"
  }
}
```

## Developer Guide: Publishing
To build the `.aar` and publish to Maven Local:
```bash
./gradlew :sdk:publishReleasePublicationToMavenLocal
```
