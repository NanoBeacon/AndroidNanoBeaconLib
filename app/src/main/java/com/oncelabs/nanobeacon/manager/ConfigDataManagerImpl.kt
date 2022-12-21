package com.oncelabs.nanobeacon.manager

import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.codable.Payload
import com.oncelabs.nanobeacon.enum.ADType
import com.oncelabs.nanobeaconlib.enums.DynamicDataType
import com.oncelabs.nanobeacon.extension.StringExtensions.Companion.decodeHex
import com.oncelabs.nanobeaconlib.enums.AdvMode
import com.oncelabs.nanobeaconlib.enums.ConfigType
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
    private val _parsedConfig = MutableStateFlow<ParsedConfigData?>(null)
    override val parsedConfig: StateFlow<ParsedConfigData?> = _parsedConfig.asStateFlow()

    override fun init() {
        TODO("Not yet implemented")
    }


    override fun setConfig(configData: ConfigData) {
        parseConfigData(configData)
    }

    private fun parseConfigData(configData: ConfigData) {

        configData.advSet?.let {
            var parsedAdvertisements = mutableListOf<ParsedAdvertisementData>()
            for (advData in it) {
                val parsedPayload = parsePayload(advData.payload, advData.ui_format)
                val id = advData.id
                val bdAddr = advData.bdAddr
                val parsedAdvertisementData = ParsedAdvertisementData(
                    id = id,
                    bdAddr = bdAddr,
                    parsedPayloadItems = parsedPayload,
                    ui_format = ConfigType.fromLabel(advData.ui_format),
                    interval = advData.interval,
                    advModeTrigEn = AdvMode.fromMode(advData.advModeTrigEn),
                )
                parsedAdvertisements.add(parsedAdvertisementData)
            }
            if (configData.tempUnit != null && configData.vccUnit != null) {
                val configData = ParsedConfigData(
                    parsedAdvertisements.toTypedArray(),
                    tempUnit = configData.tempUnit,
                    vccUnit = configData.vccUnit,
                    txPower = configData.txSetting?.txPower,
                    sleepAftTx = configData.txSetting?.sleepAftTx == 1,
                    ch0 = configData.txSetting?.ch0,
                    ch1 = configData.txSetting?.ch1,
                    ch2 = configData.txSetting?.ch2
                )
                NanoBeaconManager.loadConfiguration(configData)
                _parsedConfig.value = configData
            }
        }
    }

    fun parsePayload(payloads: Array<Payload>?, type: String): ParsedPayload? {
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
                            if (type == "ibeacon") {
                                parsedPayload.manufacturerData = parseIBeaconManufacturerData(data)
                            }
                            if (type == "custom") {
                                parsedPayload.manufacturerData = parseCustomManufacturerData(data)
                            }
                        }
                    }
                    ADType.TX_POWER -> {
                        payload.data?.let { data ->
                            parsedPayload.txPower = data
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

    private fun parseCustomManufacturerData(raw: String): Map<DynamicDataType, ParsedDynamicData>? {
        var splitRaw: List<String> = raw.split("<").toList()
        splitRaw = splitRaw.drop(1)

        var parsedMap: MutableMap<DynamicDataType, ParsedDynamicData> = mutableMapOf()

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
                val data = ParsedDynamicData(len, dynamicDataType, bigEndian, encrypted, null)
                parsedMap[dynamicDataType] = data
            }
        }
        if (parsedMap.isNotEmpty()) {
            return parsedMap
        }
        return null
    }

    private fun parseIBeaconManufacturerData(raw: String): Map<DynamicDataType, ParsedDynamicData>? {
        var result: MutableMap<DynamicDataType, ParsedDynamicData> = mutableMapOf()
        if (raw.length >= 50) {
            result[DynamicDataType.IBEACON_ADDR] = ParsedDynamicData(
                len = 2,
                dynamicType = DynamicDataType.IBEACON_ADDR,
                bigEndian = false,
                encrypted = false,
                rawData = raw.substring(0, 8)
            )
            result[DynamicDataType.UUID] = ParsedDynamicData(
                len = 16,
                dynamicType = DynamicDataType.UUID,
                bigEndian = false,
                encrypted = false,
                rawData = raw.substring(8, 40)
            )
            result[DynamicDataType.MAJOR] = ParsedDynamicData(
                len = 2,
                dynamicType = DynamicDataType.MAJOR,
                bigEndian = false,
                encrypted = false,
                rawData = raw.substring(40, 44)
            )
            result[DynamicDataType.MINOR] = ParsedDynamicData(
                len = 2,
                dynamicType = DynamicDataType.MINOR,
                bigEndian = false,
                encrypted = false,
                rawData = raw.substring(44, 48)
            )
            result[DynamicDataType.TX_POWER] = ParsedDynamicData(
                len = 1,
                dynamicType = DynamicDataType.TX_POWER,
                bigEndian = false,
                encrypted = false,
                rawData = raw.substring(48, 50)
            )

            return result
        }
        return null
    }
}



