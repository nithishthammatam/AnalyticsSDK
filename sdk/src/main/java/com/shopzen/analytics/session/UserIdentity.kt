package com.shopzen.analytics.session

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

class UserIdentity(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("analytics_user_prefs", Context.MODE_PRIVATE)

    var anonymousId: String
        private set

    var userId: String? = null
        private set

    init {
        anonymousId = prefs.getString("anon_id", null) ?: run {
            val newId = "anon_" + UUID.randomUUID().toString()
            prefs.edit().putString("anon_id", newId).apply()
            newId
        }
        userId = prefs.getString("user_id", null)
    }

    fun identify(newUserId: String) {
        this.userId = newUserId
        prefs.edit().putString("user_id", newUserId).apply()
    }

    fun clear() {
        userId = null
        val newAnon = "anon_" + UUID.randomUUID().toString()
        anonymousId = newAnon
        prefs.edit()
            .remove("user_id")
            .putString("anon_id", newAnon)
            .apply()
    }
}
