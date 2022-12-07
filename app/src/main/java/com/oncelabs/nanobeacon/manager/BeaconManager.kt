package com.oncelabs.nanobeacon.manager

import androidx.compose.material.ExperimentalMaterialApi
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.device.ADXL367
import com.oncelabs.nanobeaconlib.enums.BleState
import com.oncelabs.nanobeaconlib.enums.ScanState
import com.oncelabs.nanobeaconlib.model.NanoBeacon
import com.oncelabs.nanobeaconlib.model.NanoBeaconData
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

@ExperimentalMaterialApi
interface BeaconManager {
    val discoveredAdxlBeacons: StateFlow<List<ADXL367>>
    val discoveredBeacons: StateFlow<List<NanoBeacon>>
    val newBeaconDataFlow: SharedFlow<NanoBeaconData>
    val bleStateChange: SharedFlow<BleState?>
    val scanningEnabled: StateFlow<ScanState>
    fun startScanning()
    fun stopScanning()
    fun refresh()
    fun init()
}