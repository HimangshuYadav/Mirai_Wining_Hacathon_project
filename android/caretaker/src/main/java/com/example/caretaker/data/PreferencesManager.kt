package com.example.caretaker.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("caretaker_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_API_URL = "api_url"
        private const val DEFAULT_API_URL = "https://miraiwininghacathonproject-production.up.railway.app"
        private const val KEY_USERNAME = "username"
        private const val KEY_PHONE = "phone"
        private const val KEY_LOGGED_IN = "is_logged_in"
    }

    var apiUrl: String
        get() = prefs.getString(KEY_API_URL, DEFAULT_API_URL) ?: DEFAULT_API_URL
        set(value) {
            prefs.edit().putString(KEY_API_URL, value).apply()
        }

    var username: String
        get() = prefs.getString(KEY_USERNAME, "") ?: ""
        set(value) {
            prefs.edit().putString(KEY_USERNAME, value).apply()
        }

    var caregiverPhone: String
        get() = prefs.getString(KEY_PHONE, "") ?: ""
        set(value) {
            prefs.edit().putString(KEY_PHONE, value).apply()
        }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_LOGGED_IN, false)
        set(value) {
            prefs.edit().putBoolean(KEY_LOGGED_IN, value).apply()
        }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
