package com.oncelabs.nanobeacon.manager

import android.content.Context
import com.oncelabs.nanobeacon.device.ADXL367
import com.oncelabs.nanobeaconlib.enums.BleState
import com.oncelabs.nanobeaconlib.enums.NanoBeaconEvent
import com.oncelabs.nanobeaconlib.manager.NanoBeaconManager
import com.oncelabs.nanobeaconlib.model.NanoBeacon
import com.oncelabs.nanobeaconlib.model.NanoBeaconData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BeaconManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
): BeaconManager {

    private val scope = CoroutineScope(Dispatchers.IO)

    override val scanningEnabled = NanoBeaconManager.scanState

    // Registered beacon instance flow
    private val _discoveredAdxlBeacons = MutableStateFlow<List<ADXL367>>(listOf())
    override val discoveredAdxlBeacons = _discoveredAdxlBeacons.asStateFlow()

    // Beacon instance flow
    private val _discoveredBeacons = MutableStateFlow<List<NanoBeacon>>(listOf())
    override val discoveredBeacons = _discoveredBeacons.asStateFlow()

    // Raw beacon data flow
    private val _newBeaconDataFlow = MutableSharedFlow<NanoBeaconData>()
    override val newBeaconDataFlow = _newBeaconDataFlow.asSharedFlow()

    private val _bleStateChange = MutableSharedFlow<BleState?>()
    override val bleStateChange = _bleStateChange.asSharedFlow()

    private val discoveredRegisteredTypeFlow = MutableSharedFlow<NanoBeacon?>()
    private val discoveredBeaconFlow = MutableSharedFlow<NanoBeacon>()
    private val beaconTimeoutFlow = MutableSharedFlow<NanoBeacon>()

    init {
        NanoBeaconManager.init(getContext = (WeakReference(context)))
        NanoBeaconManager.register(ADXL367())
        addObservers()
    }

    override fun startScanning() {
        NanoBeaconManager.requestBluetoothEnable()
        NanoBeaconManager.startScanning()
    }

    override fun stopScanning() {
        NanoBeaconManager.stopScanning()
    }

    override fun refresh() {
        NanoBeaconManager.refresh()
        _discoveredBeacons.value = listOf()
        _discoveredAdxlBeacons.value = listOf()
        startScanning()
    }

    private fun addObservers(){
        NanoBeaconManager.on(NanoBeaconEvent.DiscoveredRegisteredType(flow = discoveredRegisteredTypeFlow))
        NanoBeaconManager.on(NanoBeaconEvent.NewBeaconData(flow = _newBeaconDataFlow))
        NanoBeaconManager.on(NanoBeaconEvent.BleStateChange(flow = _bleStateChange))
        NanoBeaconManager.on(NanoBeaconEvent.NewBeacon(flow = discoveredBeaconFlow))
        NanoBeaconManager.on(NanoBeaconEvent.BeaconDidTimeout(flow = beaconTimeoutFlow))

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

        scope.launch {
            discoveredBeaconFlow.collect{ beacon ->
                if (!_discoveredBeacons.value.contains(beacon)){
                    _discoveredBeacons.value += listOf(beacon)
                }
            }
        }

        scope.launch {
            beaconTimeoutFlow.collect {

            }
        }
    }
}