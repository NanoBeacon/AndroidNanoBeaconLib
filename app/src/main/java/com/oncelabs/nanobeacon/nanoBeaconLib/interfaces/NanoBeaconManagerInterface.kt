package com.oncelabs.nanobeacon.nanoBeaconLib.interfaces

interface NanoBeaconManagerInterface {
    fun startScanning()
    fun stopScanning()
    fun register(customBeacon: CustomBeaconInterface)
}