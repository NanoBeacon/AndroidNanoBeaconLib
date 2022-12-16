package com.oncelabs.nanobeaconlib.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.oncelabs.nanobeaconlib.enums.DynamicDataType
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconDelegate
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconInterface
import com.oncelabs.nanobeaconlib.parser.DynamicDataParsers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class NanoBeacon(
    var beaconData: NanoBeaconData? = null,
    context: Context? = null,
    delegate: NanoBeaconDelegate? = null,
    private var timeoutInterval: Float = 60f,
    override val address: String? = beaconData?.bluetoothAddress
) : NanoBeaconInterface {

    private val TAG = NanoBeacon::class.simpleName

    /* Expose flow for observing real-time advertisement data */
    private val _beaconDataFlow = MutableStateFlow<NanoBeaconData?>(null)
    override val beaconDataFlow = _beaconDataFlow.asStateFlow()

    /* Expose flow for observing real-time RSSI updates*/
    private val _rssiFlow = MutableStateFlow<Int?>(null)
    override val rssiFlow = _rssiFlow.asStateFlow()

    /* Expose flow for observing estimated advertisement interval */
    private val _estimatedAdvIntervalFlow = MutableStateFlow<Int?>(null)
    override val estimatedAdvIntervalFlow = _estimatedAdvIntervalFlow.asStateFlow()

    private val TIMESTAMP_COUNT = 10
    private var advTimestamps: MutableList<Long> = mutableListOf()

    override var matchingConfig: ParsedConfigData? = null

    private val _manufacturerData = MutableStateFlow<Map<DynamicDataType, String>>(mapOf())
    override var manufacturerData = _manufacturerData.asStateFlow()

    override fun newBeaconData(beaconData: NanoBeaconData) {
        _beaconDataFlow.value = beaconData
        _rssiFlow.value = beaconData.rssi
        updateAdvInterval(beaconData.timeStamp)
        _manufacturerData.value = processDeviceData(beaconData)
    }

    fun loadConfig(parsedConfigData: ParsedConfigData?) {
        matchingConfig = parsedConfigData
    }

    private fun updateAdvInterval(timestamp: Long) {

        if (advTimestamps.count() < TIMESTAMP_COUNT) {
            // Add new timestamp
            advTimestamps.add(timestamp)
        } else {
            // Remove oldest timestamp and add new
            advTimestamps.removeFirst()
            advTimestamps.add(timestamp)
        }

        // Compute average delta between timestamps
        if (advTimestamps.count() > 1) {
            val deltaTimestamps = mutableListOf<Long>()

            advTimestamps.indices.forEach { index ->
                if (index < advTimestamps.count() - 1) {
                    val delta = (advTimestamps[index + 1] - advTimestamps[index])
                    deltaTimestamps.add(delta)
                }
            }
            val advIntervalAvg =
                ((deltaTimestamps.sum().toFloat()) / deltaTimestamps.count().toFloat()) / 1000000f

            // Update adv interval estimate flow
            _estimatedAdvIntervalFlow.value = advIntervalAvg.toInt()

            //Log.d(TAG, "Estimated Adv Interval: $advIntervalAvg")
        }
    }

    private fun processDeviceData(data: NanoBeaconData): Map<DynamicDataType, String> {
        val map: MutableMap<DynamicDataType, String> = mutableMapOf()
        matchingConfig?.let {
            val adv = it.advSetData[0]
            adv.parsedPayloadItems?.manufacturerData?.let { manufacturerDataFlags ->
                var currentIndex = 0
                for (i in manufacturerDataFlags.toList()) {
                    val dynamicDataFlag = i.second
                    val endIndex = currentIndex + dynamicDataFlag.len
                    var dataHolder : String? = null

                    if (endIndex <= data.manufacturerData.size) {
                        val trimmedData = data.manufacturerData.copyOfRange(currentIndex, endIndex)
                        when (dynamicDataFlag.dynamicType) {
                            DynamicDataType.VCC_ITEM -> dataHolder =
                                DynamicDataParsers.processVcc(
                                    trimmedData,
                                    matchingConfig?.vccUnit ?: 0.0F,
                                    dynamicDataFlag.bigEndian ?: false,
                                ).toString()
                            DynamicDataType.TEMP_ITEM -> dataHolder =
                                DynamicDataParsers.processInternalTemp(
                                    trimmedData,
                                    matchingConfig?.tempUnit ?: 0.0F,
                                    dynamicDataFlag.bigEndian ?: false,
                                ).toString()
                            DynamicDataType.PULSE_ITEM -> dataHolder =
                                DynamicDataParsers.processWireCount(
                                    trimmedData,
                                    dynamicDataFlag.bigEndian ?: false
                                ).toString()
                            DynamicDataType.GPIO_ITEM -> dataHolder =
                                DynamicDataParsers.processGpioStatus(trimmedData).toString()
                            DynamicDataType.AON_GPIO_ITEM -> TODO()
                            DynamicDataType.EDGE_CNT_ITEM -> dataHolder =
                                DynamicDataParsers.processGpioEdgeCount(
                                    trimmedData,
                                    dynamicDataFlag.bigEndian ?: false
                                ).toString()
                            DynamicDataType.ADC_CH0_ITEM -> dataHolder =
                                DynamicDataParsers.processCh01(
                                    trimmedData,
                                    dynamicDataFlag.bigEndian ?: false
                                ).toString()
                            DynamicDataType.ADC_CH1_ITEM -> dataHolder =
                                DynamicDataParsers.processCh01(
                                    trimmedData,
                                    dynamicDataFlag.bigEndian ?: false
                                ).toString()
                            DynamicDataType.ADC_CH2_ITEM -> TODO()
                            DynamicDataType.ADC_CH3_ITEM -> TODO()
                            DynamicDataType.REG1_ITEM -> TODO()
                            DynamicDataType.REG2_ITEM -> TODO()
                            DynamicDataType.REG3_ITEM -> TODO()
                            DynamicDataType.QDEC_ITEM -> TODO()
                            DynamicDataType.TS0_ITEM -> dataHolder =
                                DynamicDataParsers.processTimeStamp(
                                    trimmedData,
                                    dynamicDataFlag.bigEndian ?: false
                                ).toString()
                            DynamicDataType.TS1_ITEM -> dataHolder =
                                DynamicDataParsers.processTimeStamp(
                                    trimmedData,
                                    dynamicDataFlag.bigEndian ?: false
                                ).toString()
                            DynamicDataType.ADVCNT_ITEM -> dataHolder =
                                DynamicDataParsers.processAdv(
                                    trimmedData,
                                    dynamicDataFlag.bigEndian ?: false
                                ).toString()
                            DynamicDataType.REG_ITEM -> TODO()
                            DynamicDataType.RANDOM_ITEM -> dataHolder =
                                DynamicDataParsers.processRandomNumber(
                                    trimmedData,
                                    dynamicDataFlag.bigEndian ?: false
                                ).toString()
                            DynamicDataType.STATIC_RANDOM_ITEM -> dataHolder =
                                DynamicDataParsers.processRandomNumber(
                                    trimmedData,
                                    dynamicDataFlag.bigEndian ?: false
                                ).toString()
                            DynamicDataType.ENCRYPT_ITEM -> TODO()
                            DynamicDataType.SALT_ITEM -> TODO()
                            DynamicDataType.TAG_ITEM -> TODO()
                            DynamicDataType.CUSTOM_PRODUCT_ID_ITEM -> dataHolder =
                                DynamicDataParsers.processRandomNumber(
                                    trimmedData,
                                    dynamicDataFlag.bigEndian ?: false
                                ).toString()
                            DynamicDataType.BLUETOOTH_DEVICE_ADDRESS_ITEM -> dataHolder =
                                DynamicDataParsers.processRandomNumber(
                                    trimmedData,
                                    dynamicDataFlag.bigEndian ?: false
                                ).toString()
                            DynamicDataType.UTF8_ITEM -> TODO()
                        }
                        currentIndex = endIndex
                    } else {
                        break
                    }
                    dataHolder?.let { processedData ->
                        map[dynamicDataFlag.dynamicType] = processedData
                    }
                }
            }

        }
        return map
    }
}
