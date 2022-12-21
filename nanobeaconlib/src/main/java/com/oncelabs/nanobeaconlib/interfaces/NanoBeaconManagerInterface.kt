package com.oncelabs.nanobeaconlib.interfaces

interface NanoBeaconManagerInterface {
    fun startScanning()
    fun stopScanning()
    fun register(customBeacon: CustomBeaconInterface)
}