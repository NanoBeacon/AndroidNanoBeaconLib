package com.oncelabs.nanobeacon.interfaces

interface BeaconManagerInterface {
    fun startScanning()
    fun stopScanning()
    fun refresh()
}