package com.oncelabs.nanobeacon.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oncelabs.nanobeacon.ui.theme.*
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconInterface


@Composable
fun CustomDetailViewCard(beacon: NanoBeaconInterface) {

    val beaconData by beacon.beaconDataFlow.collectAsState()

    beaconData?.let {

        val data = formatToEntry(it)
        Row(Modifier.fillMaxWidth()) {
            Spacer(Modifier.weight(0.025f))
            Column(Modifier.weight(0.95f)) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(topBarBackground, RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SignalStrength(
                            modifier = Modifier.size(32.dp),
                            rawSignal = data.rssi.toIntOrNull() ?: -127
                        )
                        Spacer(modifier = Modifier.weight(.05f))
                        Row {
                            Text(text = "Custom", style = logCardTitleAccentFont)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    DataLine(title = "Device Name", data = data.localName, maxLines = 1)
                    DataLine(title = "TX Power", data = data.txPower, maxLines = 1)
                    Spacer(modifier = Modifier.height(2.dp))
                    Divider(thickness = 1.dp, color = logModalDoneButtonColor)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Manufacturer Data", style = logCardTitleAccentFont)
                    }
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row() {
                            beacon?.let { nanoBeaconInterface ->
                                val beaconData by nanoBeaconInterface.manufacturerData.collectAsState()
                                LazyColumn(Modifier.fillMaxWidth()) {
                                    items(items = beaconData.toList(), itemContent = { item ->
                                        Spacer(modifier = Modifier.height(14.dp))
                                        CustomDataItem(
                                            title = item.first.abrName,
                                            data = item.second,
                                            bigEndian = nanoBeaconInterface?.matchingConfig?.advSetData?.get(
                                                0
                                            )?.parsedPayloadItems?.manufacturerData?.get(item.first)?.bigEndian,
                                            encrypted =nanoBeaconInterface?.matchingConfig?.advSetData?.get(
                                                    0
                                                    )?.parsedPayloadItems?.manufacturerData?.get(item.first)?.encrypted
                                        )
                                    })
                                }
                                Spacer(Modifier.height(14.dp))
                            }
                        }

                    }

                    // TODO: Not used?
//            Text("Sensor Trigger Source: ${advertisement.sensorTriggerSource}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
//            Text("GPIO Trigger Source: ${advertisement.gpioTriggerSource}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
//            Text("Data Encryption: ${advertisement.dataEncryption}", style = logTextFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

            }
            Spacer(Modifier.weight(0.025f))
        }
    }
}

@Composable
fun CustomDataItem(
    title: String?,
    data: String?,
    bigEndian: Boolean?,
    encrypted: Boolean?
) {

    Column(modifier = Modifier) {
        Text(
            modifier = Modifier.padding(bottom = 2.dp),
            text = title ?: "",
            style = logItemTitleFont,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier.padding(bottom = 2.dp),
            text = data ?: "Not yet implemented",
            style = logTextFont,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(Modifier.fillMaxWidth()) {
            if (bigEndian == true) {
                Text("big-endian", style = CustomItemSubFont)
                Spacer(Modifier.width(5.dp))
            }
            if (encrypted == true) {
                Text("encrypted", style = CustomItemSubFont)
            }
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
fun DetailViewCardPreview() {
    Column {
//        LogAdvertisementCard(
//            beacon =
//        )
    }
}