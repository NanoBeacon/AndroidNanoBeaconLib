package com.oncelabs.nanobeaconlib.manager

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
import com.oncelabs.nanobeaconlib.enums.BleState

import com.oncelabs.nanobeaconlib.enums.NanoBeaconEvent
import com.oncelabs.nanobeaconlib.enums.ScanState
import com.oncelabs.nanobeaconlib.extension.toHexString
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconManagerInterface
import com.oncelabs.nanobeaconlib.interfaces.CustomBeaconInterface
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconDelegate
import com.oncelabs.nanobeaconlib.model.NanoBeacon
import com.oncelabs.nanobeaconlib.model.NanoBeaconData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


object NanoBeaconManager: NanoBeaconManagerInterface, NanoBeaconDelegate {

    private var newBeaconDataFlow = MutableSharedFlow<NanoBeaconData>()
    private var registeredTypeFlow = MutableSharedFlow<NanoBeacon?>()
    private var beaconTimeoutFlow = MutableSharedFlow<NanoBeacon?>()
    private var bleStateFlow = MutableSharedFlow<BleState?>()

    private val TAG = NanoBeaconManager::class.simpleName
    private val beaconScope = CoroutineScope(Dispatchers.IO)
    private val REQUEST_ENABLE_BT = 3

    private val _scanState = MutableStateFlow<ScanState>(ScanState.UNKNOWN)
    private var scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val leDeviceMap: ConcurrentMap<String, NanoBeacon> = ConcurrentHashMap()
    private var registeredBeaconTypes: MutableList<CustomBeaconInterface> = mutableListOf()

    private lateinit var getContext: (() -> Context)
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner

    fun init(getContext: () -> Context) {
        NanoBeaconManager.getContext = getContext
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
            is NanoBeaconEvent.NewBeaconData -> {
                newBeaconDataFlow = event.flow
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
                leScanCallback
            )

        _scanState.value = ScanState.SCANNING
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
        _scanState.value = ScanState.STOPPED
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

                        // Check for exisiting entry
                        if (!leDeviceMap.containsKey(deviceAddress)){
                            // Parse scan result
                            val beaconData = NanoBeaconData(scanResult = result, leDeviceMap[deviceAddress]?.estimatedAdvIntervalFlow?.value ?: 0)
                            var nanoBeacon: NanoBeacon?
                            // Check if match for one of the registered types
                            for (beaconType in registeredBeaconTypes){
                                beaconType.isTypeMatchFor(beaconData, getContext(), this@NanoBeaconManager)?.let { customBeacon ->
                                    beaconScope.launch {
                                        nanoBeacon = customBeacon
                                        registeredTypeFlow.emit(nanoBeacon)
                                        leDeviceMap[deviceAddress] = nanoBeacon
                                        newBeaconDataFlow.emit(beaconData)
                                    }
                                }
                            }
                        // Device already present
                        } else {
                            // Parse scan result
                            val beaconData = NanoBeaconData(scanResult = result, leDeviceMap[deviceAddress]?.estimatedAdvIntervalFlow?.value ?: 0)
                            leDeviceMap[deviceAddress]?.let {
                                it.newBeaconData(beaconData = beaconData)
                                beaconScope.launch {
                                    newBeaconDataFlow.emit(beaconData)
                                }
                            }
                        }
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.d(TAG, "BLE Scan Failed with ErrorCode: $errorCode")
                _scanState.value = ScanState.FAILED
            }
        }
    }
}