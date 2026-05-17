# ShopZen Analytics SDK Integration Guide

This package contains the pre-built Android Analytics SDK for the ShopZen Flutter app.

## Contents
* `shopzen-analytics-sdk-v1.0.0.aar`: For production builds.
* `shopzen-analytics-sdk-v1.0.0-debug.aar`: For development and debugging.

## System Requirements
* **Min SDK:** API 21 (Android 5.0)
* **Dependencies:** Room, Retrofit, OkHttp, WorkManager, and Coroutines are automatically bundled in the AAR. You do **NOT** need to add them separately in your app-level `build.gradle`.

## MethodChannel Details
You should use the following MethodChannel to communicate with the SDK:
* **Channel Name:** `com.shopzen/analytics`

### Supported Methods:
1. `init`: Initialize SDK with config.
2. `track`: Track an event with properties.
3. `identify`: Identify a user.
4. `trackScreen`: Track a screen view.
5. `optOut`: Opt-out of analytics.
6. `optIn`: Opt-in to analytics.
7. `reset`: Clear user details and start a new session.
8. `flush`: Force an immediate upload of pending events.
9. *E-commerce Tracking:* (Use `track` method mapped to the e-commerce events).

### SDK Configuration Parameters
When calling `init` over the MethodChannel, pass a Map containing:
* `endpointUrl` (String): The webhook/backend URL.
* `apiKey` (String, Optional)
* `batchSize` (Int, default 50)
* `flushIntervalSeconds` (Int, default 30)
* `debugMode` (Boolean, default true for dev)
* `enabled` (Boolean, default true)

---
*Note: Make sure to map all your Flutter e-commerce actions to the 8 required events (product_viewed, cart_item_added, checkout_started, payment_attempted, payment_success, payment_failed, order_completed, checkout_funnel_step).*
