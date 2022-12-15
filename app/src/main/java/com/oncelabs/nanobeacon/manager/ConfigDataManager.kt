package com.oncelabs.nanobeacon.manager

import androidx.compose.material.ExperimentalMaterialApi
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeaconlib.model.ParsedConfigData
import com.oncelabs.nanobeaconlib.model.NanoBeaconData
import kotlinx.coroutines.flow.StateFlow

@ExperimentalMaterialApi
interface ConfigDataManager {
    val savedConfig : StateFlow<ConfigData?>
    val parsedConfig : StateFlow<ParsedConfigData?>
    fun init()
    fun setConfig(configData: ConfigData)
    fun processDeviceData(data : NanoBeaconData)
}