package com.oncelabs.nanobeaconlib.interfaces

import android.content.Context
import com.oncelabs.nanobeaconlib.model.NanoBeacon
import com.oncelabs.nanobeaconlib.model.NanoBeaconData

interface CustomBeaconInterface {
    fun isTypeMatchFor(beaconData: NanoBeaconData, context: Context, delegate: NanoBeaconDelegate): NanoBeacon?
}