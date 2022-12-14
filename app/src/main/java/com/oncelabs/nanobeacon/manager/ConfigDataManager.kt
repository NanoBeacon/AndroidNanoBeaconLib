package com.oncelabs.nanobeacon.manager

import androidx.compose.material.ExperimentalMaterialApi
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.device.ADXL367
import com.oncelabs.nanobeacon.model.ParsedConfigData
import com.oncelabs.nanobeaconlib.enums.BleState
import com.oncelabs.nanobeaconlib.enums.ScanState
import com.oncelabs.nanobeaconlib.model.NanoBeacon
import com.oncelabs.nanobeaconlib.model.NanoBeaconData
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

@ExperimentalMaterialApi
interface ConfigDataManager {
    val savedConfigs : StateFlow<List<ConfigData>>
    val parsedConfigs : StateFlow<List<ParsedConfigData>>
    fun init()
    fun addConfigToList(configData: ConfigData)
}