package com.oncelabs.template

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.oncelabs.template.nanoBeaconLib.manager.NanoBeaconManager
import com.oncelabs.template.permission.PermissionType
import com.oncelabs.template.permission.RequestAllPermissions
import com.oncelabs.template.screen.MainScreenView
import com.oncelabs.template.ui.theme.TemplateTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * This is a Single Activity application,
 * try and keep this file as clean as possible
 */
var beaconManager: NanoBeaconManager? = null
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beaconManager = NanoBeaconManager(this)
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

