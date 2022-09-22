package com.oncelabs.template.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oncelabs.template.model.Advertisement
import com.oncelabs.template.ui.theme.logTextFont

@Composable
fun LogAdvertisementCard(advertisement: Advertisement) {
    Column(Modifier.fillMaxWidth().height(250.dp)) {
        Text("${advertisement.advertisementId} Advertisement-->", style = logTextFont, maxLines = 1)
        Spacer(Modifier.height(10.dp))
        Text("BLE Address: ${advertisement.bleAddress}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("RSSI: ${advertisement.rssi}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Connectable: ${advertisement.connectable}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Manufacturer Data: ${advertisement.manufacturerData}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Transmit Power Level: ${advertisement.powerLevel}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Local Name: ${advertisement.localName}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Adv Interval: ${advertisement.advInterval}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Adv Mode: ${advertisement.advMode}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Sensor Trigger Source: ${advertisement.sensorTriggerSource}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("GPIO Trigger Source: ${advertisement.gpioTriggerSource}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Data Encryption: ${advertisement.dataEncryption}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
    val advertisement: Advertisement =
        Advertisement(
            "10:44:10.76",
            "00:01:02:03:04:05",
            "-76",
            "false",
            "0F-03-0A-07-05-09-0E",
            "6",
            "ADXL367_Temp",
            "1000ms",
            "Single Trigger",
            "Low Trigger 1",
            "GPIO2",
            "Disabled"
        )
    LogAdvertisementCard(advertisement)
}