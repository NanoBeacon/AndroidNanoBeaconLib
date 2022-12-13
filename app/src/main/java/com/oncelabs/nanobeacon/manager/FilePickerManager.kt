package com.oncelabs.nanobeacon.manager

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.MutableLiveData
import com.beust.klaxon.Klaxon
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.device.ADXL367
import com.oncelabs.nanobeaconlib.enums.BleState
import com.oncelabs.nanobeaconlib.enums.ScanState
import com.oncelabs.nanobeaconlib.model.NanoBeacon
import com.oncelabs.nanobeaconlib.model.NanoBeaconData
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

interface FilePickerManager {
    fun openFilePicker()
    fun createActivity(act : Activity)
    fun onResultFromActivity(requestCode: Int, resultCode: Int, data: Intent?)
}