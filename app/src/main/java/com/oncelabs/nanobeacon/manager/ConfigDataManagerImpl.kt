package com.oncelabs.nanobeacon.manager

import androidx.compose.material.ExperimentalMaterialApi
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.codable.Payload
import com.oncelabs.nanobeacon.enum.ADType
import com.oncelabs.nanobeacon.enum.DynamicDataType
import com.oncelabs.nanobeacon.extension.StringExtensions.Companion.decodeHex
import com.oncelabs.nanobeacon.model.ParsedConfigData
import com.oncelabs.nanobeacon.model.ParsedDynamicData
import com.oncelabs.nanobeacon.model.ParsedPayload
import com.oncelabs.nanobeacon.parser.DynamicDataParsers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalMaterialApi::class)
@Singleton
class ConfigDataManagerImpl
@Inject constructor() : ConfigDataManager {
    private val _savedConfigs = MutableStateFlow<List<ConfigData>>(listOf())
    override val savedConfigs: StateFlow<List<ConfigData>> = _savedConfigs.asStateFlow()

    private val _parsedConfigs = MutableStateFlow<List<ParsedConfigData>>(listOf())
    override val parsedConfigs: StateFlow<List<ParsedConfigData>> = _parsedConfigs.asStateFlow()
    override fun init() {
        TODO("Not yet implemented")
    }


    override fun addConfigToList(configData: ConfigData) {
        var holder : MutableList<ConfigData> = savedConfigs.value?.toMutableList() ?: mutableListOf()
        holder.add(configData)
        _savedConfigs.value = holder
    }

    fun parseConfigData(configData: ConfigData) : ParsedConfigData? {

        configData?.advSet?.let {
            for (advData in it) {
                val parsedPayload = parsePayload(advData.payload)
                val id = advData.id
                val bdAddr = advData.bdAddr

            }
        }
        return null
    }

    fun parsePayload(payloads : Array<Payload>?) : ParsedPayload? {
        var parsedPayload = ParsedPayload(null, null, null)

        if (payloads.isNullOrEmpty()) {
            return null
        }

        for (payload in payloads) {
            payload.type?.let {
                val adType : ADType? = ADType.fromType(it)
                when (adType) {
                    ADType.MANUFACTURER_DATA -> {
                        payload.data?.let { data ->
                            parsedPayload.manufacturerData = parseManufacturerData(data)
                        }
                    }
                    ADType.TX_POWER -> {
                        payload.data?.let { data ->
                            parsedPayload.txPower = data.decodeHex().toInt()
                        }
                    }
                    ADType.DEVICE_NAME -> {
                        payload.data?.let { data ->
                            parsedPayload.deviceName = data.decodeHex()
                        }
                    }
                    else -> {}
                }
            }
        }
        return parsedPayload
    }

    fun parseManufacturerData(raw : String) : List<ParsedDynamicData>? {
        val splitRaw : Array<String> = raw.split("<").toTypedArray()
        var parsedList : MutableList<ParsedDynamicData> = mutableListOf()
        for (dynamicRaw in splitRaw) {
            val droppedEnd = dynamicRaw.dropLast(1)
            val endOfName = (droppedEnd.indexOf("byte") - 2)

            val name = droppedEnd.substring(startIndex = 0, endIndex = endOfName)

            val splitData = droppedEnd.substring(startIndex = endOfName + 1).split(" ")

            val dynamicDataType : DynamicDataType? = DynamicDataType.fromAbr(name)
            val len = splitData[0].substring(0,splitData.indexOf("bytes")).toInt()
            val bigEndian = splitData[1].toInt() == 1
            val encrypted = splitData[2].toInt() == 1
            dynamicDataType?.let {
                val data = ParsedDynamicData(len, dynamicDataType, bigEndian, encrypted)
                parsedList.add(data)
            }
        }
        if (parsedList.isNotEmpty()) {
            return parsedList
        }
        return null
    }
}