package com.oncelabs.template.device

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.oncelabs.template.nanoBeaconLib.interfaces.NanoBeaconDelegate
import com.oncelabs.template.nanoBeaconLib.model.CustomBeaconInterface
import com.oncelabs.template.nanoBeaconLib.model.NanoBeacon
import com.oncelabs.template.nanoBeaconLib.model.NanoBeaconData

class ADXL367(
    device: BluetoothDevice,
    context: Context,
    delegate: NanoBeaconDelegate
):NanoBeacon(bluetoothDevice = device, context = context, delegate = delegate), CustomBeaconInterface {

    override fun isTypeMatchFor(beaconData: NanoBeaconData): NanoBeacon {
        TODO("Not yet implemented")
    }
}