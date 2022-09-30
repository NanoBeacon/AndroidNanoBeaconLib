package com.oncelabs.template.manager

import android.content.Context
import com.oncelabs.template.device.ADXL367
import com.oncelabs.template.interfaces.BeaconManagerInterface
import com.oncelabs.template.nanoBeaconLib.enums.NanoBeaconEvent
import com.oncelabs.template.nanoBeaconLib.manager.NanoBeaconManager
import com.oncelabs.template.nanoBeaconLib.model.NanoBeacon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

object BeaconManager: BeaconManagerInterface {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _discoveredAdxlBeacons = MutableStateFlow<List<ADXL367>>(listOf())
    val discoveredAdxlBeacons = _discoveredAdxlBeacons.asStateFlow()

    private val discoveredRegisteredTypeFlow = MutableSharedFlow<NanoBeacon?>()

    fun init(context: Context) {
        NanoBeaconManager.init {
            context
        }
        NanoBeaconManager.register(ADXL367())
        addObservers()
    }

    override fun startScanning() {
        NanoBeaconManager.startScanning()
    }

    override fun stopScanning() {
        NanoBeaconManager.stopScanning()
    }

    private fun addObservers(){
        NanoBeaconManager.on(NanoBeaconEvent.DiscoveredRegisteredType(flow = discoveredRegisteredTypeFlow))

        scope.launch {
            discoveredRegisteredTypeFlow.collect{
                it?.let { nanoBeacon ->
                    if (nanoBeacon is ADXL367){
                        if (!_discoveredAdxlBeacons.value.contains(nanoBeacon)){
                            _discoveredAdxlBeacons.value += listOf(nanoBeacon)
                        }
                    }
                }
            }
        }
    }
}