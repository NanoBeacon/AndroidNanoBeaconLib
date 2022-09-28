package com.oncelabs.template.nanoBeaconLib.manager

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import android.util.SparseArray
import androidx.core.app.ActivityCompat
import com.oncelabs.template.nanoBeaconLib.enums.BleState

import com.oncelabs.template.nanoBeaconLib.enums.NanoBeaconEvent
import com.oncelabs.template.nanoBeaconLib.enums.ScanState
import com.oncelabs.template.nanoBeaconLib.interfaces.NanoBeaconManagerInterface
import com.oncelabs.template.nanoBeaconLib.interfaces.CustomBeaconInterface
import com.oncelabs.template.nanoBeaconLib.interfaces.NanoBeaconDelegate
import com.oncelabs.template.nanoBeaconLib.model.NanoBeacon
import com.oncelabs.template.nanoBeaconLib.model.NanoBeaconData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

data class ADXLData(
    var xAccel: Float,
    var yAccel: Float,
    var zAccel: Float,
    var temp: Float,
    var rssi: Int
)

object NanoBeaconManager: NanoBeaconManagerInterface, NanoBeaconDelegate {

    private var registeredTypeFlow = MutableSharedFlow<NanoBeacon?>()
    private var beaconTimeoutFlow = MutableSharedFlow<NanoBeacon?>()
    private var bleStateFlow = MutableSharedFlow<BleState?>()

    private var _adxlData = MutableStateFlow<ADXLData>(ADXLData(0f,0f,0f,0f, 0))
    public var adxlData = _adxlData.asStateFlow()

    private val TAG = NanoBeaconManager::class.simpleName
    private val beaconScope = CoroutineScope(Dispatchers.IO)
    private val REQUEST_ENABLE_BT = 3

    private var scanState = ScanState.IDLE
    private val leDeviceMap: ConcurrentMap<String, NanoBeacon> = ConcurrentHashMap()
    private var registeredBeaconTypes: MutableList<CustomBeaconInterface> = mutableListOf()

    private lateinit var getContext: (() -> Context)
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner

