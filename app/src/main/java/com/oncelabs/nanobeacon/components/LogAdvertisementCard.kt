package com.oncelabs.nanobeacon.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oncelabs.nanobeacon.ui.theme.*
import com.oncelabs.nanobeaconlib.extension.toHexString
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconInterface
import com.oncelabs.nanobeaconlib.model.NanoBeaconData
import java.util.*
import kotlin.random.Random

/**
 * Format the beacon class to a usable model for the view
 * @param nanoBeaconData the incoming beacon
 * @return [BeaconDataEntry] formatted for view
 */
internal fun formatToEntry(nanoBeaconData: NanoBeaconData): BeaconDataEntry {
    return BeaconDataEntry(
        address = nanoBeaconData.bluetoothAddress,
        timestamp = nanoBeaconData.timeStampFormatted,
        rssi = "${nanoBeaconData.rssi}",
        advInterval = "${nanoBeaconData.estimatedAdvInterval}",
        manufacturerData = nanoBeaconData.manufacturerData.toHexString("-").uppercase(),
        manufacturerId = nanoBeaconData.manufacturerId,
        company = nanoBeaconData.company,
        txPower = "${nanoBeaconData.txPowerClaimed}",
        localName = nanoBeaconData.name,
        flags = nanoBeaconData.flags,
        txPowerObserved = "${nanoBeaconData.transmitPowerObserved}",
        searchableString = nanoBeaconData.searchableString,
        rawData = nanoBeaconData.raw?.uppercase() ?: ""
    )
}

@Composable
fun LogAdvertisementCard(beacon: NanoBeaconInterface) {

    val beaconData by beacon.beaconDataFlow.collectAsState()
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    beaconData?.let {

        val data = formatToEntry(it)

        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(topBarBackground, RoundedCornerShape(10.dp))
                .padding(8.dp)
                .clickable {
                    isExpanded = !isExpanded
                }
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 0.dp, bottom = if (isExpanded) 10.dp else 0.dp)
                    .wrapContentHeight()
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SignalStrength(modifier = Modifier.size(32.dp), rawSignal = data.rssi.toIntOrNull() ?: -127)
                Spacer(modifier = Modifier.weight(.1f))
                Row {
                    Text(text = "Address: ", style = logCardTitleAccentFont)
                    Text(text = data.address, style = logCardTitleFont)
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (!isExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropUp,
                    contentDescription = "Expand Button",
                    tint = Color.White,
                    modifier = Modifier
                        .clickable {
                            isExpanded = !isExpanded
                        }
                )
            }
            DataLine(title = "Local Name", data = data.localName, maxLines = 1)
            DataLine(title = "Timestamp", data = data.timestamp, maxLines = 1)
            DataLine(title = "RSSI", data = data.rssi, maxLines = 1)
            DataLine(title = "Estimated Adv Interval", data = "${data.advInterval}ms", maxLines = 1)
            AnimatedVisibility(visible = isExpanded) {
                Column() {
                    DataLine(title = "Transmit Power Level", data = data.txPower, maxLines = 1)
                    DataLine(title = "Flags", data = data.flags, maxLines = 1)
                    DataLine(title = "TX Power Observed", data = data.txPowerObserved, maxLines = 1)
                    DataLine(title = "Company", data = data.company, maxLines = 2, separateData = false)
                    DataLine(title = "Manufacturer ID", data = data.manufacturerId, maxLines = 1)
                    DataLine(
                        title = "Manufacturer Data",
                        data = data.manufacturerData,
                        maxLines = 2,
                        separateData = true
                    )
                    DataLine(title = "Raw", data = data.rawData, maxLines = 3, separateData = true)
                }
            }
            // TODO: Not used?
//            Text("Sensor Trigger Source: ${advertisement.sensorTriggerSource}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
//            Text("GPIO Trigger Source: ${advertisement.gpioTriggerSource}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
//            Text("Data Encryption: ${advertisement.dataEncryption}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}


@Composable
fun DataLine(
    title: String,
    data: String,
    maxLines: Int,
    separateData: Boolean = false,
    isTitle: Boolean = false
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
                    modifier = Modifier.padding(bottom = 2.dp),
                    text = data,
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
//        LogAdvertisementCard(
//            beacon =
//        )
    }
}