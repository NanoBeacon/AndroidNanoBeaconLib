package com.oncelabs.nanobeacon.manager.settings

import android.app.Application
import android.content.Context

object SettingsManager {
    private lateinit var prefManager: PreferencesManager

    fun init(context: Context) {
        prefManager = PreferencesManager(application = context.applicationContext as Application)
    }

    fun setSavedConfig(state : String) {
        prefManager.setSavedConfig(state)
    }

    fun getSavedConfig() : String?{
        return prefManager.getSavedConfig()
    }
}