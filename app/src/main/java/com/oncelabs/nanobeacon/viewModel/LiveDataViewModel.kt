package com.oncelabs.nanobeacon.viewModel

import android.app.Application
import androidx.compose.material.ExperimentalMaterialApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oncelabs.nanobeacon.device.ADXL367
import com.oncelabs.nanobeacon.manager.BeaconManager
import com.oncelabs.nanobeacon.model.ADXL367Data
import com.oncelabs.nanobeaconlib.enums.BleState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.collections.set

@ExperimentalMaterialApi
@HiltViewModel
class LiveDataViewModel @Inject constructor(
    beaconManager: BeaconManager,
    application: Application
): AndroidViewModel(application) {

    private val _beacons = MutableLiveData<List<Pair<String,List<Pair<Long, ADXL367Data>>>>>(listOf())
    val beacons : LiveData<List<Pair<String,List<Pair<Long, ADXL367Data>>>>> = _beacons

    private val _activeBeacons = MutableLiveData<ConcurrentHashMap<String, ADXL367>>(ConcurrentHashMap())
    val activeBeacons : LiveData<ConcurrentHashMap<String, ADXL367>> = _activeBeacons

    private var index = 0

    init {
        beaconManager.startScanning()

        viewModelScope.launch {
            beaconManager.discoveredAdxlBeacons.collect {
                it.forEach { adxlBeacon ->
                    adxlBeacon.address?.let { address ->
                        _activeBeacons.value?.let { ab ->
                            if (!ab.containsKey(address)){
                                ab[address] = adxlBeacon
                                observeDataFor(adxlBeacon)
                            }
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            beaconManager.bleStateChange.collect { state ->
                state.takeIf { it != null } ?: return@collect
                if(state == BleState.AVAILABLE) {
                    delay(3000) // Why?
                    beaconManager.startScanning()
                }
            }
        }
    }

    private fun observeDataFor(adxL367: ADXL367){
        viewModelScope.launch {
            adxL367.historicalAdxlData.collect {
                adxL367.address?.let { address ->
                   _beacons.value = _activeBeacons.value?.map { Pair(it.key, it.value.historicalAdxlData.value) }
                }
            }
        }
    }
}
