package com.oncelabs.nanobeacon.components

import android.bluetooth.le.ScanResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oncelabs.nanobeacon.model.Advertisement
import com.oncelabs.nanobeacon.nanoBeaconLib.extension.toHexString
import com.oncelabs.nanobeacon.nanoBeaconLib.model.NanoBeaconData
import com.oncelabs.nanobeacon.ui.theme.logTextFont

@Composable
fun LogAdvertisementCard(beaconData: NanoBeaconData) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(250.dp)) {
        Text("<-----------Advertisement------------->", style = logTextFont, maxLines = 1)
        Text(beaconData.timeStampFormatted, style = logTextFont, maxLines = 1)
        Spacer(Modifier.height(10.dp))
        Text("BLE Address: ${beaconData.bluetoothAddress}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("RSSI: ${beaconData.rssi}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Estimated Adv Interval: ${beaconData.estimatedAdvInterval}ms", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Manufacturer Data: ${beaconData.manufacturerData.toHexString().uppercase()}", style = logTextFont, maxLines = 2, overflow = TextOverflow.Visible)
        Text("Manufacturer ID: ${beaconData.manufacturerId.toHexString()}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Transmit Power Level: ${beaconData.txPowerClaimed}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Local Name: ${beaconData.name}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Flags: ${beaconData.flags}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("TX Power Observed: ${beaconData.transmitPowerObserved}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Primary Phy: ${beaconData.primaryPhy}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Secondary Phy: ${beaconData.secondaryPhy}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        //Text("Sensor Trigger Source: ${advertisement.sensorTriggerSource}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        //Text("GPIO Trigger Source: ${advertisement.gpioTriggerSource}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        //Text("Data Encryption: ${advertisement.dataEncryption}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

}

@Preview
@Preview(name = "NEXUS_7", device = Devices.NEXUS_7)
@Preview(name = "NEXUS_5", device = Devices.NEXUS_5)
@Preview(name = "PIXEL_C", device = Devices.PIXEL_C)
@Preview(name = "PIXEL_2", device = Devices.PIXEL_2)
@Preview(name = "PIXEL_4", device = Devices.PIXEL_4)
@Preview(name = "PIXEL_4_XL", device = Devices.PIXEL_4_XL)
@Composable
fun LogAdvertisementCardPreview() {
//    val advertisement: NanoBeaconData = NanoBeaconData(
//        ScanResult(
//
//        )
//    )

    //LogAdvertisementCard(advertisement)
}