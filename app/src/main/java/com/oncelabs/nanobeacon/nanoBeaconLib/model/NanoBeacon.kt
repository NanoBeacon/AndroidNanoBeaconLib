package com.oncelabs.nanobeacon.nanoBeaconLib.model

import android.content.Context
import android.util.Log
import com.oncelabs.nanobeacon.nanoBeaconLib.interfaces.NanoBeaconDelegate
import com.oncelabs.nanobeacon.nanoBeaconLib.interfaces.NanoBeaconInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class NanoBeacon(
    var beaconData: NanoBeaconData? = null,
    context: Context? = null,
    delegate: NanoBeaconDelegate? = null,
    private var timeoutInterval: Float = 60f,
    override val address: String? = beaconData?.bluetoothAddress
): NanoBeaconInterface {

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

    override fun newBeaconData(beaconData: NanoBeaconData) {
        _beaconDataFlow.value = beaconData
        _rssiFlow.value = beaconData.rssi
        updateAdvInterval(beaconData.timeStamp)
    }

    private fun updateAdvInterval(timestamp: Long){

        if (advTimestamps.count() < TIMESTAMP_COUNT){
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
                if (index < advTimestamps.count() - 1 ){
                    deltaTimestamps.add((advTimestamps[index + 1] - advTimestamps[index]))
                }
            }
            val advIntervalAvg = (deltaTimestamps.sum())/deltaTimestamps.count()

            // Update adv interval estimate flow
            _estimatedAdvIntervalFlow.value = advIntervalAvg.toInt()

            Log.d(TAG, "Estimated Adv Interval: $advIntervalAvg")
        }
    }
}
