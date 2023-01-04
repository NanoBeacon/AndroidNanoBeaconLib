package com.oncelabs.nanobeacon

import android.content.ComponentName
import android.content.ContentValues
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.oncelabs.nanobeacon.analyzer.gzipDecompress
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.manager.BeaconManager
import com.oncelabs.nanobeacon.manager.ConfigDataManager
import com.oncelabs.nanobeacon.manager.FilePickerManager
import com.oncelabs.nanobeacon.manager.settings.SettingsManager
import com.oncelabs.nanobeacon.permission.PermissionType
import com.oncelabs.nanobeacon.permission.RequestAllPermissions
import com.oncelabs.nanobeacon.screen.MainScreen
import com.oncelabs.nanobeacon.ui.theme.InplayTheme
import com.oncelabs.nanobeacon.ui.theme.topBarBackground
import com.oncelabs.nanobeaconlib.manager.NanoNotificationManager
import com.oncelabs.nanobeaconlib.manager.NanoNotificationService
import com.oncelabs.nanobeaconlib.manager.ServiceUnbinder
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
var nanoNotificationService : NanoNotificationService? = null


/**
 * This is a Single Activity application,
 * try and keep this file as clean as possible
 */
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity(), ServiceUnbinder {

  @Inject
    lateinit var configDataManager: ConfigDataManager

   @Inject
   lateinit var filePickerManager : FilePickerManager

   @Inject
   lateinit var beaconManager: BeaconManager

   private val TAG = MainActivity::class.simpleName


    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SettingsManager.init(this)
        filePickerManager.createActivity(this)


        SettingsManager.getSavedConfig()?.let { configString ->
            if(configString.isNotEmpty()) {
                try {
                    val parsedData = Klaxon().parse<ConfigData>(configString)
                    parsedData?.let {
                        configDataManager.setConfig(it)
                        beaconManager.refresh()
                    }
                } catch (e: KlaxonException) {
                    Log.d(ContentValues.TAG, "Not formatted Correctly")
                }
            }
        }
        NanoNotificationManager.appContext = this
        bindService()
        setContent {
            rememberSystemUiController().setSystemBarsColor(topBarBackground)
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
            SettingsManager.init(this)
            SettingsManager.getSavedConfig()?.let { configString ->
                if(configString.isNotEmpty()) {
                    try {
                        val parsedData = Klaxon().parse<ConfigData>(configString)
                        parsedData?.let {
                            configDataManager.setConfig(it)
                            beaconManager.refresh()
                        }
                    } catch (e: KlaxonException) {
                        Log.d(ContentValues.TAG, "Not formatted Correctly")
                    }
                }
            }
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

    override fun unbind() {
        unbindService()
    }

    private fun unbindService() {
        Log.d(TAG, "UNBIND SERVICE")
        unbindService(serviceConnection)
        nanoNotificationService = null
        finish()
    }

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
            print("ServiceConnection: connected to service.")
            // We've bound to MyService, cast the IBinder and get MyBinder instance
            val binder = iBinder as NanoNotificationService.NotificationBinder
            binder.service.setUnbinder(this@MainActivity)
            nanoNotificationService = binder.service
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(TAG, "Service Disconnected")
        }
    }

    private fun bindService() {
        Intent(this, NanoNotificationService::class.java).also { intent ->
            bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        }
    }



}

