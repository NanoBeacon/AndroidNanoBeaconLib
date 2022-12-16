package com.oncelabs.nanobeacon.viewModel

import android.app.Application
import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.components.BeaconDataEntry
import com.oncelabs.nanobeacon.manager.BeaconManager
import com.oncelabs.nanobeacon.manager.ConfigDataManager
import com.oncelabs.nanobeacon.manager.FilePickerManager
import com.oncelabs.nanobeacon.model.FilterOption
import com.oncelabs.nanobeacon.model.FilterType
import com.oncelabs.nanobeaconlib.enums.DynamicDataType
import com.oncelabs.nanobeaconlib.enums.ScanState
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconInterface
import com.oncelabs.nanobeaconlib.model.NanoBeacon
import com.oncelabs.nanobeaconlib.model.NanoBeaconData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.scheduleAtFixedRate

@ExperimentalMaterialApi
@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val beaconManager: BeaconManager,
    application: Application,
    private val configDataManager: ConfigDataManager,
    private val filePickerManager: FilePickerManager
): AndroidViewModel(application) {

    private val TAG = ScannerViewModel::class.simpleName

    private var filterTimer: TimerTask? = null
    private val _beaconDataEntries = MutableLiveData<List<BeaconDataEntry>>()
    private val _discoveredBeacons = MutableLiveData<List<NanoBeaconInterface>>()
    private val _scanningEnabled = MutableLiveData(true)
    private val _savedConfigs = MutableLiveData<List<ConfigData>>(filePickerManager.savedConfigs.value)
    private val _currentFiltersDescription = MutableLiveData<String?>(null)
    val savedConfigs : LiveData<List<ConfigData>> = _savedConfigs
    val filteredDiscoveredBeacons: LiveData<List<NanoBeaconInterface>> = _filteredDiscoveredBeacons
    val scanningEnabled: LiveData<Boolean> = _scanningEnabled

    private val _filters = MutableLiveData(FilterOption.getDefaultOptions())
    val filters: LiveData<List<FilterOption>> = _filters
    val currentFiltersDescription: LiveData<String?> = _currentFiltersDescription

    private val _filteredDiscoveredBeacons = MutableLiveData<List<NanoBeaconInterface>>()
    val filteredDiscoveredBeacons: LiveData<List<NanoBeaconInterface>> = _filteredDiscoveredBeacons

    private val _savedConfigs = MutableLiveData<ConfigData>(configDataManager.savedConfig.value)
    val savedConfigs : LiveData<ConfigData> = _savedConfigs

    private val _showDetailModal = MutableLiveData<Boolean>(false)
    val showDetailModal = _showDetailModal

    private val _currentDetailBeacon = MutableLiveData<NanoBeaconInterface?>(null)
    val currentDetailBeacon = _currentDetailBeacon

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

    fun setCurrentDetailData(beacon : NanoBeaconInterface?) {
        _currentDetailBeacon.value = beacon
    }

    fun setShowDetailModal(value : Boolean) {
        _showDetailModal.value = value
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
                Log.d(TAG, "Scan State $it")
                _scanningEnabled.postValue(it == ScanState.SCANNING)
            }
        }

        viewModelScope.launch {
            beaconManager.discoveredBeacons.collect {
                _discoveredBeacons.postValue(it)
                Log.d(TAG, "Updated Beacon Count ${it.count()}")
            }
        }

        viewModelScope.launch {
            configDataManager.savedConfig.collect {
                _savedConfigs.postValue(it)
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
                if(!filter.enabled) { continue }
                when(filter.filterType) {
                    FilterType.ADDRESS -> {
                        filteredList = filteredList.filter {
                            (it.beaconDataFlow.value?.bluetoothAddress)?.contains(filter.value as? String ?: "") ?: false
                        }
                    }
                    FilterType.ADVANCED_SEARCH -> {
                        filteredList = filteredList.filter {
                            it.beaconDataFlow.value?.searchableString?.contains(filter.value as? String ?: "", ignoreCase = true) ?: false
                        }
                    }
                    FilterType.RSSI -> {
                        filteredList = filteredList.filter {
                            (it.beaconDataFlow.value?.rssi?.toFloat() ?: -127f) > (filter.value as? Float ?: 0f)
                        }
                    }
                    FilterType.HIDE_UNNAMED -> {
                        filteredList = filteredList.filter {
                            (it.beaconDataFlow.value?.name?.isNotBlank()) ?: true
                        }
                    }
                    FilterType.ONLY_SHOW_CONFIGURATION -> {
                        filteredList = filteredList.filter {
                            it.matchingConfig != null
                        }
                    }
                    FilterType.BY_TYPE -> {
                        Log.e(TAG, "${filter.filterType} not yet implemented")
                    }
                    FilterType.NAME -> {
                        (filter.value as? String)?.let { value ->
                            if (value.isNotEmpty()) {
                                filteredList = filteredList.filter {
                                    it.beaconDataFlow.value?.name?.contains(
                                        value,
                                        ignoreCase = true
                                    ) == true
                                }
                            }
                        }
                    }
                    FilterType.SORT_RSSI -> {
                        filteredList = filteredList.sortedBy { it.beaconDataFlow.value?.rssi }
                    }
                }
            }
        }
        return filteredList
    }

    /**
     * Mutate a specified [type] filter value
     */
    fun onFilterChanged(type: FilterType, value: Any?, enabled: Boolean) {
        val index = _filters.value?.indexOfFirst { it.filterType == type }
        if(index != -1 && index != null) {
            val filterCopy = _filters.value?.toMutableList()
            filterCopy?.get(index)?.value = value
            filterCopy?.get(index)?.enabled = enabled
            _filters.value = listOf()
            _filters.value = filterCopy
        }

        _filters.value?.mapNotNull { it.getDescription() }?.let { filterDescriptions ->
            if(filterDescriptions.isEmpty()) {
                _currentFiltersDescription.value = "No filters"
                return
            }

            _currentFiltersDescription.value = filterDescriptions.joinToString(", ")
        }
    }



    fun openFilePickerManager() {
        filePickerManager.openFilePicker()
    }
}