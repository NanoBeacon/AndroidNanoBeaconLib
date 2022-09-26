package com.oncelabs.template.nanoBeaconLib

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
import android.os.ParcelUuid
import android.util.Log
import android.util.SparseArray
import androidx.core.app.ActivityCompat
import com.oncelabs.template.nanoBeaconLib.enums.ScanState

class NanoBeaconManagerImpl(val context: Context): NanoBeaconManager {

    private val TAG = NanoBeaconManagerImpl::class.simpleName
    private val REQUEST_ENABLE_BT = 3

    private var scanState = ScanState.IDLE

    private val bluetoothManager = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        bluetoothManager.adapter
    }

    private val bluetoothLeScanner: BluetoothLeScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
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

    init {
        setupBluetoothAdapterStateHandler()
        if (!bluetoothAdapter.isEnabled){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            (context as Activity).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    override fun startScanning(){
        if (ActivityCompat.checkSelfPermission(
                context,
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

        bluetoothLeScanner
            .startScan(
                scanFilters,
                scanSettings,
                this.leScanCallback)
    }

    override fun stopScanning() {
        TODO("Not yet implemented")
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
        (context as Activity).registerReceiver(bluetoothAdapterStateReceiver, filter)
    }

    private val leScanCallback: ScanCallback by lazy {
        object : ScanCallback() {

            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                val record = result?.scanRecord

                result?.device?.address?.let {
                    if (it == "06:05:04:03:02:01"){
                        Log.d(TAG, """
                        BLE ScanResult
                        Periodic Advertising Interval: ${result?.periodicAdvertisingInterval}
                        Primary Phy: ${result?.primaryPhy}
                        RSSI: ${result?.rssi}
                        Secondary PHY: ${result?.secondaryPhy}
                        Timestamp: ${result?.timestampNanos}
                        TX Power: ${if (result?.txPower == ScanResult.TX_POWER_NOT_PRESENT) "NOT PRESENT" else result?.txPower}
                        Connectable: ${result?.isConnectable}
                        Legacy: ${result?.isLegacy}
                        Device Address: ${result?.device?.address}
                        """.trimIndent())

                        var rawAdvBytes: String = ""
                        record?.bytes?.let { bytes ->
                            for (b in bytes){
                                rawAdvBytes += String.format("%02x", b)
                            }
                        }

                        var manufactureDataBytes: String = ""
                        toString(record?.manufacturerSpecificData)?.let {
                            manufactureDataBytes = it
                        }

                        Log.d(TAG, """
                        BLE ScanRecord
                        Advertisement Raw Bytes: $rawAdvBytes
                        Advertisement Flags: ${record?.advertiseFlags}
                        Device Name: ${record?.deviceName}
                        Manufacturer Specific Data: $manufactureDataBytes
                        Service Data: ${record?.serviceData}
                        Service Solicitation UUIDs: ${record?.serviceSolicitationUuids}
                        Service UUIDs: ${record?.serviceUuids}
                        """.trimIndent())
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



}