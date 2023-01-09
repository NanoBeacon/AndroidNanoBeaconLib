package com.oncelabs.nanobeaconlib.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.oncelabs.nanobeaconlib.enums.AdvMode
import com.oncelabs.nanobeaconlib.enums.ConfigType
import com.oncelabs.nanobeaconlib.enums.DynamicDataType
import com.oncelabs.nanobeaconlib.extension.toHexString
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconDelegate
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconInterface
import com.oncelabs.nanobeaconlib.manager.NanoNotificationManager
import com.oncelabs.nanobeaconlib.manager.NanoNotificationService
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

    private val _matchingConfig = MutableStateFlow<ParsedConfigData?>(null)
    override val matchingConfig = _matchingConfig.asStateFlow()

    private val _parsedData = MutableStateFlow<List<ProcessedDataAdv>>(listOf())
    override var parsedData = _parsedData.asStateFlow()

    private var check = false

    override fun newBeaconData(beaconData: NanoBeaconData) {
        _beaconDataFlow.value = beaconData
        _rssiFlow.value = beaconData.rssi
        updateAdvInterval(beaconData.timeStamp)
        val processed = processDeviceData(beaconData)
        if (processed.isNotEmpty()) {
            _parsedData.value = processed
        }
    }

    fun loadConfig(parsedConfigData: ParsedConfigData?) {
        _matchingConfig.value = parsedConfigData
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

        }
    }

    private fun processDeviceData(data: NanoBeaconData): List<ProcessedDataAdv> {
        val list: MutableList<ProcessedDataAdv> = mutableListOf()
        matchingConfig.value?.let {

            for (adv in it.advSetData) {
                val stagedList : MutableList<ProcessedData> = mutableListOf()
                if (adv.advModeTrigEn == AdvMode.TRIGGERED) {
                    NanoNotificationManager.submitNotification(adv.id)
                }
                adv.parsedPayloadItems?.manufacturerData?.let { manufacturerDataFlags ->
                    var serviceDataSize = 0
                    if (data.serviceData?.isNotEmpty() == true) {
                        serviceDataSize = data.serviceData!!.toList()[0].second.size
                    }
                    var currentIndex = 0
                    for (i in manufacturerDataFlags.toList()) {
                        val endIndex = currentIndex + i.len
                        var dataHolder: String? = null
                        if (endIndex <= data.manufacturerData.size || endIndex <= serviceDataSize) {
                            var trimmedData: ByteArray = byteArrayOf()
                            if ((adv.ui_format == ConfigType.UID || adv.ui_format == ConfigType.TLM) && serviceDataSize > 0) {

                                trimmedData =
                                    data.serviceData?.toList()?.get(0)?.second?.copyOfRange(
                                        currentIndex,
                                        endIndex
                                    )
                                        ?: byteArrayOf()
                            } else {
                                if (data.manufacturerData.size >= endIndex) {
                                    trimmedData =
                                        data.manufacturerData.copyOfRange(currentIndex, endIndex)
                                }
                            }
                            if (trimmedData.isNotEmpty()) {
                                when (i.dynamicType) {
                                    DynamicDataType.VCC_ITEM -> dataHolder = if (adv.ui_format != ConfigType.TLM) {
                                        DynamicDataParsers.processVcc(
                                            trimmedData,
                                            matchingConfig.value?.vccUnit ?: 0.0F,
                                            i.bigEndian ?: false,
                                        ).toString() } else {
                                        (DynamicDataParsers.processTLMVcc(trimmedData, i.bigEndian ?: false)).toString()
                                        }
                                    DynamicDataType.TEMP_ITEM -> dataHolder =
                                        DynamicDataParsers.processInternalTemp(
                                            trimmedData,
                                            matchingConfig.value?.tempUnit ?: 0.0F,
                                            i.bigEndian ?: false,
                                        ).toString()
                                    DynamicDataType.PULSE_ITEM -> dataHolder =
                                        DynamicDataParsers.processWireCount(
                                            trimmedData,
                                            i.bigEndian ?: false
                                        ).toString()
                                    DynamicDataType.GPIO_ITEM -> dataHolder =
                                        DynamicDataParsers.processGpioStatus(trimmedData)
                                    DynamicDataType.AON_GPIO_ITEM -> TODO()
                                    DynamicDataType.EDGE_CNT_ITEM -> dataHolder =
                                        DynamicDataParsers.processGpioEdgeCount(
                                            trimmedData,
                                            i.bigEndian ?: false
                                        ).toString()
                                    DynamicDataType.ADC_CH0_ITEM -> dataHolder =
                                        DynamicDataParsers.processCh01(
                                            trimmedData,
                                            i.bigEndian ?: false
                                        ).toString()
                                    DynamicDataType.ADC_CH1_ITEM -> dataHolder =
                                        DynamicDataParsers.processCh01(
                                            trimmedData,
                                            i.bigEndian ?: false
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
                                            i.bigEndian ?: false,
                                            multiplier = 100
                                        ).toString()
                                    DynamicDataType.TS1_ITEM -> dataHolder =
                                        DynamicDataParsers.processTimeStamp(
                                            trimmedData,
                                            i.bigEndian ?: false
                                        ).toString()
                                    DynamicDataType.ADVCNT_ITEM -> dataHolder =
                                        DynamicDataParsers.processAdv(
                                            trimmedData,
                                            i.bigEndian ?: false
                                        ).toString()
                                    DynamicDataType.REG_ITEM -> TODO()
                                    DynamicDataType.RANDOM_ITEM -> dataHolder =
                                        DynamicDataParsers.processRandomNumber(
                                            trimmedData,
                                            i.bigEndian ?: false
                                        ).toString()
                                    DynamicDataType.STATIC_RANDOM_ITEM -> dataHolder =
                                        DynamicDataParsers.processRandomNumber(
                                            trimmedData,
                                            i.bigEndian ?: false
                                        ).toString()
                                    DynamicDataType.ENCRYPT_ITEM -> TODO()
                                    DynamicDataType.SALT_ITEM -> TODO()
                                    DynamicDataType.TAG_ITEM -> TODO()
                                    DynamicDataType.CUSTOM_PRODUCT_ID_ITEM -> dataHolder =
                                        DynamicDataParsers.processCustomerProductID(
                                            trimmedData,
                                            i.bigEndian ?: false
                                        ).toString()
                                    DynamicDataType.BLUETOOTH_DEVICE_ADDRESS_ITEM -> dataHolder =
                                        DynamicDataParsers.processBluetoothDeviceAddress(
                                            trimmedData,
                                            i.bigEndian ?: false
                                        ).toString()
                                    DynamicDataType.UTF8_ITEM -> TODO()
                                    DynamicDataType.UUID -> {
                                        dataHolder =
                                            DynamicDataParsers.processIBeaconUUID(trimmedData)
                                    }
                                    DynamicDataType.MAJOR -> {
                                        dataHolder =
                                            DynamicDataParsers.processMajor(trimmedData)
                                    }
                                    DynamicDataType.MINOR -> {
                                        dataHolder =
                                            DynamicDataParsers.processMinor(trimmedData)
                                    }
                                    DynamicDataType.TX_POWER -> {
                                        dataHolder =
                                            DynamicDataParsers.processIBeaconTxPower(trimmedData)
                                                .toString()
                                    }
                                    DynamicDataType.IBEACON_ADDR -> {}
                                    DynamicDataType.EDDYSTONE_NAMESPACE -> {
                                        data.serviceData?.let { dataMaps ->
                                            dataHolder =
                                                DynamicDataParsers.processEddystoneNamespace(
                                                    trimmedData
                                                )
                                        }
                                    }
                                    DynamicDataType.EDDYSTONE_INSTANCE -> {
                                        data.serviceData?.let { dataMaps ->
                                            dataHolder =
                                                DynamicDataParsers.processEddystoneInstance(
                                                    trimmedData
                                                )
                                        }
                                    }
                                    DynamicDataType.EDDYSTONE_PREFIX -> {}
                                    DynamicDataType.EDDYSTONE_POSTFIX -> {}
                                }
                            }
                            currentIndex = endIndex
                        } else {
                            break
                        }
                        dataHolder?.let { processedData ->
                            stagedList.add(
                                ProcessedData(
                                    dynamicDataType = i.dynamicType,
                                    processedData = processedData,
                                    bigEndian = i.bigEndian,
                                    encrypted = i.encrypted
                                )
                            )
                        }
                    }
                }
                list.add(ProcessedDataAdv(adv.ui_format,stagedList))
            }
        }
        return list.toList()
    }
}
