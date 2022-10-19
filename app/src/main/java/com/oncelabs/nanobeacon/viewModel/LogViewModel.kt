package com.oncelabs.nanobeacon.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oncelabs.nanobeacon.components.BeaconDataEntry
import com.oncelabs.nanobeacon.manager.BeaconManager
import com.oncelabs.nanobeacon.nanoBeaconLib.extension.toHexString
import com.oncelabs.nanobeacon.nanoBeaconLib.model.NanoBeaconData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    private val _beaconDataEntries = MutableLiveData<List<BeaconDataEntry>>()
    val beaconDataEntries: LiveData<List<BeaconDataEntry>> = _beaconDataEntries

    init {
        addObservers()
    }

    private fun addObservers(){
        viewModelScope.launch {
            BeaconManager.newBeaconDataFlow.collect {
                val beaconEntriesCopy = beaconDataEntries.value?.toMutableList() ?: mutableListOf()
                beaconEntriesCopy.add(formatToEntry(nanoBeaconData = it))
                _beaconDataEntries.postValue(beaconEntriesCopy)
            }
        }
    }

    /**
     * Format the beacon class to a usable model for the view
     * @param nanoBeaconData the incoming beacon
     * @return [BeaconDataEntry] formatted for view
     */
    private fun formatToEntry(nanoBeaconData: NanoBeaconData): BeaconDataEntry {
        return BeaconDataEntry(
            address = nanoBeaconData.bluetoothAddress,
            timestamp = nanoBeaconData.timeStampFormatted,
            rssi = "${nanoBeaconData.rssi}",
            advInterval = "${nanoBeaconData.estimatedAdvInterval}",
            manufacturerData = nanoBeaconData.manufacturerData.toHexString().uppercase(),
            manufacturerId = nanoBeaconData.manufacturerId.toHexString(),
            txPower = "${nanoBeaconData.txPowerClaimed}",
            localName = nanoBeaconData.name ?: "Unknown",
            flags = "${nanoBeaconData.flags}",
            txPowerObserved = "${nanoBeaconData.transmitPowerObserved}",
            primaryPhy = "${nanoBeaconData.primaryPhy}",
            secondaryPhy = "${nanoBeaconData.secondaryPhy}"
        )
    }
}