package com.example.analytics.core

import android.content.Context

data class SDKConfig(
    val endpoint: String,
    val batchSize: Int = 50,
    val flushIntervalSeconds: Long = 30,
    val debug: Boolean = false,
    val enabled: Boolean = true
) {
    class Builder(private val endpoint: String) {
        private var batchSize = 50
        private var flushIntervalSeconds = 30L
        private var debug = false
        private var enabled = true

        fun setBatchSize(size: Int) = apply { this.batchSize = size }
        fun setFlushIntervalSeconds(seconds: Long) = apply { this.flushIntervalSeconds = seconds }
        fun setDebug(debug: Boolean) = apply { this.debug = debug }
        fun setEnabled(enabled: Boolean) = apply { this.enabled = enabled }

        fun build(): SDKConfig {
            return SDKConfig(endpoint, batchSize, flushIntervalSeconds, debug, enabled)
        }
    }
}
