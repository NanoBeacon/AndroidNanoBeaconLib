package com.oncelabs.nanobeacon

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.app.ActivityCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.oncelabs.nanobeacon.manager.BeaconManager
import com.oncelabs.nanobeacon.manager.FilePickerManager
import com.oncelabs.nanobeacon.permission.PermissionType
import com.oncelabs.nanobeacon.permission.RequestAllPermissions
import com.oncelabs.nanobeacon.screen.MainScreen
import com.oncelabs.nanobeacon.ui.theme.InplayTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * This is a Single Activity application,
 * try and keep this file as clean as possible
 */
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

   @Inject
   lateinit var filePickerManager : FilePickerManager

   @Inject
   lateinit var beaconManager: BeaconManager

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filePickerManager.createActivity(this)

        setContent {
            InplayTheme {
                /**TODO: Request needed permissions*/
                RequestAllPermissions(
                    navigateToSettingsScreen = {
                        PermissionType.navigateToSettings(context = this)
                    },
                    onAllGranted = {
                        beaconManager.init()
                        MainScreen()
                        //BeaconManagerImpl.init(this)
                        //MainScreenView()
                        /*TODO: */
                    }
                )

            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        filePickerManager?.onResultFromActivity(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


}

