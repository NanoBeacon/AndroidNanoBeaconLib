package com.oncelabs.nanobeacon

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.oncelabs.nanobeacon.manager.BeaconManager
import com.oncelabs.nanobeacon.permission.PermissionType
import com.oncelabs.nanobeacon.permission.RequestAllPermissions
import com.oncelabs.nanobeacon.screen.MainScreenView
import com.oncelabs.nanobeacon.ui.theme.TemplateTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * This is a Single Activity application,
 * try and keep this file as clean as possible
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BeaconManager.init(this)
        setContent {
            TemplateTheme {
//                Navigation()
                MainScreenView()
                /**TODO: Request needed permissions*/
                RequestAllPermissions(
                    navigateToSettingsScreen = {
                        PermissionType.navigateToSettings(context = this)
                    },
                    onAllGranted = {
                        //MainScreenView()
                        /*TODO: */
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(allPermissionsGranted()) {
            /**TODO: */
        }
    }

    /**
     * Check if all permissions are granted
     * @return if all permissions are granted
     */
    private fun allPermissionsGranted(): Boolean {
        val permissions = mutableListOf<String>()
        for (permission in PermissionType.values()) {
            if (permission.minimumVersion == null || Build.VERSION.SDK_INT >= permission.minimumVersion) {
                permissions.add(permission.id)
            }
        }
        return permissions.all {
            ActivityCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}

