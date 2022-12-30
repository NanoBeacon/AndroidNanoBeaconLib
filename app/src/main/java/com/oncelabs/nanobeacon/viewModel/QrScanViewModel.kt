package com.oncelabs.nanobeacon.viewModel

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import com.oncelabs.nanobeacon.analyzer.gzipDecompress
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.device.ADXL367
import com.oncelabs.nanobeacon.manager.BeaconManager
import com.oncelabs.nanobeacon.manager.ConfigDataManager
import com.oncelabs.nanobeacon.manager.ConfigDataManagerImpl
import com.oncelabs.nanobeacon.manager.FilePickerManager
import com.oncelabs.nanobeacon.manager.settings.SettingsManager
import com.oncelabs.nanobeacon.model.ADXL367Data
import com.oncelabs.nanobeaconlib.enums.BleState
import com.oncelabs.nanobeaconlib.enums.ScanState
import com.oncelabs.nanobeaconlib.model.ParsedConfigData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.GZIPInputStream
import javax.inject.Inject

@ExperimentalMaterialApi
@HiltViewModel
class QrScanViewModel @Inject constructor(
    application: Application,
    private val configDataManager : ConfigDataManager,
    private val beaconManager: BeaconManager
    ): AndroidViewModel(application) {

    var pendingQr: String? = null

    private val _showQrScanner : MutableLiveData<Boolean> = MutableLiveData(
        false
    )
    val showQrScanner : LiveData<Boolean> = _showQrScanner

    private val _currentConfig : MutableLiveData<ParsedConfigData?> = MutableLiveData(configDataManager.parsedConfig.value)
    val currentConfig : LiveData<ParsedConfigData?> = _currentConfig

    init {
        observeFields()
    }

    fun observeFields() {
        viewModelScope.launch {
            configDataManager.parsedConfig.collect {
                _currentConfig.value = it
            }
        }
    }

    fun submitQrConfig(rawValue : String) {
        if (pendingQr != rawValue) {
            pendingQr = rawValue
            try {
                rawValue.toByteArray().gzipDecompress()
                val decoded = Base64.getDecoder().decode(rawValue).gzipDecompress()
                Log.d("JSON", rawValue)
                Log.d("JSON", decoded.toString())
                val parsedData = Klaxon().parse<ConfigData>(decoded)
                parsedData?.let {
                    _showQrScanner.value = false
                    pendingQr = null
                    SettingsManager.setSavedConfig(decoded)
                    configDataManager.setConfig(it)
                    beaconManager.refresh()
                } ?: run {
                    pendingQr = null
                }
            } catch (e: KlaxonException) {
                Log.d(ContentValues.TAG, "Not formatted Correctly")
                pendingQr = null
            }
        }
    }

    fun openScanner() {
        _showQrScanner.value = true
    }

}