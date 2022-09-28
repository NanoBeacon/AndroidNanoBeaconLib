package com.oncelabs.template.device

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.oncelabs.template.nanoBeaconLib.interfaces.NanoBeaconDelegate
import com.oncelabs.template.nanoBeaconLib.interfaces.CustomBeaconInterface
import com.oncelabs.template.nanoBeaconLib.model.NanoBeacon
import com.oncelabs.template.nanoBeaconLib.model.NanoBeaconData

class ADXL367(
    data: NanoBeaconData? = null,
    context: Context? = null,
    delegate: NanoBeaconDelegate? = null
):NanoBeacon(beaconData = data, context = context, delegate = delegate),
    CustomBeaconInterface {
    val TAG = ADXL367::class.simpleName

    override fun isTypeMatchFor(beaconData: NanoBeaconData): Boolean {
        Log.d(TAG, "Scanned Name: ${beaconData.name}")
        if (beaconData.name == "ADXL367_Temp"){
            return true
        }
        return false
    }
}