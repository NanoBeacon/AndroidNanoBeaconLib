package com.oncelabs.nanobeacon.nanoBeaconLib.interfaces

import com.oncelabs.nanobeacon.nanoBeaconLib.model.NanoBeaconData
import kotlinx.coroutines.flow.StateFlow

interface NanoBeaconInterface {
    fun newBeaconData(beaconData: NanoBeaconData)
    val beaconDataFlow: StateFlow<NanoBeaconData?>
    val rssiFlow: StateFlow<Int?>
    val address:String?
}