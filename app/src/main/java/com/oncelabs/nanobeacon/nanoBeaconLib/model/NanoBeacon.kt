package com.oncelabs.nanobeacon.nanoBeaconLib.model

import android.content.Context
import com.oncelabs.nanobeacon.nanoBeaconLib.interfaces.NanoBeaconDelegate
import com.oncelabs.nanobeacon.nanoBeaconLib.interfaces.NanoBeaconInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class NanoBeacon(
    var beaconData: NanoBeaconData? = null,
    context: Context? = null,
    delegate: NanoBeaconDelegate? = null,
    private var timeoutInterval: Float = 60f,
    override val address: String? = beaconData?.bluetoothAddress
): NanoBeaconInterface {

    private val TAG = NanoBeacon::class.simpleName

    /* Expose flow for observing real-time RSSI updates*/
    private val _rssiFlow = MutableStateFlow<Int?>(null)
    override val rssiFlow = _rssiFlow.asStateFlow()

    /* Expose flow for observing real-time advertisement data */
    private val _beaconDataFlow = MutableStateFlow<NanoBeaconData?>(null)
    override val beaconDataFlow = _beaconDataFlow.asStateFlow()

    override fun newBeaconData(beaconData: NanoBeaconData) {
        _beaconDataFlow.value = beaconData
    }
}
