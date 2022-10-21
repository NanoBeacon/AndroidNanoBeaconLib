package com.oncelabs.nanobeaconlib.interfaces

import com.oncelabs.nanobeaconlib.model.NanoBeaconData
import kotlinx.coroutines.flow.StateFlow

interface NanoBeaconInterface {
    fun newBeaconData(beaconData: NanoBeaconData)
    val beaconDataFlow: StateFlow<NanoBeaconData?>
    val rssiFlow: StateFlow<Int?>
    val estimatedAdvIntervalFlow: StateFlow<Int?>
    val address:String?
}