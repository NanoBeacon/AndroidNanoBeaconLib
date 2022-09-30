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

data class BeaconChartData(
    var name: String,
    var data: ADXL367Data
)

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

//    private fun observeDataFor(adxL367: ADXL367){
//        viewModelScope.launch {
//            adxL367.adxlData.collect {
//                val deviceMap = _activeBeacons.value
//                it?.let { newBeaconData ->
//                    deviceMap?.let { ab ->
//                        ab[adxL367.address]?.let {
//                            if (it.currentIndex < 50){
//                                it.currentIndex += 1
//                                val test = it.x
//                                it.rssi.add(Pair(it.currentIndex.toFloat(), newBeaconData.rssi.toFloat()))
//                                it.temp.add(Pair(it.currentIndex.toFloat(), newBeaconData.temp))
//                                it.x.add(Pair(it.currentIndex.toFloat(), newBeaconData.xAccel))
//                                it.y.add(Pair(it.currentIndex.toFloat(), newBeaconData.yAccel))
//                                it.z.add(Pair(it.currentIndex.toFloat(), newBeaconData.zAccel))
//
//                            } else {
//                                it.currentIndex = 0
//                                clearData(it)
//                                it.rssi.add(Pair(it.currentIndex.toFloat(), newBeaconData.rssi.toFloat()))
//                                it.temp.add(Pair(it.currentIndex.toFloat(), newBeaconData.temp))
//                                it.x.add(Pair(it.currentIndex.toFloat(), newBeaconData.xAccel))
//                                it.y.add(Pair(it.currentIndex.toFloat(), newBeaconData.yAccel))
//                                it.z.add(Pair(it.currentIndex.toFloat(), newBeaconData.zAccel))
//                            }
//                        }
//                    }
//                }
//                if (deviceMap != null) {
//                    val list: MutableList<BeaconChartData> = mutableListOf()
//                    deviceMap.map {
//                        list.add(it.value)
//                    }
//                    _beacons.value = list
//                }
//                _activeBeacons.postValue(deviceMap)
//            }
//        }
//    }

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


//NanoBeaconManager.adxlData.collect{
//
//    val t = _temp.value?.toMutableList()
//    t?.add(Pair(index.toFloat(), it.temp))
//    _temp.postValue(t?.toList())
//
//    val r = _rssi.value?.toMutableList()
//    r?.add(Pair(index.toFloat(), it.rssi.toFloat()))
//    _rssi.postValue(r?.toList())
//
//    val x = _x.value?.toMutableList()
//    x?.add(Pair(index.toFloat(), it.xAccel.toFloat()))
//    _x.postValue(x?.toList())
//
//    val y = _y.value?.toMutableList()
//    y?.add(Pair(index.toFloat(), it.yAccel.toFloat()))
//    _y.postValue(y?.toList())
//
//    val z = _z.value?.toMutableList()
//    z?.add(Pair(index.toFloat(), it.zAccel.toFloat()))
//    _z.postValue(z?.toList())
//
//    if (index < 50) {
//        index++
//    } else {
//        index = 0
//        _temp.value = listOf()
//        _rssi.value = listOf()
//        _x.value = listOf()
//        _y.value = listOf()
//        _z.value = listOf()
//    }
//}