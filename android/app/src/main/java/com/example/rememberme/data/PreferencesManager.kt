package com.example.rememberme.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("rememberme_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_API_URL = "api_url"
        private const val DEFAULT_API_URL = "https://miraiwininghacathonproject-production.up.railway.app"
    }

    var apiUrl: String
        get() = prefs.getString(KEY_API_URL, DEFAULT_API_URL) ?: DEFAULT_API_URL
        set(value) {
            prefs.edit().putString(KEY_API_URL, value).apply()
        }
}
