package com.oncelabs.nanobeaconlib.interfaces

interface NanoBeaconManagerInterface {
    fun startScanning()
    fun stopScanning()
    fun refresh()
    fun register(customBeacon: CustomBeaconInterface)
}