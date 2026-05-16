package com.example.analytics.tracker

import com.example.analytics.core.AnalyticsSDK

/**
 * Helper object providing strongly-typed methods for standard E-commerce events.
 * This ensures consistency in event naming and required properties across the app.
 */
object EcommerceTracker {

    fun trackProductView(productId: String, name: String, price: Double, currency: String = "USD") {
        val props = mapOf(
            "product_id" to productId,
            "product_name" to name,
            "price" to price,
            "currency" to currency
        )
        AnalyticsSDK.track("product_view", props)
    }

    fun trackAddToCart(productId: String, name: String, price: Double, quantity: Int = 1, currency: String = "USD") {
        val props = mapOf(
            "product_id" to productId,
            "product_name" to name,
            "price" to price,
            "quantity" to quantity,
            "currency" to currency,
            "total_value" to (price * quantity)
        )
        AnalyticsSDK.track("add_to_cart", props)
    }

    fun trackCheckoutStarted(cartValue: Double, itemCount: Int, currency: String = "USD") {
        val props = mapOf(
            "cart_value" to cartValue,
            "item_count" to itemCount,
            "currency" to currency
        )
        AnalyticsSDK.track("checkout_started", props)
    }

    fun trackPaymentAttempted(cartValue: Double, paymentMethod: String, currency: String = "USD") {
        val props = mapOf(
            "cart_value" to cartValue,
            "payment_method" to paymentMethod,
            "currency" to currency
        )
        AnalyticsSDK.track("payment_attempted", props)
    }

    fun trackOrderCompleted(orderId: String, totalValue: Double, currency: String = "USD") {
        val props = mapOf(
            "order_id" to orderId,
            "total_value" to totalValue,
            "currency" to currency
        )
        AnalyticsSDK.track("order_completed", props)
    }

    fun trackOrderFailed(orderId: String, reason: String) {
        val props = mapOf(
            "order_id" to orderId,
            "failure_reason" to reason
        )
        AnalyticsSDK.track("order_failed", props)
    }

    fun trackFunnelStep(funnelName: String, stepName: String, stepIndex: Int) {
        val props = mapOf(
            "funnel_name" to funnelName,
            "step_name" to stepName,
            "step_index" to stepIndex
        )
        AnalyticsSDK.track("funnel_step", props)
    }
}
