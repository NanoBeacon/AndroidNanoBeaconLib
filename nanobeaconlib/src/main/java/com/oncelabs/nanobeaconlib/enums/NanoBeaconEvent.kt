package com.oncelabs.nanobeaconlib.enums

import com.oncelabs.nanobeaconlib.model.NanoBeacon
import com.oncelabs.nanobeaconlib.model.NanoBeaconData
import kotlinx.coroutines.flow.MutableSharedFlow

sealed class NanoBeaconEvent{
    class DiscoveredRegisteredType(val flow: MutableSharedFlow<NanoBeacon?>): NanoBeaconEvent()
    class BleStateChange(val flow: MutableSharedFlow<BleState?>): NanoBeaconEvent()
    class BeaconDidTimeout(val flow: MutableSharedFlow<NanoBeacon?>): NanoBeaconEvent()
    class NewBeaconData(val flow: MutableSharedFlow<NanoBeaconData>): NanoBeaconEvent()
}

