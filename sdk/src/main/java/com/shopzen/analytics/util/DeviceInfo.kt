package com.shopzen.analytics.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.util.Locale

object DeviceInfo {
    fun get(context: Context): Map<String, String> {
        val packageInfo = try {
            context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: Exception) {
            null
        }
        
        return mapOf(
            "os" to "Android ${Build.VERSION.RELEASE}",
            "model" to "${Build.MANUFACTURER} ${Build.MODEL}",
            "app_version" to (packageInfo?.versionName ?: "unknown"),
            "sdk_version" to "1.0.0",
            "locale" to Locale.getDefault().toLanguageTag(),
            "network" to getNetworkType(context)
        )
    }

    private fun getNetworkType(context: Context): String {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return "unknown"
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork) ?: return "none"
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "cellular"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ethernet"
            else -> "unknown"
        }
    }
}
