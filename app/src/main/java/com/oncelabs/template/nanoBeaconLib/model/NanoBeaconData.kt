package com.oncelabs.template.nanoBeaconLib.model

import android.bluetooth.le.ScanResult

data class NanoBeaconData(
    val scanResult: ScanResult
){
    var bluetoothAddress = scanResult.device.address
}
