package com.oncelabs.nanobeacon.manager

import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.codable.Payload
import com.oncelabs.nanobeacon.enum.ADType
import com.oncelabs.nanobeaconlib.enums.DynamicDataType
import com.oncelabs.nanobeacon.extension.StringExtensions.Companion.decodeHex
import com.oncelabs.nanobeaconlib.model.ParsedAdvertisementData
import com.oncelabs.nanobeaconlib.model.ParsedConfigData
import com.oncelabs.nanobeaconlib.model.ParsedDynamicData
import com.oncelabs.nanobeaconlib.model.ParsedPayload
import com.oncelabs.nanobeaconlib.parser.DynamicDataParsers
import com.oncelabs.nanobeaconlib.manager.NanoBeaconManager
import com.oncelabs.nanobeaconlib.model.NanoBeaconData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalMaterialApi::class)
@Singleton
class ConfigDataManagerImpl
@Inject constructor() : ConfigDataManager {
    private val _savedConfig = MutableStateFlow<ConfigData?>(null)
    override val savedConfig : StateFlow<ConfigData?> = _savedConfig.asStateFlow()

    private val _parsedConfig = MutableStateFlow<ParsedConfigData?>(null)
    override val parsedConfig: StateFlow<ParsedConfigData?> = _parsedConfig.asStateFlow()
    override fun init() {
        TODO("Not yet implemented")
    }


    override fun setConfig(configData: ConfigData) {
        _savedConfig.value = configData
    }

    fun parseConfigData(configData: ConfigData) {

        configData.advSet?.let {
            var parsedAdvertisements = mutableListOf<ParsedAdvertisementData>()
            for (advData in it) {
                val parsedPayload = parsePayload(advData.payload)
                val id = advData.id
                val bdAddr = advData.bdAddr
                val parsedAdvertisementData = ParsedAdvertisementData(
                    id = id,
                    bdAddr = bdAddr,
                    parsedPayloadItems = parsedPayload
                )
                parsedAdvertisements.add(parsedAdvertisementData)
            }
            if (configData.tempUnit != null && configData.vccUnit != null) {
                NanoBeaconManager.loadConfiguration(ParsedConfigData(parsedAdvertisements.toTypedArray(), tempUnit = configData.tempUnit, vccUnit = configData.vccUnit))
            }
        }
    }

    fun parsePayload(payloads: Array<Payload>?): ParsedPayload? {
        var parsedPayload = ParsedPayload(null, null, null)

        if (payloads.isNullOrEmpty()) {
            return null
        }

        for (payload in payloads) {
            payload.type?.let {
                val adType: ADType? = ADType.fromType(it)
                when (adType) {
                    ADType.MANUFACTURER_DATA -> {
                        payload.data?.let { data ->
                            parsedPayload.manufacturerData = parseManufacturerData(data)
                        }
                    }
                    ADType.TX_POWER -> {
                        payload.data?.let { data ->
                            parsedPayload.txPower = data.decodeHex()
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

    fun parseManufacturerData(raw: String): List<ParsedDynamicData>? {
        var splitRaw : List<String> = raw.split("<").toList()
        splitRaw = splitRaw.drop(1)

        var parsedList: MutableList<ParsedDynamicData> = mutableListOf()

        for (dynamicRaw in splitRaw) {
            val droppedEnd = dynamicRaw.dropLast(1)
            val endOfName = (droppedEnd.indexOf("byte") - 2)

            val name = droppedEnd.substring(startIndex = 0, endIndex = endOfName)

            val splitData = droppedEnd.substring(startIndex = endOfName + 1).split(" ")

            val dynamicDataType: DynamicDataType? = DynamicDataType.fromAbr(name)
            val len = splitData[0].substring(0, splitData[0].indexOf("byte")).toInt()
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

    override fun processDeviceData(data : NanoBeaconData) {
        parsedConfig.value?.let {
            for (adv in it.advSetData) {
                adv.parsedPayloadItems?.manufacturerData?.let { manufacturerDataFlags ->
                    var currentIndex = 0
                    Log.d("TESTING", data.manufacturerData.toString())
                    for (dynamicDataFlag in manufacturerDataFlags) {
                        val endIndex = currentIndex + dynamicDataFlag.len
                        if (endIndex <= data.manufacturerData.size) {
                            val trimmedData = data.manufacturerData.copyOfRange(currentIndex, endIndex)
                            when (dynamicDataFlag.dynamicType) {
                                DynamicDataType.VCC_ITEM -> Log.d(
                                    "VCC",
                                    DynamicDataParsers.processVcc(trimmedData, savedConfig.value?.vccUnit ?: 0.0F,dynamicDataFlag.bigEndian ?: false, ).toString()
                                )
                                DynamicDataType.TEMP_ITEM -> Log.d(
                                    "TEMP_ITEM",
                                    DynamicDataParsers.processInternalTemp(trimmedData, savedConfig.value?.tempUnit ?: 0.0F,dynamicDataFlag.bigEndian ?: false, ).toString()
                                )
                                DynamicDataType.PULSE_ITEM -> Log.d(
                                    "PULSE_ITEM",
                                    DynamicDataParsers.processWireCount(trimmedData, dynamicDataFlag.bigEndian ?: false).toString()
                                )
                                DynamicDataType.GPIO_ITEM -> Log.d(
                                    "GPIO_ITEM",
                                    DynamicDataParsers.processGpioStatus(trimmedData).toString()
                                )
                                DynamicDataType.AON_GPIO_ITEM -> TODO()
                                DynamicDataType.EDGE_CNT_ITEM -> Log.d(
                                    "EDGE_CNT_ITEM",
                                    DynamicDataParsers.processGpioEdgeCount(trimmedData, dynamicDataFlag.bigEndian ?: false).toString()
                                )
                                DynamicDataType.ADC_CH0_ITEM -> Log.d(
                                    "ADC_CH0_ITEM",
                                    DynamicDataParsers.processCh01(trimmedData, dynamicDataFlag.bigEndian ?: false).toString()
                                )
                                DynamicDataType.ADC_CH1_ITEM -> Log.d(
                                    "ADC_CH1_ITEM",
                                    DynamicDataParsers.processCh01(trimmedData, dynamicDataFlag.bigEndian ?: false).toString()
                                )
                                DynamicDataType.ADC_CH2_ITEM -> TODO()
                                DynamicDataType.ADC_CH3_ITEM -> TODO()
                                DynamicDataType.REG1_ITEM -> TODO()
                                DynamicDataType.REG2_ITEM -> TODO()
                                DynamicDataType.REG3_ITEM -> TODO()
                                DynamicDataType.QDEC_ITEM -> TODO()
                                DynamicDataType.TS0_ITEM -> Log.d(
                                    "TS0_ITEM",
                                    DynamicDataParsers.processTimeStamp(trimmedData, dynamicDataFlag.bigEndian ?: false).toString()
                                )
                                DynamicDataType.TS1_ITEM -> Log.d(
                                    "TS1_ITEM",
                                    DynamicDataParsers.processTimeStamp(trimmedData, dynamicDataFlag.bigEndian ?: false).toString()
                                )
                                DynamicDataType.ADVCNT_ITEM -> Log.d(
                                    "ADVCNT_ITEM",
                                    DynamicDataParsers.processAdv(trimmedData, dynamicDataFlag.bigEndian ?: false).toString()
                                )
                                DynamicDataType.REG_ITEM -> TODO()
                                DynamicDataType.RANDOM_ITEM -> Log.d(
                                    "RANDOM_ITEM",
                                    DynamicDataParsers.processRandomNumber(trimmedData, dynamicDataFlag.bigEndian ?: false).toString()
                                )
                                DynamicDataType.STATIC_RANDOM_ITEM -> Log.d(
                                    "STATIC_RANDOM_ITEM",
                                    DynamicDataParsers.processRandomNumber(trimmedData, dynamicDataFlag.bigEndian ?: false).toString()
                                )
                                DynamicDataType.ENCRYPT_ITEM -> TODO()
                                DynamicDataType.SALT_ITEM -> TODO()
                                DynamicDataType.TAG_ITEM -> TODO()
                                DynamicDataType.CUSTOM_PRODUCT_ID_ITEM -> Log.d(
                                    "CUSTOM_PRODUCT_ID_ITEM",
                                    DynamicDataParsers.processRandomNumber(trimmedData, dynamicDataFlag.bigEndian ?: false).toString()
                                )
                                DynamicDataType.BLUETOOTH_DEVICE_ADDRESS_ITEM -> Log.d(
                                    "BLUETOOTH_DEVICE_ADDRESS_ITEM",
                                    DynamicDataParsers.processRandomNumber(trimmedData, dynamicDataFlag.bigEndian ?: false).toString()
                                )
                                DynamicDataType.UTF8_ITEM -> TODO()
                            }
                            currentIndex = endIndex
                        } else {
                            break
                        }
                    }
                }
            }
        }
    }
}



