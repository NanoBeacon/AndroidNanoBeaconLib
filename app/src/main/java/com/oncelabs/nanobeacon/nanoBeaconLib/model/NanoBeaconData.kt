package com.oncelabs.nanobeacon.nanoBeaconLib.model

import android.bluetooth.le.ScanResult
import android.os.Build
import android.os.SystemClock
import android.util.SparseArray
import androidx.core.util.forEach
import java.text.SimpleDateFormat
import java.util.*

data class NanoBeaconData(
    val scanResult: ScanResult,
    private val estAdvInterval: Int,
){
    var bluetoothDevice = scanResult.device
    var bluetoothAddress = scanResult.device.address
    var primaryPhy = scanResult.primaryPhy
    var secondaryPhy = scanResult.secondaryPhy
    var advInterval = scanResult.periodicAdvertisingInterval
    var connectable = scanResult.isConnectable
    var manufacturerData = parseManufacturerData(scanResult.scanRecord?.manufacturerSpecificData).first
    var manufacturerId = parseManufacturerData(scanResult.scanRecord?.manufacturerSpecificData).second.toShort()
    var timeStamp = scanResult.timestampNanos
    val timeStampFormatted: String
        get() {
            val timeStampMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime() + scanResult.timestampNanos / 1000000
            val rxDate = Date(timeStampMillis)
            return SimpleDateFormat("HH:mm:ss.sss", Locale.US).format(rxDate)
        }
    var name = scanResult.scanRecord?.deviceName
    var flags = scanResult.scanRecord?.advertiseFlags
    var serviceUuids = scanResult.scanRecord?.serviceUuids
    var serviceData = scanResult.scanRecord?.serviceData
    var transmitPowerObserved = scanResult.txPower
    var txPowerClaimed = scanResult.scanRecord?.txPowerLevel
    var rssi = scanResult.rssi
    var serviceSolicitationUuids = if (Build.VERSION.SDK_INT >= 29) scanResult.scanRecord?.serviceSolicitationUuids else null
    var estimatedAdvInterval = estAdvInterval

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
