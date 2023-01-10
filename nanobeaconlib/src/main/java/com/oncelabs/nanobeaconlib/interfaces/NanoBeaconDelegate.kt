package com.oncelabs.nanobeaconlib.interfaces

import com.oncelabs.nanobeaconlib.model.NanoBeacon

interface NanoBeaconDelegate {
    fun nanoBeaconDidTimeOut(nanoBeacon : NanoBeacon)

}