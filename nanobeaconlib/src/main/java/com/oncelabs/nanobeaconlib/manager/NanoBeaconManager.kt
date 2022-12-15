package com.oncelabs.nanobeaconlib.manager

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.oncelabs.nanobeaconlib.enums.BleState
import com.oncelabs.nanobeaconlib.enums.NanoBeaconEvent
import com.oncelabs.nanobeaconlib.enums.ScanState
import com.oncelabs.nanobeaconlib.interfaces.CustomBeaconInterface
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconDelegate
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconManagerInterface
import com.oncelabs.nanobeaconlib.model.NanoBeacon
import com.oncelabs.nanobeaconlib.model.NanoBeaconData
import com.oncelabs.nanobeaconlib.model.ParsedAdvertisementData
import com.oncelabs.nanobeaconlib.model.ParsedConfigData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

object NanoBeaconManager : NanoBeaconManagerInterface, NanoBeaconDelegate {

    private var newBeaconDataFlow = MutableSharedFlow<NanoBeaconData>()
    private var newBeaconFlow = MutableSharedFlow<NanoBeacon>()
    private var registeredTypeFlow = MutableSharedFlow<NanoBeacon?>()
    private var beaconTimeoutFlow = MutableSharedFlow<NanoBeacon>()
    private var bleStateFlow = MutableSharedFlow<BleState?>()

    private val TAG = NanoBeaconManager::class.simpleName
    private val beaconScope = CoroutineScope(Dispatchers.IO)
    private val REQUEST_ENABLE_BT = 3

    private val _scanState = MutableStateFlow(ScanState.UNKNOWN)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val leDeviceMap: ConcurrentMap<String, NanoBeacon> = ConcurrentHashMap()
    private var registeredBeaconTypes: MutableList<CustomBeaconInterface> = mutableListOf()

    private var context: WeakReference<Context>? = null
    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var currentConfig: ParsedConfigData? = null

    fun init(getContext: WeakReference<Context>) {
        context = getContext
        bluetoothManager =
            (getContext.get()?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)
        bluetoothAdapter = bluetoothManager?.adapter
        bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
        setupBluetoothAdapterStateHandler()
        requestBluetoothEnable()
    }

    fun loadConfiguration(parsedConfigData: ParsedConfigData) {
        currentConfig = parsedConfigData
        refresh()
    }

