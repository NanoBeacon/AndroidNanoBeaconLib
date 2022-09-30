package com.oncelabs.template.viewModel

import android.app.Application
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.*
import com.oncelabs.template.device.ADXL367
import com.oncelabs.template.manager.BeaconManager
import com.oncelabs.template.model.ADXL367Data
import com.oncelabs.template.nanoBeaconLib.manager.NanoBeaconManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject


@HiltViewModel
class LiveDataViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    private val _beacons = MutableLiveData<List<Pair<String,List<Pair<Long, ADXL367Data>>>>>(listOf())
    val beacons : LiveData<List<Pair<String,List<Pair<Long, ADXL367Data>>>>> = _beacons

    private val _activeBeacons = MutableLiveData<ConcurrentHashMap<String, ADXL367>>(ConcurrentHashMap())
    val activeBeacons : LiveData<ConcurrentHashMap<String, ADXL367>> = _activeBeacons

    private var index = 0

    init {
        BeaconManager.startScanning()

        viewModelScope.launch {
            BeaconManager.discoveredAdxlBeacons.collect {
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
