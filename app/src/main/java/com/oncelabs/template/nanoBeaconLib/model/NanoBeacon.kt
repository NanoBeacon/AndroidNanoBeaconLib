package com.oncelabs.template.nanoBeaconLib.model

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.oncelabs.template.nanoBeaconLib.interfaces.NanoBeaconDelegate
import com.oncelabs.template.nanoBeaconLib.interfaces.NanoBeaconInterface
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

open class NanoBeacon(
    var beaconData: NanoBeaconData? = null,
    context: Context? = null,
    delegate: NanoBeaconDelegate? = null,
    private var timeoutInterval: Float = 60f
): NanoBeaconInterface {

    private val _beaconDataFlow = MutableStateFlow<NanoBeaconData>()
    val beaconDataFlow = _beaconDataFlow.asSharedFlow()

    override fun newBeaconData(beaconData: NanoBeaconData) {
        _beaconDataFlow.emit(beaconData)
    }

}