package com.oncelabs.nanobeacon.manager.settings

import android.app.Application
import android.content.Context

class PreferencesManager(application: Application) {
    private val TAG = "AdminPreferences"
    private val prefs = application.getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
    private val editor = prefs.edit()
    private val savedConfig = "SAVED_CONFIG"

    fun setSavedConfig(state: String) {
        editor.putString(savedConfig, state)
        editor.apply()
    }
    fun getSavedConfig(): String? {
        return prefs.getString(savedConfig, "")
    }
}

