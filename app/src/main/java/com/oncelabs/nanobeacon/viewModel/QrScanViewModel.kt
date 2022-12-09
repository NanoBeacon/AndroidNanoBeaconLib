package com.oncelabs.nanobeacon.viewModel

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
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
import com.oncelabs.nanobeacon.manager.FilePickerManager
import com.oncelabs.nanobeacon.model.ADXL367Data
import com.oncelabs.nanobeaconlib.enums.BleState
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
    private val filePickerManager : FilePickerManager

): AndroidViewModel(application) {

    var pendingQr: String? = null

    private val _showModal : MutableLiveData<Boolean> = MutableLiveData(true)
    val showModal : LiveData<Boolean> = _showModal

    private val _stagedConfig : MutableLiveData<ConfigData?> = MutableLiveData(null)
    val stagedConfig : LiveData<ConfigData?> = _stagedConfig



    fun submitQrConfig(rawValue : String) {

        if (pendingQr != rawValue) {
            pendingQr = rawValue
            try {
                rawValue.toByteArray().gzipDecompress()
                val decoded = Base64.getDecoder().decode(rawValue).gzipDecompress()
                Log.d("JSON", rawValue)
                Log.d("JSON", decoded.toString())
                _showModal.postValue(true)
               /* val parsedData = Klaxon().parse<ConfigData>(decoded)
                parsedData?.let {
                    _stagedConfig.value = it
                    Log.d("qwdq", "MADE IT")
                }*/
            } catch (e: KlaxonException) {
                Log.d(ContentValues.TAG, "Not formatted Correctly")
                pendingQr = null
            }
        }
    }

    fun declineConfig() {
        _showModal.value = false
        pendingQr = null
        _stagedConfig.value = null
    }

    fun confirmConfig() {
        stagedConfig.value?.let {
            filePickerManager.addConfigToList(it)
        }
        pendingQr = null
        _stagedConfig.value = null
        _showModal.value = false
    }
}