package com.oncelabs.nanobeaconlib.model

import android.bluetooth.le.ScanResult
import android.os.Build
import android.os.SystemClock
import android.util.SparseArray
import androidx.core.util.forEach
import com.oncelabs.nanobeaconlib.extension.toHexString
import com.oncelabs.nanobeaconlib.utils.CompanyString
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
    var manufacturerId =
        if (parseManufacturerData(scanResult.scanRecord?.manufacturerSpecificData).second != 0){
            "%04X".format(parseManufacturerData(scanResult.scanRecord?.manufacturerSpecificData).second.toShort())
        } else {
            "Not Set"
        }
    var company =
        if (parseManufacturerData(scanResult.scanRecord?.manufacturerSpecificData).second != 0){
            CompanyString(parseManufacturerData(scanResult.scanRecord?.manufacturerSpecificData).second)
        } else {
            "Not Set"
        }
    var timeStamp = scanResult.timestampNanos
    val timeStampFormatted: String
        get() {
            val timeStampMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime() + scanResult.timestampNanos / 1000000
            val rxDate = Date(timeStampMillis)
            return SimpleDateFormat("HH:mm:ss.sss", Locale.US).format(rxDate)
        }
    var name = scanResult.scanRecord?.deviceName ?: "Not Set"
    var flags = if (scanResult.scanRecord?.advertiseFlags == -1) "Not Set" else "%02X".format(scanResult.scanRecord?.advertiseFlags?.toByte())
    var serviceUuids = scanResult.scanRecord?.serviceUuids
    var serviceData = scanResult.scanRecord?.serviceData
    var transmitPowerObserved = if (scanResult.txPower == ScanResult.TX_POWER_NOT_PRESENT) "Not Set"  else scanResult.txPower

    var txPowerClaimed = if (scanResult.scanRecord?.txPowerLevel == Integer.MIN_VALUE) "Not Set" else scanResult.scanRecord?.txPowerLevel
    var rssi = scanResult.rssi
    var serviceSolicitationUuids = if (Build.VERSION.SDK_INT >= 29) scanResult.scanRecord?.serviceSolicitationUuids else null
    var estimatedAdvInterval = estAdvInterval
    val searchableString = "$this $manufacturerData $name $bluetoothAddress $company"
    val raw = scanResult.scanRecord?.bytes?.toHexString()

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
