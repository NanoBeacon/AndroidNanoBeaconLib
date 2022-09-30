package com.oncelabs.nanobeacon.nanoBeaconLib.enums

import com.oncelabs.nanobeacon.nanoBeaconLib.model.NanoBeacon
import kotlinx.coroutines.flow.MutableSharedFlow

sealed class NanoBeaconEvent{
    class DiscoveredRegisteredType(val flow: MutableSharedFlow<NanoBeacon?>): NanoBeaconEvent()
    class BleStateChange(val flow: MutableSharedFlow<BleState?>): NanoBeaconEvent()
    class BeaconDidTimeout(val flow: MutableSharedFlow<NanoBeacon?>): NanoBeaconEvent()
}

