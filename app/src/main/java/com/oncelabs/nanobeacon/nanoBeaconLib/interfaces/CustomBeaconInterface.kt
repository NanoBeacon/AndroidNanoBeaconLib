package com.oncelabs.nanobeacon.nanoBeaconLib.interfaces

import android.content.Context
import com.oncelabs.nanobeacon.nanoBeaconLib.model.NanoBeacon
import com.oncelabs.nanobeacon.nanoBeaconLib.model.NanoBeaconData

interface CustomBeaconInterface {
    fun isTypeMatchFor(beaconData: NanoBeaconData, context: Context, delegate: NanoBeaconDelegate): NanoBeacon?
}