package com.oncelabs.template.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.oncelabs.template.beaconManager
import com.oncelabs.template.nanoBeaconLib.manager.ADXLData
import com.oncelabs.template.nanoBeaconLib.manager.NanoBeaconManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveDataViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    private val _temp: MutableLiveData<List<Pair<Float, Float>>> = MutableLiveData(listOf())
    val temp : LiveData<List<Pair<Float, Float>>> = _temp

    private val _rssi: MutableLiveData<List<Pair<Float, Float>>> = MutableLiveData(listOf())
    val rssi : LiveData<List<Pair<Float, Float>>> = _rssi

    private val _x: MutableLiveData<List<Pair<Float, Float>>> = MutableLiveData(listOf())
    val x : LiveData<List<Pair<Float, Float>>> = _x

    private val _y: MutableLiveData<List<Pair<Float, Float>>> = MutableLiveData(listOf())
    val y : LiveData<List<Pair<Float, Float>>> = _y

    private val _z: MutableLiveData<List<Pair<Float, Float>>> = MutableLiveData(listOf())
    val z : LiveData<List<Pair<Float, Float>>> = _z

    private var index = 0

    init {
        beaconManager?.startScanning()
        viewModelScope.launch {
            beaconManager?.adxlData?.collect{

                val t = _temp.value?.toMutableList()
                t?.add(Pair(index.toFloat(), it.temp))
                _temp.postValue(t?.toList())

                val r = _rssi.value?.toMutableList()
                r?.add(Pair(index.toFloat(), it.rssi.toFloat()))
                _rssi.postValue(r?.toList())

                val x = _x.value?.toMutableList()
                x?.add(Pair(index.toFloat(), it.xAccel.toFloat()))
                _x.postValue(x?.toList())

                val y = _y.value?.toMutableList()
                y?.add(Pair(index.toFloat(), it.yAccel.toFloat()))
                _y.postValue(y?.toList())

                val z = _z.value?.toMutableList()
                z?.add(Pair(index.toFloat(), it.zAccel.toFloat()))
                _z.postValue(z?.toList())

                if (index < 50) {
                    index++
                } else {
                    index = 0
                    _temp.value = listOf()
                    _rssi.value = listOf()
                    _x.value = listOf()
                    _y.value = listOf()
                    _z.value = listOf()
                }
            }
        }
    }
}