    fun init(getContext: () -> Context) {
        this.getContext = getContext
        bluetoothManager = (getContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        setupBluetoothAdapterStateHandler()
        if (!bluetoothAdapter.isEnabled){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            NanoBeaconManager.getContext.let {
                (it() as Activity).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        }

        on(NanoBeaconEvent.BeaconDidTimeout(flow = beaconTimeoutFlow))
        on(NanoBeaconEvent.DiscoveredRegisteredType(flow = registeredTypeFlow))
        on(NanoBeaconEvent.BleStateChange(flow = bleStateFlow))

    }

    fun on(event: NanoBeaconEvent) {

        when (event) {
            is NanoBeaconEvent.DiscoveredRegisteredType -> {
                registeredTypeFlow = event.flow
            }
            is NanoBeaconEvent.BeaconDidTimeout -> {
                beaconTimeoutFlow = event.flow
            }
            is NanoBeaconEvent.BleStateChange -> {
                bleStateFlow = event.flow
            }
        }
    }

    private val scanSettings: ScanSettings by lazy {
        ScanSettings.Builder()
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES) // Report all advertisements
            .setLegacy(false) // Report legacy in addition to extended
            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE) // Report matches even when signal level may be low
            .setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT) // Report as many advertisments as possible
            .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED) // Use all available PHYs
            .setReportDelay(0)// Deliver results immediately
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // Use highest duty cycle
            .build() // Finished
    }

    private val scanFilters: MutableList<ScanFilter> by lazy {
        val _scanFilters = mutableListOf<ScanFilter>(
            ScanFilter.Builder()
                //.setDeviceName(BLINKY_DEVICE_NAME)
                .build(),
            ScanFilter.Builder()
                //.setServiceUuid(ParcelUuid(BLINKY_SERVICE_UUID))
                .build()
        )
        _scanFilters
    }

    override fun register(customBeacon: CustomBeaconInterface) {
        registeredBeaconTypes.add(customBeacon)
    }

    override fun startScanning(){
        getContext.let {
            if (ActivityCompat.checkSelfPermission(
                    it(),
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
        }

        bluetoothLeScanner
            .startScan(
                scanFilters,
                scanSettings,
                this.leScanCallback)
    }

    override fun stopScanning() {
        getContext.let {
            if (ActivityCompat.checkSelfPermission(
                    it(),
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
        }
        bluetoothLeScanner.stopScan(leScanCallback)
    }

    private fun setupBluetoothAdapterStateHandler() {

        val bluetoothAdapterStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                // Verify the action matches what we are looking for
                if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {

                    val previousState = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_PREVIOUS_STATE,
                        BluetoothAdapter.ERROR
                    )

                    val currentState = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR
                    )

                    when (currentState) {
                        BluetoothAdapter.STATE_OFF ->
                            Log.d(TAG, "BluetoothAdapter State: Off")
                        BluetoothAdapter.STATE_TURNING_OFF ->
                            Log.d(TAG, "BluetoothAdapter State: Turning off")
                        BluetoothAdapter.STATE_ON -> {
                            Log.d(TAG, "BluetoothAdapter State: On")
                        }
                        BluetoothAdapter.STATE_TURNING_ON ->
                            Log.d(TAG, "BluetoothAdapter State: Turning on")
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        getContext.let {
            (it() as Activity).registerReceiver(bluetoothAdapterStateReceiver, filter)
        }
    }

    private val leScanCallback: ScanCallback by lazy {
        object : ScanCallback() {

            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                getContext.let {
                    result?.device?.address?.let { deviceAddress ->
                        if (!leDeviceMap.containsKey(deviceAddress)){
                            val beaconData = NanoBeaconData(scanResult = result)
                            val nanoBeacon = NanoBeacon(beaconData, it(), this@NanoBeaconManager)
                            for (beaconType in registeredBeaconTypes){
                                if (beaconType.isTypeMatchFor(beaconData)){
                                    beaconScope.launch {
                                        registeredTypeFlow.emit(nanoBeacon)
                                    }
                                }
                            }
                            leDeviceMap[deviceAddress] = nanoBeacon
                        } else {

                        }
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.d(TAG, "BLE Scan Failed with ErrorCode: $errorCode")
            }
        }
    }

    private fun toString(array: SparseArray<ByteArray?>?): String? {
        if (array == null) {
            return "null"
        }
        if (array.size() == 0) {
            return ""
        }
        val buffer = StringBuilder()

        Log.d(TAG, "Manufacturer Sparse Count ${array.size()}")
        for (i in 0 until array.size()) {
            buffer.append( String.format("%02x", array.keyAt(i)))
            val a = array.valueAt(i)
            a?.let {
                it.forEach { byte ->
                    buffer.append(String.format("%02x", byte))
                }
            }
        }
        return buffer.toString()
    }

    private fun toByteArrat(array: SparseArray<ByteArray?>?): ByteArray? {
        if (array == null) {
            return null
        }
        if (array.size() == 0) {
            return null
        }
        var buffer = byteArrayOf()

        Log.d(TAG, "Manufacturer Sparse Count ${array.size()}")
        for (i in 0 until array.size()) {
            //buffer += array.keyAt(i)
            val manufacturerId = array.keyAt(i)
            val a = array.valueAt(i)
            a?.let {
                it.forEach { byte ->
                    buffer +=  byte
                }
            }
        }
        return buffer
    }
}

fun ByteArray.toShort(start: Int = 0, endian: ByteOrder = ByteOrder.BIG_ENDIAN): Short {
    this.size.takeIf { it >= 2 } ?: return 0
    return ByteBuffer.wrap(this.copyOfRange(start, start + 2)).order(endian).short
}


//                    if (it == "06:05:04:03:02:01"){
//                        toByteArrat(record?.manufacturerSpecificData)?.let {
//                            Log.d(TAG, "$it")
//                            val x = (it.toShort(1).toFloat()*(245166f/1000000000f)*0.25)
//                            val y = it.toShort(3).toFloat()*(245166f/1000000000f)*0.25
//                            val z = it.toShort(5).toFloat()*(245166f/1000000000f)*0.25
//                            val rssi = result.rssi
//                            val temp = (it.toShort(7) + 1185) * 18518518
//                            Log.d(TAG, "X: $x, Y: $y, Z: $z, TEMP: $temp")
//                            _adxlData.value = ADXLData(x.toFloat(), y.toFloat(), z.toFloat(), 0f, rssi)
//                        }

//                        Log.d(TAG, """
//                        BLE ScanResult
//                        Periodic Advertising Interval: ${result?.periodicAdvertisingInterval}
//                        Primary Phy: ${result?.primaryPhy}
//                        RSSI: ${result?.rssi}
//                        Secondary PHY: ${result?.secondaryPhy}
//                        Timestamp: ${result?.timestampNanos}
//                        TX Power: ${if (result?.txPower == ScanResult.TX_POWER_NOT_PRESENT) "NOT PRESENT" else result?.txPower}
//                        Connectable: ${result?.isConnectable}
//                        Legacy: ${result?.isLegacy}
//                        Device Address: ${result?.device?.address}
//                        """.trimIndent())

//                        var rawAdvBytes: String = ""
//                        record?.bytes?.let { bytes ->
//                            for (b in bytes){
//                                rawAdvBytes += String.format("%02x", b)
//                            }
//                        }
//
//                        var manufactureDataBytes: String = ""
//                        toString(record?.manufacturerSpecificData)?.let {
//                            manufactureDataBytes = it
//                        }

//                        Log.d(TAG, """
//                        BLE ScanRecord
//                        Advertisement Raw Bytes: $rawAdvBytes
//                        Advertisement Flags: ${record?.advertiseFlags}
//                        Device Name: ${record?.deviceName}
//                        Manufacturer Specific Data: $manufactureDataBytes
//                        Service Data: ${record?.serviceData}
//                        Service Solicitation UUIDs: ${record?.serviceSolicitationUuids}
//                        Service UUIDs: ${record?.serviceUuids}
//                        """.trimIndent())
//   }