    fun requestBluetoothEnable() {
        bluetoothAdapter?.let { adapter ->
            if (!adapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.get()?.let {
                    if (ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.BLUETOOTH_CONNECT
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
                    Log.d(TAG, "Requesting BLE to be turned on")
                    it.startActivity(enableBtIntent)
                }
            } else {
                Log.d(TAG, "BLE is turned on. No need to request again.")
            }
        } ?: run {
            Log.d(TAG, "Cannot request BLE enable. Bluetooth adapter is null")
        }
    }

    override fun refresh() {
        stopScanning()
        leDeviceMap.clear()
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
            is NanoBeaconEvent.NewBeacon -> {
                newBeaconFlow = event.flow
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

    @SuppressLint("MissingPermission")
    override fun startScanning() {
        if (_scanState.value == ScanState.SCANNING) return
        context?.get()?.let {
            if (ActivityCompat.checkSelfPermission(
                    it,
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
        bluetoothAdapter?.let { adapter ->
            if (!adapter.isEnabled) {
                return
            }
            bluetoothLeScanner
                ?.startScan(
                    scanFilters,
                    scanSettings,
                    leScanCallback
                )

            _scanState.value = ScanState.SCANNING
            Log.d(TAG, "Starting scan")
        } ?: run {
            Log.d(TAG, "Cannot start scanning. Bluetooth adapter is null")
        }
    }

    @SuppressLint("MissingPermission")
    override fun stopScanning() {
        context?.get()?.let {
            if (ActivityCompat.checkSelfPermission(
                    it,
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
        bluetoothLeScanner?.stopScan(leScanCallback)
        _scanState.value = ScanState.STOPPED
        Log.d(TAG, "Starting scan")
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
                        BluetoothAdapter.STATE_OFF -> {
                            Log.d(TAG, "BluetoothAdapter State: Off")
                            beaconScope.launch {
                                bleStateFlow.emit(BleState.UNAVAILABLE)
                            }
                        }
                        BluetoothAdapter.STATE_TURNING_OFF ->
                            Log.d(TAG, "BluetoothAdapter State: Turning off")
                        BluetoothAdapter.STATE_ON -> {
                            Log.d(TAG, "BluetoothAdapter State: On")
                            beaconScope.launch {
                                bleStateFlow.emit(BleState.AVAILABLE)
                            }
                        }
                        BluetoothAdapter.STATE_TURNING_ON ->
                            Log.d(TAG, "BluetoothAdapter State: Turning on")
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context?.get()?.let {
            (it).registerReceiver(bluetoothAdapterStateReceiver, filter)
        }
    }

    private fun checkAdvMatch(bdAddr : String) : ParsedConfigData? {
        val cleanedbdAddr = bdAddr.replace(":", "")

        val configMatch = currentConfig?.advSetData?.firstOrNull {
            it.bdAddr == cleanedbdAddr }
        configMatch?.let { it ->
            val parsedConfigData = currentConfig
            parsedConfigData?.advSetData = arrayOf(it)
            return parsedConfigData
        }
        return null
    }

    private val leScanCallback: ScanCallback by lazy {
        object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                context?.get()?.let { context ->
                    result?.device?.address?.let { deviceAddress ->

                        // Check for exisiting entry
                        if (!leDeviceMap.containsKey(deviceAddress)) {

                            // Parse scan result
                            val beaconData =
                                NanoBeaconData(
                                    scanResult = result,
                                    leDeviceMap[deviceAddress]?.estimatedAdvIntervalFlow?.value ?: 0
                                )

                            var nanoBeacon: NanoBeacon? = null
                            // Check if match for one of the registered types
                            for (beaconType in registeredBeaconTypes) {
                                beaconType.isTypeMatchFor(
                                    beaconData,
                                    context,
                                    this@NanoBeaconManager
                                )?.let { customBeacon ->
                                    nanoBeacon = customBeacon

                                    //Check for advertisement match and drop irrelevant advertisements
                                    checkAdvMatch(beaconData.bluetoothAddress)?.let { parsedConfigData ->
                                        nanoBeacon?.loadConfig(parsedConfigData)
                                    }

                                    leDeviceMap[deviceAddress] = nanoBeacon
                                    beaconScope.launch {
                                        registeredTypeFlow.emit(nanoBeacon)
                                    }
                                    beaconScope.launch {
                                        newBeaconFlow.emit(customBeacon)
                                    }
                                }
                            }
                            nanoBeacon?.let {} ?: run {
                                nanoBeacon = NanoBeacon(
                                    beaconData,
                                    context,
                                    this@NanoBeaconManager
                                )

                                //Check for advertisement match and drop irrelevant advertisements
                                checkAdvMatch(beaconData.bluetoothAddress)?.let { parsedConfigData ->
                                    nanoBeacon?.loadConfig(parsedConfigData)
                                }

                                leDeviceMap[deviceAddress] = nanoBeacon
                                beaconScope.launch {
                                    newBeaconDataFlow.emit(beaconData)
                                    nanoBeacon?.let { nb ->
                                        newBeaconFlow.emit(nb)
                                    }
                                }
                            }
                            // Device already present
                        } else {
                            // Parse scan result
                            val beaconData =
                                NanoBeaconData(
                                    scanResult = result,
                                    leDeviceMap[deviceAddress]?.estimatedAdvIntervalFlow?.value ?: 0
                                )

                            leDeviceMap[deviceAddress]?.let {

                                // Pass new adv data to NanoBeacon Instance
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