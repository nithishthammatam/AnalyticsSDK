package com.shopzen.analytics.tracker

import com.shopzen.analytics.core.AnalyticsSDK

/**
 * Helper object providing strongly-typed methods for standard E-commerce events.
 * This ensures consistency in event naming and required properties across the app.
 */
object EcommerceTracker {

    fun trackProductView(productId: String, name: String, category: String, price: Double, currency: String = "USD") {
        val props = mapOf(
            "product_id" to productId,
            "product_name" to name,
            "category" to category,
            "price" to price,
            "currency" to currency
        )
        AnalyticsSDK.track("product_viewed", props)
    }

    fun trackAddToCart(productId: String, name: String, qty: Int, price: Double, cartTotal: Double, currency: String = "USD") {
        val props = mapOf(
            "product_id" to productId,
            "product_name" to name,
            "quantity" to qty,
            "price" to price,
            "cart_total" to cartTotal,
            "currency" to currency
        )
        AnalyticsSDK.track("cart_item_added", props)
    }

    fun trackCheckoutStarted(cartTotal: Double, itemCount: Int, currency: String = "USD") {
        val props = mapOf(
            "cart_total" to cartTotal,
            "item_count" to itemCount,
            "currency" to currency
        )
        AnalyticsSDK.track("checkout_started", props)
    }

    fun trackPaymentAttempted(amount: Double, currency: String, method: String, gateway: String) {
        val props = mapOf(
            "amount" to amount,
            "currency" to currency,
            "method" to method,
            "gateway" to gateway
        )
        AnalyticsSDK.track("payment_attempted", props)
    }

    fun trackPaymentSuccess(orderId: String, amount: Double, currency: String, method: String) {
        val props = mapOf(
            "order_id" to orderId,
            "amount" to amount,
            "currency" to currency,
            "method" to method
        )
        AnalyticsSDK.track("payment_success", props)
    }

    fun trackPaymentFailed(errorCode: String, errorMessage: String, method: String) {
        val props = mapOf(
            "error_code" to errorCode,
            "error_message" to errorMessage,
            "method" to method
        )
        AnalyticsSDK.track("payment_failed", props)
    }

    fun trackOrderCompleted(orderId: String, total: Double, itemCount: Int, currency: String, method: String) {
        val props = mapOf(
            "order_id" to orderId,
            "total_value" to total,
            "item_count" to itemCount,
            "currency" to currency,
            "method" to method
        )
        AnalyticsSDK.track("order_completed", props)
    }

    fun trackFunnelStep(funnelName: String, stepName: String, stepIndex: Int) {
        val props = mapOf(
            "funnel_name" to funnelName,
            "step_name" to stepName,
            "step_index" to stepIndex
        )
        AnalyticsSDK.track("checkout_funnel_step", props)
    }
}
