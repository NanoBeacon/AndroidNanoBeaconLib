package com.oncelabs.template.nanoBeaconLib.model

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.oncelabs.template.nanoBeaconLib.interfaces.NanoBeaconDelegate

open class NanoBeacon(
    var bluetoothDevice: BluetoothDevice,
    context: Context,
    delegate: NanoBeaconDelegate,
    private var timeoutInterval: Float = 60f
): NanoBeaconInterface {


}