package com.oncelabs.nanobeacon.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oncelabs.nanobeacon.components.BeaconDataEntry
import com.oncelabs.nanobeacon.manager.BeaconManager
import com.oncelabs.nanobeacon.model.FilterOption
import com.oncelabs.nanobeacon.model.FilterType
import com.oncelabs.nanobeaconlib.enums.ScanState
import com.oncelabs.nanobeaconlib.extension.toHexString
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconInterface
import com.oncelabs.nanobeaconlib.model.NanoBeacon
import com.oncelabs.nanobeaconlib.model.NanoBeaconData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.scheduleAtFixedRate

@HiltViewModel
class LogViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    private var filterTimer: TimerTask? = null

    private val _beaconDataEntries = MutableLiveData<List<BeaconDataEntry>>()
    private val _filteredBeaconDataEntries = MutableLiveData<List<BeaconDataEntry>>()
    private val _filteredDiscoveredBeacons = MutableLiveData<List<NanoBeaconInterface>>()
    private val _filters = MutableLiveData(FilterOption.getDefaultOptions())
    private val _scanningEnabled = MutableLiveData(true)
    private val _discoveredBeacons = MutableLiveData<List<NanoBeaconInterface>>()

    val discoveredBeacons: LiveData<List<NanoBeaconInterface>> = _discoveredBeacons
    val filteredDiscoveredBeacons: LiveData<List<NanoBeaconInterface>> = _filteredDiscoveredBeacons
    val scanningEnabled: LiveData<Boolean> = _scanningEnabled
    val filteredBeaconDataEntries: LiveData<List<BeaconDataEntry>> =_filteredBeaconDataEntries
    val filters: LiveData<List<FilterOption>> = _filters

    init {
        addObservers()
        startFilterTimer()
    }

    fun startScanning(){
        BeaconManager.startScanning()
    }

    fun stopScanning(){
        BeaconManager.stopScanning()
    }

    private fun addObservers(){
        viewModelScope.launch {
            BeaconManager.newBeaconDataFlow.collect {
                val beaconEntriesCopy = _beaconDataEntries.value?.toMutableList() ?: mutableListOf()
                //beaconEntriesCopy.add(formatToEntry(nanoBeaconData = it))
                _beaconDataEntries.postValue(beaconEntriesCopy)
            }
        }

        viewModelScope.launch {
            BeaconManager.scanningEnabled.collect {
                _scanningEnabled.postValue(it == ScanState.SCANNING)
            }
        }

        viewModelScope.launch {
            BeaconManager.discoveredBeacons.collect {
                _discoveredBeacons.postValue(it)
            }
        }
    }

    /**
     * Filter all results on timer
     */
    private fun startFilterTimer() {
        filterTimer?.cancel()
        filterTimer = Timer().scheduleAtFixedRate(0, 1000) {
            _filteredDiscoveredBeacons.postValue(filterResults(unfilteredBeacons = _discoveredBeacons.value ?: listOf()))
        }
    }

    private fun filterResults(unfilteredBeacons: List<NanoBeaconInterface>): List<NanoBeaconInterface> {
        var filteredList = unfilteredBeacons

        _filters.value?.let { filters ->
            for(filter in filters) {
                when(filter.filterType) {
                    FilterType.RSSI -> {
                        filteredList = filteredList.filter {
                            (it.beaconDataFlow.value?.rssi?.toFloat() ?: -127f) > (filter.value as? Float ?: 0f)
                        }
                    }
                }
            }
        }
        return filteredList
    }

    /**
     * Mutate a specified [type] filter value
     */
    fun setFilter(type: FilterType, value: Any?, enabled: Boolean) {
        val index = _filters.value?.indexOfFirst { it.filterType == type }
        if(index != -1 && index != null) {
            val filterCopy = _filters.value?.toMutableList()
            filterCopy?.get(index)?.value = value
            filterCopy?.get(index)?.enabled = enabled
            _filters.value = listOf()
            _filters.value = filterCopy
        }
    }
}