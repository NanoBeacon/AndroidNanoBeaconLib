package com.oncelabs.nanobeacon.manager

import androidx.compose.material.ExperimentalMaterialApi
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.codable.Payload
import com.oncelabs.nanobeacon.enum.ADType
import com.oncelabs.nanobeacon.enum.DynamicDataType
import com.oncelabs.nanobeacon.model.ConfigDynamicDataType
import com.oncelabs.nanobeacon.model.ParsedConfigData
import com.oncelabs.nanobeacon.model.ParsedDynamicData
import com.oncelabs.nanobeacon.model.ParsedPayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalMaterialApi::class)
@Singleton
class ConfigDataManagerImpl @Inject constructor() : ConfigDataManager {
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

            }
        }
        return null
    }

    fun parsePayload(payload: Payload) : ParsedPayload? {
        payload.type?.let {
            val adType : ADType? = ADType.fromType(it)
            when (adType) {
                ADType.MANUFACTURER_DATA -> {

                }
                ADType.TX_POWER -> {
                    val txPower = payload.data?.toInt()
                    return null
                }
                ADType.DEVICE_NAME -> {
                    return null
                }
                else -> {
                    return null
                }
            }
        } ?: run {
            return null
        }
    }

    fun parseTxPower(raw : String) {

    }

    fun parseManufacturerData(raw : String) : ParsedDynamicData? {
        val splitRaw : Array<String> = raw.split("<").toTypedArray()
        for (dynamicRaw in splitRaw) {
            val droppedEnd = dynamicRaw.dropLast(1)
            val endOfName = (droppedEnd.indexOf("byte") - 2)

            val name = droppedEnd.substring(startIndex = 0, endIndex = endOfName)

            val splitData = droppedEnd.substring(startIndex = endOfName + 1).split(" ")
            val len = splitData
        }

        return null
    }

}