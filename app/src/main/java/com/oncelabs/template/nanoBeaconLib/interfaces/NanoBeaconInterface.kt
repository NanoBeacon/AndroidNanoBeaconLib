package com.oncelabs.template.nanoBeaconLib.interfaces

import com.oncelabs.template.nanoBeaconLib.model.NanoBeaconData

interface NanoBeaconInterface {
    fun newBeaconData(beaconData: NanoBeaconData)
}