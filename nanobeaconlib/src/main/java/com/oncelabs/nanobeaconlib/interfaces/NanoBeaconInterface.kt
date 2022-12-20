package com.oncelabs.nanobeaconlib.interfaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.oncelabs.nanobeaconlib.enums.DynamicDataType
import com.oncelabs.nanobeaconlib.model.NanoBeaconData
import com.oncelabs.nanobeaconlib.model.ParsedConfigData
import kotlinx.coroutines.flow.StateFlow

interface NanoBeaconInterface {
    fun newBeaconData(beaconData: NanoBeaconData)
    val beaconDataFlow: StateFlow<NanoBeaconData?>
    val rssiFlow: StateFlow<Int?>
    val estimatedAdvIntervalFlow: StateFlow<Int?>
    val matchingConfig : StateFlow<ParsedConfigData?>
    val address:String?
    val manufacturerData : StateFlow<Map<DynamicDataType, String>>
}