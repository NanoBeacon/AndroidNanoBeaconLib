package com.oncelabs.template.nanoBeaconLib.model

interface CustomBeaconInterface {
    fun isTypeMatchFor(beaconData: NanoBeaconData): NanoBeacon
}