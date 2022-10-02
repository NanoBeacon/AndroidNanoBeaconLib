package com.oncelabs.nanobeacon.device

import android.content.Context
import android.util.Log
import com.oncelabs.nanobeacon.model.ADXL367Data
import com.oncelabs.nanobeacon.nanoBeaconLib.interfaces.NanoBeaconDelegate
import com.oncelabs.nanobeacon.nanoBeaconLib.interfaces.CustomBeaconInterface
import com.oncelabs.nanobeacon.nanoBeaconLib.manager.toShort
import com.oncelabs.nanobeacon.nanoBeaconLib.model.NanoBeacon
import com.oncelabs.nanobeacon.nanoBeaconLib.model.NanoBeaconData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ADXL367(
    data: NanoBeaconData? = null,
    context: Context? = null,
    delegate: NanoBeaconDelegate? = null
):NanoBeacon(
    beaconData = data,
    context = context,
    delegate = delegate
), CustomBeaconInterface {

    private val TAG = ADXL367::class.simpleName
    private val scope = CoroutineScope(Dispatchers.IO)
    private val HISTORICAL_DATA_SIZE = 15

    private val _adxlData = MutableStateFlow<ADXL367Data?>(null)
    val adxlData = _adxlData.asStateFlow()

    private val _historicalAdxlData = MutableStateFlow<List<Pair<Long, ADXL367Data>>>(mutableListOf())
    val historicalAdxlData: StateFlow<List<Pair<Long, ADXL367Data>>> = _historicalAdxlData.asStateFlow()

    private var localHistoricalADXL367Data: MutableList<Pair<Long, ADXL367Data>> = mutableListOf()

    override fun isTypeMatchFor(beaconData: NanoBeaconData, context: Context, delegate: NanoBeaconDelegate): NanoBeacon? {
        if (beaconData.name == "ADXL367_Temp"){
            return ADXL367(beaconData, context, delegate)
        }
        return null
    }

    init {
        observeBeaconData()
    }

    var beaconTimeStamp: Long? = null
    private fun observeBeaconData(){
        scope.launch {
            beaconDataFlow.collect {
                beaconTimeStamp?.let {
                    val newTimeStamp = System.currentTimeMillis()
                    Log.d(TAG, "Beacon Udpate DT: ${newTimeStamp - it}")
                    beaconTimeStamp = newTimeStamp
                } ?: run {
                    beaconTimeStamp = System.currentTimeMillis()
                }
                Log.d(TAG, "New Beacon Data")
                it?.let { beaconData ->
                    if (beaconData.manufacturerData.size == 9){

                        val processedData = processRawData(beaconData.manufacturerData)
                        processedData.rssi = beaconData.rssi
                        _adxlData.value = processedData

                        // Update list with new data
                        if (localHistoricalADXL367Data.count() < HISTORICAL_DATA_SIZE) {
                            localHistoricalADXL367Data.add(
                                Pair(System.currentTimeMillis(), processedData)
                            )
                        } else {
                            localHistoricalADXL367Data.removeAt(0)
                            localHistoricalADXL367Data.add(
                                Pair(System.currentTimeMillis(), processedData)
                            )
                        }
                        //Update timestamps to be relative to first sample
                        val relativeHistoricalData: MutableList<Pair<Long, ADXL367Data>> = mutableListOf()

                        if (localHistoricalADXL367Data.count() > 2){
                            localHistoricalADXL367Data.reversed().indices.forEach { index ->
                                Log.d(TAG, "Index: ${index}")
                                if (index < localHistoricalADXL367Data.count() - 1){
                                    relativeHistoricalData.add( Pair(
                                        localHistoricalADXL367Data.last().first - localHistoricalADXL367Data[index].first,
                                        localHistoricalADXL367Data[index].second
                                    ))
                                } else if (index == localHistoricalADXL367Data.count() - 1) {
                                    relativeHistoricalData.add( Pair(
                                        0,
                                        localHistoricalADXL367Data[index].second
                                    ))
                                }
                            }
                            val reversed = relativeHistoricalData//.reversed()
                            Log.d(TAG, "Data Processed")
                            reversed.forEach {
                                Log.d(TAG, "${it.first}, ${it.second}")
                            }
                            _historicalAdxlData.value = relativeHistoricalData.reversed().toList()
                        }
                    }
                }
            }
        }
    }

    private fun processRawData(byteArray: ByteArray): ADXL367Data {
        val x = byteArray.toShort(1).toFloat()*(245166f/1000000000f)*0.25f
        val y = byteArray.toShort(3).toFloat()*(245166f/1000000000f)*0.25f
        val z = byteArray.toShort(5).toFloat()*(245166f/1000000000f)*0.25f
        val temp = ((byteArray.toShort(7) + 1185) * (18518518f / 1000000000f) + 80).roundToInt()
        return ADXL367Data(x, y, z, temp.toFloat(), rssi = 0)
    }
}