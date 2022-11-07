package com.oncelabs.nanobeacon.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oncelabs.nanobeacon.components.BeaconDataEntry
import com.oncelabs.nanobeacon.manager.BeaconManager
import com.oncelabs.nanobeacon.model.FilterOption
import com.oncelabs.nanobeacon.model.FilterType
import com.oncelabs.nanobeaconlib.enums.ScanState
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.scheduleAtFixedRate

@HiltViewModel
class LogViewModel @Inject constructor(
    private val beaconManager: BeaconManager,
    application: Application
): AndroidViewModel(application) {

    private val TAG = LogViewModel::class.simpleName

    private var filterTimer: TimerTask? = null
    private val _beaconDataEntries = MutableLiveData<List<BeaconDataEntry>>()
    private val _filteredDiscoveredBeacons = MutableLiveData<List<NanoBeaconInterface>>()
    private val _filters = MutableLiveData(FilterOption.getDefaultOptions())
    private val _scanningEnabled = MutableLiveData(true)
    private val _discoveredBeacons = MutableLiveData<List<NanoBeaconInterface>>()

    val filteredDiscoveredBeacons: LiveData<List<NanoBeaconInterface>> = _filteredDiscoveredBeacons
    val scanningEnabled: LiveData<Boolean> = _scanningEnabled
    val filters: LiveData<List<FilterOption>> = _filters

    init {
        addObservers()
        startFilterTimer()
        beaconManager.startScanning()
    }

    fun startScanning(){
        beaconManager.startScanning()
    }

    fun stopScanning(){
        beaconManager.stopScanning()
    }

    fun refresh(){
        beaconManager.refresh()
    }

    private fun addObservers(){

        viewModelScope.launch {
            beaconManager.newBeaconDataFlow.collect {
                val beaconEntriesCopy = _beaconDataEntries.value?.toMutableList() ?: mutableListOf()
                _beaconDataEntries.postValue(beaconEntriesCopy)
            }
        }

        viewModelScope.launch {
            beaconManager.scanningEnabled.collect {
                _scanningEnabled.postValue(it == ScanState.SCANNING)
            }
        }

        viewModelScope.launch {
            beaconManager.discoveredBeacons.collect {
                _discoveredBeacons.postValue(it)
                Log.d(TAG, "Updated Beacon Count ${it.count()}")
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