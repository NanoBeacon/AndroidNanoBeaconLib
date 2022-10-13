package com.oncelabs.nanobeacon.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oncelabs.nanobeacon.manager.BeaconManager
import com.oncelabs.nanobeacon.nanoBeaconLib.model.NanoBeacon
import com.oncelabs.nanobeacon.nanoBeaconLib.model.NanoBeaconData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    private val _beaconDataLog = MutableLiveData<List<NanoBeaconData>>()
    val beaconDataLog: LiveData<List<NanoBeaconData>> = _beaconDataLog

    init {
        addObservers()
    }

    private fun addObservers(){
        viewModelScope.launch {
            BeaconManager.newBeaconDataFlow.collect {
                val beaconData: MutableList<NanoBeaconData> = beaconDataLog.value?.toMutableList() ?: mutableListOf<NanoBeaconData>()
                beaconData.add(it)
                _beaconDataLog.postValue(beaconData.toList())
            }
        }
    }
}