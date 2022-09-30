package com.oncelabs.nanobeacon.nanoBeaconLib.model

import android.bluetooth.le.ScanResult
import android.os.Build
import android.util.SparseArray
import androidx.core.util.forEach

data class NanoBeaconData(
    val scanResult: ScanResult
){
    var bluetoothDevice = scanResult.device
    var bluetoothAddress = scanResult.device.address
    var primaryPhy = scanResult.primaryPhy
    var secondaryPhy = scanResult.secondaryPhy
    var advInterval = scanResult.periodicAdvertisingInterval
    var connectable = scanResult.isConnectable
    var manufacturerData = parseManufacturerData(scanResult.scanRecord?.manufacturerSpecificData).first
    var manufacturerId = parseManufacturerData(scanResult.scanRecord?.manufacturerSpecificData).second
    var timeStamp = scanResult.timestampNanos
    var name = scanResult.scanRecord?.deviceName
    var flags = scanResult.scanRecord?.advertiseFlags
    var serviceUuids = scanResult.scanRecord?.serviceUuids
    var serviceData = scanResult.scanRecord?.serviceData
    var transmitPowerObserved = scanResult.txPower
    var txPowerClaimed = scanResult.scanRecord?.txPowerLevel
    var rssi = scanResult.rssi
    var serviceSolicitationUuids = if (Build.VERSION.SDK_INT >= 29) scanResult.scanRecord?.serviceSolicitationUuids else null

    private fun parseManufacturerData(data: SparseArray<ByteArray>?): Pair<ByteArray, Int> {
        var byteArray = byteArrayOf()
        var manufacturerId = 0
        data?.forEach { key, value ->
            manufacturerId = key
            value.forEach {
                byteArray += it
            }
        }
        return Pair(byteArray, manufacturerId)
    }
}
