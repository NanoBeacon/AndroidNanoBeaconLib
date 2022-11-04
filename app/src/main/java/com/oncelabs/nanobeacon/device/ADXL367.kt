package com.oncelabs.nanobeacon.device

import android.content.Context
import android.util.Log
import com.oncelabs.nanobeacon.manager.NotificationService
import com.oncelabs.nanobeacon.model.ADXL367Data
import com.oncelabs.nanobeaconlib.extension.toHexString
import com.oncelabs.nanobeaconlib.extension.toShort
import com.oncelabs.nanobeaconlib.interfaces.CustomBeaconInterface
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconDelegate
import com.oncelabs.nanobeaconlib.model.NanoBeacon
import com.oncelabs.nanobeaconlib.model.NanoBeaconData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.experimental.and

class ADXL367(
    data: NanoBeaconData? = null,
    val context: Context? = null,
    delegate: NanoBeaconDelegate? = null
): NanoBeacon(
    beaconData = data,
    context = context,
    delegate = delegate
), CustomBeaconInterface {

    private val TAG = ADXL367::class.simpleName
    private val scope = CoroutineScope(Dispatchers.IO)

    private val HISTORICAL_DATA_SIZE = 15

    private val _adxlAwake = MutableStateFlow<Boolean>(false)
    val adxAwake = _adxlAwake.asStateFlow()

    private val _adxlData = MutableStateFlow<ADXL367Data?>(null)
    val adxlData = _adxlData.asStateFlow()

    private val _historicalAdxlData = MutableStateFlow<List<Pair<Long, ADXL367Data>>>(mutableListOf())
    val historicalAdxlData: StateFlow<List<Pair<Long, ADXL367Data>>> = _historicalAdxlData.asStateFlow()

    private var localHistoricalADXL367Data: MutableList<Pair<Long, ADXL367Data>> = mutableListOf()

    override fun isTypeMatchFor(beaconData: NanoBeaconData, context: Context, delegate: com.oncelabs.nanobeaconlib.interfaces.NanoBeaconDelegate): NanoBeacon? {
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
                        // Update timestamps to be relative to first sample
                        val relativeHistoricalData: MutableList<Pair<Long, ADXL367Data>> = mutableListOf()
                        if (localHistoricalADXL367Data.count() > 2){
                            localHistoricalADXL367Data.indices.forEach { index ->
                                //Log.d(TAG, "Index: ${index}")
                                if (index < localHistoricalADXL367Data.count() - 1){
                                    relativeHistoricalData.add( Pair(
                                        localHistoricalADXL367Data.first().first - localHistoricalADXL367Data[index].first,
                                        localHistoricalADXL367Data[index].second
                                    ))
                                } else if (index == localHistoricalADXL367Data.count() - 1) {
                                    relativeHistoricalData.add( Pair(
                                        0,
                                        localHistoricalADXL367Data[index].second
                                    ))
                                }
                            }
                            _historicalAdxlData.value = relativeHistoricalData.reversed().toList()
                        }
                    }
                }
            }
        }
    }

    private fun processRawData(byteArray: ByteArray): ADXL367Data {
        val status = byteArray[0]
        val awake = status and 0b01000000
        _adxlAwake.value = awake.toInt() == 64
        val inactive = status and 0b00100000
        val active = status and 0b00010000
        val dataRead = status and 0b00000001
        val x = byteArray.toShort(1).toFloat()*(245166f/1000000000f)*0.25f
        val y = byteArray.toShort(3).toFloat()*(245166f/1000000000f)*0.25f
        val z = byteArray.toShort(5).toFloat()*(245166f/1000000000f)*0.25f
        val tempRawShort = byteArray.toShort(7).toInt()/4
        val temp = (((tempRawShort + 1185) * (185_185_18f / 1_000_000_000f) * 2)*100).toInt().toFloat()/100
        Log.d(TAG, "Awake: $awake, Inactive: $inactive, Active: $active, Data Read: $dataRead, Temp: $temp, Data Ready: ${byteArray.toHexString("")}")
        if (_adxlAwake.value){
            context?.let {
                NotificationService.startService(context = it, "ADXL367 Alert", "Device Awake", true)
            }
        }
        return ADXL367Data(x, y, z, temp, rssi = 0)
    }
}

// Status:

// Bit 7: ERR_USER_REGS
/* SEU Error Detect. A 1 indicates one of two conditions: either an SEU event, such as an alpha
particle of a power glitch, has disturbed a user register setting or the ADXL367 is not configured. This
bit is high on both startup and soft reset, and resets as soon as any register write commands are
performed.*/

// Bit 6: AWAKE
/* Indicates whether the accelerometer is in an active (AWAKE = 1) or inactive (AWAKE = 0) state, based
on the activity and inactivity functionality. To enable autosleep, activity and inactivity detection must
be in linked mode or loop mode (LINKLOOP bits in the ACT_INACT_CTL register). Otherwise, this bit
defaults to 1 and must be ignored.
0x1 R
0: device is inactive.
1: device is active (reset state).*/

// Bit 5: INACT
/* Inactivity. A 1 indicates that the inactivity detection function has detected an inactivity or a free fall
condition.*/

// Bit 4: ACT
/*Activity. A 1 indicates that the activity detection function has detected an overthreshold condition. */