package com.oncelabs.template.nanoBeaconLib.interfaces

import com.oncelabs.template.nanoBeaconLib.model.NanoBeacon
import com.oncelabs.template.nanoBeaconLib.model.NanoBeaconData

interface CustomBeaconInterface {
    fun isTypeMatchFor(beaconData: NanoBeaconData): Boolean
}