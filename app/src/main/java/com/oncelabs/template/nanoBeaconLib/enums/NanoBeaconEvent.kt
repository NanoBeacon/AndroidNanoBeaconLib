package com.oncelabs.template.nanoBeaconLib.enums

import com.oncelabs.template.nanoBeaconLib.model.NanoBeacon
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

sealed class NanoBeaconEvent{
    class DiscoveredRegisteredType(val discoverFlow: Flow<NanoBeacon>): NanoBeaconEvent()
    class BleState(val bleReadyFlow: Flow<BleState>): NanoBeaconEvent()
    class BeaconDidTimeout(val timeoutFlow: Flow<NanoBeacon>): NanoBeaconEvent()
}