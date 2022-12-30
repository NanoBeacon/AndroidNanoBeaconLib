package com.oncelabs.nanobeacon.manager

import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.text.toUpperCase
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.codable.GlobalTrigSetting
import com.oncelabs.nanobeacon.codable.Gpio
import com.oncelabs.nanobeacon.codable.Payload
import com.oncelabs.nanobeacon.enum.ADType
import com.oncelabs.nanobeacon.extension.StringExtensions.Companion.decodeHex
import com.oncelabs.nanobeaconlib.enums.*
import com.oncelabs.nanobeaconlib.parser.DynamicDataParsers
import com.oncelabs.nanobeaconlib.manager.NanoBeaconManager
import com.oncelabs.nanobeaconlib.model.*
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

    override fun deleteConfig() {
        _parsedConfig.value = null
        NanoBeaconManager.deleteConfig()
    }

    private fun parseConfigData(configData: ConfigData) {

        configData.advSet?.let {
            val parsedAdvertisements = mutableListOf<ParsedAdvertisementData>()
            for (advData in it) {
                val parsedPayload = parsePayload(advData.payload, advData.ui_format)
                val id = advData.id
                val bdAddr = advData.bdAddr
                val parsedAdvertisementData = ParsedAdvertisementData(
                    id = id,
                    bdAddr = bdAddr?.uppercase(),
                    parsedPayloadItems = parsedPayload,
                    ui_format = ConfigType.fromLabel(advData.ui_format),
                    interval = advData.interval,
                    advModeTrigEn = AdvMode.fromMode(advData.advModeTrigEn),
                    chCtrl = advData.chCtrl,
                    gpioTriggers = advData.gpioTrigerSrc?.toList(),
                    postTrigCtrlMode = PostTriggerControlMode.fromCodes(
                        advData.postTrigCtrlMode,
                        advData.postTrigNumAdv
                    ),
                    postTrigNumAdv = advData.postTrigNumAdv,
                    trigCheckPeriod = advData.trigCheckPeriod,
                    triggers = parseSensorTriggerSources(advData.sensorTrigerSrc),
                    randDlyType = advData.randomDlyType
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
                    ch2 = configData.txSetting?.ch2,
                    globalGpioTriggerSrc = parseGpioTriggerSrc(configData.gpio),
                    globalTrigSettings = parseGlobalTriggerSettings(configData.globalTrigSetting)
                )
                NanoBeaconManager.loadConfiguration(configData)
                _parsedConfig.value = configData
            }
        }
    }

    fun parseGlobalTriggerSettings(globalTrigSettings : GlobalTrigSetting?) : Map<SensorTriggerSource, GlobalTriggerSettings>? {
        globalTrigSettings?.let { globalTrig ->
            var trigSettings = mutableMapOf<SensorTriggerSource, GlobalTriggerSettings>()
            globalTrig.trig1Src?.let { trig1Src ->
                globalTrig.trig1Low?.let {
                    trigSettings[SensorTriggerSource.T1LOW] = GlobalTriggerSettings(it, trig1Src)
                }
            }
            globalTrig.trig2Src?.let { trig2Src ->
                globalTrig.trig2Low?.let {
                    trigSettings[SensorTriggerSource.T2LOW] = GlobalTriggerSettings(it, trig2Src)
                }
                globalTrig.trig2High?.let {
                    trigSettings[SensorTriggerSource.T2HIGH] = GlobalTriggerSettings(it, trig2Src)
                }
            }
            globalTrig.trig3Src?.let { trig3Src ->
                globalTrig.trig3Low?.let {
                    trigSettings[SensorTriggerSource.T3LOW] = GlobalTriggerSettings(it, trig3Src)
                }
                globalTrig.trig3High?.let {
                    trigSettings[SensorTriggerSource.T3HIGH] = GlobalTriggerSettings(it, trig3Src)
                }
            }
            globalTrig.trig4Src?.let { trig4Src ->
                globalTrig.trig4Low?.let {
                    trigSettings[SensorTriggerSource.T4LOW] = GlobalTriggerSettings(it, trig4Src)
                }
                globalTrig.trig4High?.let {
                    trigSettings[SensorTriggerSource.T4HIGH] = GlobalTriggerSettings(it, trig4Src)
                }
            }
            return trigSettings
        } ?: run {
            return null
        }
    }

    fun parseGpioTriggerSrc(gpios: Array<Gpio>?): Map<Int, GlobalGpio>? {
        if (gpios.isNullOrEmpty()) {
            return null
        }
        var gpioMap: MutableMap<Int, GlobalGpio> = mutableMapOf()
        for (gpio in gpios) {
            gpio.id?.let {
                gpioMap[it] =
                    GlobalGpio(
                        id = it,
                        digital = gpio.digital,
                        wakeup = gpio.wakeup,
                        advTrig = gpio.advTrig,
                        latch = gpio.latch,
                        maskb = gpio.maskb
                    )
            }
        }
        return gpioMap
    }

    fun parseSensorTriggerSources(triggers: Array<String>?): List<SensorTriggerSource>? {
        if (triggers.isNullOrEmpty()) {
            return null
        }
        var triggersList: MutableList<SensorTriggerSource> = mutableListOf()
        for (trigger in triggers) {
            val foundTrigger = SensorTriggerSource.fromAbr(trigger)
            foundTrigger?.let {
                triggersList.add(it)
            }
        }
        if (triggersList.isNotEmpty()) {
            return triggersList
        }
        return null
    }

    fun parsePayload(payloads: Array<Payload>?, type: String): ParsedPayload? {
        val parsedPayload = ParsedPayload(null, null, null)

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
                            val dbm: String = data
                            parsedPayload.txPower = "${Integer.parseInt(dbm, 16)}"
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

    private fun parseCustomManufacturerData(raw: String): List<ParsedDynamicData>? {
        var splitRaw: List<String> = raw.split("<").toList()
        splitRaw = splitRaw.drop(1)

        var parsedMap: MutableList<ParsedDynamicData> = mutableListOf()

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
                parsedMap.add(data)
            }
        }
        if (parsedMap.isNotEmpty()) {
            return parsedMap
        }
        return null
    }

    private fun parseIBeaconManufacturerData(raw: String): List<ParsedDynamicData>? {
        var result: MutableList<ParsedDynamicData> = mutableListOf()
        if (raw.length >= 50) {
            result.add(
                ParsedDynamicData(
                    len = 2,
                    dynamicType = DynamicDataType.IBEACON_ADDR,
                    bigEndian = false,
                    encrypted = false,
                    rawData = raw.substring(0, 8)
                )
            )
            result.add(
                ParsedDynamicData(
                    len = 16,
                    dynamicType = DynamicDataType.UUID,
                    bigEndian = false,
                    encrypted = false,
                    rawData = raw.substring(8, 40)
                )
            )
            result.add(
                ParsedDynamicData(
                    len = 2,
                    dynamicType = DynamicDataType.MAJOR,
                    bigEndian = false,
                    encrypted = false,
                    rawData = raw.substring(40, 44)
                )
            )
            result.add(
                ParsedDynamicData(
                    len = 2,
                    dynamicType = DynamicDataType.MINOR,
                    bigEndian = false,
                    encrypted = false,
                    rawData = raw.substring(44, 48)
                )
            )
            result.add(
                ParsedDynamicData(
                    len = 1,
                    dynamicType = DynamicDataType.TX_POWER,
                    bigEndian = false,
                    encrypted = false,
                    rawData = raw.substring(48, 50)
                )
            )

            return result
        }
        return null
    }
}



