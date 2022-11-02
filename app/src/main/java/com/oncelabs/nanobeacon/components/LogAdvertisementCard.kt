package com.oncelabs.nanobeacon.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oncelabs.nanobeacon.ui.theme.*
import java.util.*
import kotlin.random.Random

@Composable
fun LogAdvertisementCard(data: BeaconDataEntry) {
    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(topBarBackground, RoundedCornerShape(10.dp))
            .padding(8.dp)
        ){
        Text("====================================", style = logItemSeparatorFont, maxLines = 1)
        DataLine(title = "New Advertisement", data = data.timestamp, maxLines = 1)
        Text("====================================", style = logItemSeparatorFont, maxLines = 1)
        DataLine(title = "Address", data = data.address, maxLines = 1)
        DataLine(title = "RSSI", data = data.rssi, maxLines = 1)
        DataLine(title = "Estimated Adv Interval", data = "${data.advInterval}ms", maxLines = 1)
        DataLine(title = "Manufacturer Data", data = data.manufacturerData, maxLines = 2, separateData = true)
        DataLine(title = "Manufacturer ID", data = data.manufacturerId, maxLines = 1)
        DataLine(title = "Company", data = data.company, maxLines = 2, separateData = false)
        DataLine(title = "Transmit Power Level", data = data.txPower, maxLines = 1)
        DataLine(title = "Local Name", data = data.localName, maxLines = 1)
        DataLine(title = "Flags", data = data.flags, maxLines = 1)
        DataLine(title = "TX Power Observed", data = data.txPowerObserved, maxLines = 1)
        DataLine(title = "Primary Phy", data = data.primaryPhy, maxLines = 1)
        DataLine(title = "Secondary Phy", data = data.secondaryPhy, maxLines = 1)
        DataLine(title = "Raw", data = data.rawData, maxLines = 3, separateData = true)
        Text("====================================", style = logItemSeparatorFont, maxLines = 1)
        // TODO: Not used?
        //Text("Sensor Trigger Source: ${advertisement.sensorTriggerSource}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        //Text("GPIO Trigger Source: ${advertisement.gpioTriggerSource}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        //Text("Data Encryption: ${advertisement.dataEncryption}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun DataLine(
    title: String,
    data: String,
    maxLines: Int,
    separateData: Boolean = false
) {
    if (!separateData || data.isEmpty()){
        Row(modifier =
            Modifier
                .padding(bottom = 2.dp, top = 1.dp)
        ) {
            Text(
                if (title.isNotEmpty()) "$title:  " else "",
                style = logItemTitleFont,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                if (data.isNotEmpty()) data else "Not Set",
                style = logTextFont,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        Column(modifier =
            Modifier
                //.padding(bottom = 1.dp, top = 1.dp)
        ) {
            Text(
                if (title.isNotEmpty()) "$title:  " else "",
                style = logItemTitleFont,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (data.isNotEmpty()) {
                Text(
                    data,
                    style = logTextFont,
                    maxLines = maxLines,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Text(
                    "Not Set",
                    style = logTextFont,
                    maxLines = maxLines,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Testable model for BeaconData so we can use Previews
 */
data class BeaconDataEntry(
    val address: String,
    val timestamp: String,
    val rssi: String,
    val advInterval: String,
    val manufacturerData: String,
    val manufacturerId: String,
    val company: String,
    val txPower: String,
    val localName: String,
    val flags: String,
    val txPowerObserved: String,
    val primaryPhy: String,
    val secondaryPhy: String,
    val searchableString: String,
    val rawData:String
) {
    companion object {
        fun getRandomBeaconDataEntry(): BeaconDataEntry {
            return BeaconDataEntry(
                address = "${Random.nextDouble(1000.0, 9999.0)}",
                timestamp = Date().time.toString(),
                rssi = "${Random.nextInt(200)}",
                advInterval = "${Random.nextInt(200)}",
                manufacturerData = "${Random.nextInt(200)}",
                manufacturerId = "${Random.nextInt(200)}",
                company = "Your Company",
                txPower = "${Random.nextInt(200)}",
                localName = "${Random.nextInt(200)}",
                flags = "${Random.nextInt(200)}",
                txPowerObserved = "${Random.nextInt(200)}",
                primaryPhy = "${Random.nextInt(200)}",
                secondaryPhy = "${Random.nextInt(200)}",
                searchableString = "",
                rawData = "0x0000"
            )
        }
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
    Column {
        LogAdvertisementCard(BeaconDataEntry.getRandomBeaconDataEntry())
    }
}