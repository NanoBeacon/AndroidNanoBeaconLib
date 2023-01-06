package com.oncelabs.nanobeacon.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oncelabs.nanobeacon.ui.theme.*
import com.oncelabs.nanobeaconlib.enums.ConfigType
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconInterface
import java.util.*


@Composable
fun DetailedViewCard(beacon: NanoBeaconInterface) {

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
                            Text(
                                text = beacon.matchingConfig.value?.advSetData?.get(0)?.ui_format?.title
                                    ?: ConfigType.NOT_RECOGNIZED.title,
                                style = logCardTitleAccentFont
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    CustomDataLine(title = "Timestamp", data = it.timeStampFormatted, maxLines = 1)
                    CustomDataLine(title = "RSSI", data = it.rssi.toString(), maxLines = 1)
                    CustomDataLine(
                        title = "Estimated",
                        data = it.estimatedAdvInterval.toString(),
                        maxLines = 1
                    )


                    when (beacon.matchingConfig.value?.advSetData?.get(0)?.ui_format
                        ?: ConfigType.NOT_RECOGNIZED) {
                        ConfigType.CUSTOM -> {
                            CustomTypeView(beacon = beacon, data = data)
                        }
                        ConfigType.EDDYSTONE -> {}
                        ConfigType.IBEACON -> {
                            IBeaconTypeView(beacon = beacon)
                        }
                        ConfigType.NOT_RECOGNIZED -> {}
                        ConfigType.UID -> {
                            UIDTypeView(beacon = beacon)
                        }
                        ConfigType.TLM -> {
                            TLMTypeView(beacon = beacon)
                        }
                    }

                }

            }
            Spacer(Modifier.weight(0.025f))
        }
    }
}

@Composable
fun CustomDataLine(
    title: String,
    data: String?,
    maxLines: Int,
    separateData: Boolean = false
) {
    // Hide unset properties
    if (data.isNullOrEmpty()) {
        return
    }

    if (!separateData || data.isEmpty()) {
        Row(
            modifier =
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
                data.ifEmpty { "Not Set" },
                style = logTextFont,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        Column(
            modifier =
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

@Composable
fun CustomTypeView(beacon: NanoBeaconInterface, data: BeaconDataEntry) {

    val matchingConfig by beacon.matchingConfig.collectAsState()
    CustomDataLine(title = "Device Name", data = data.localName, maxLines = 1)
    CustomDataLine(title = "TX Power", data = data.txPower, maxLines = 1)
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
                            title = item.dynamicDataType.fullName,
                            data = item.processedData + item.dynamicDataType.units,
                            bigEndian = item.bigEndian,
                            encrypted = item.encrypted
                        )
                    })
                }
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}

@Composable
fun IBeaconTypeView(beacon: NanoBeaconInterface) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Row() {
            beacon.let { nanoBeaconInterface ->
                val beaconData by nanoBeaconInterface.manufacturerData.collectAsState()
                LazyColumn(Modifier.fillMaxWidth()) {
                    items(items = beaconData.toList(), itemContent = { item ->
                        if (item.dynamicDataType.displayToUser) {
                            CustomDataLine(
                                title = item.dynamicDataType.fullName,
                                data = item.processedData.uppercase() + item.dynamicDataType.units,
                                maxLines = 1
                            )
                        }
                    })
                }
                Spacer(Modifier.height(14.dp))
            }
        }

    }
}

@Composable
fun UIDTypeView(beacon: NanoBeaconInterface) {
    Spacer(modifier = Modifier.height(2.dp))
    Divider(thickness = 1.dp, color = logModalDoneButtonColor)
    Spacer(modifier = Modifier.height(2.dp))
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "Service Data", style = logCardTitleAccentFont)
    }
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Row() {
            beacon.let { nanoBeaconInterface ->
                val beaconData by nanoBeaconInterface.manufacturerData.collectAsState()
                LazyColumn(Modifier.fillMaxWidth()) {
                    items(items = beaconData.toList(), itemContent = { item ->
                        CustomDataLine(
                            title = item.dynamicDataType.fullName,
                            data = item.processedData.uppercase() + item.dynamicDataType.units,
                            maxLines = 1
                        )
                    })
                }
                Spacer(Modifier.height(14.dp))
            }
        }

    }
}

@Composable
fun TLMTypeView(beacon: NanoBeaconInterface) {
    Spacer(modifier = Modifier.height(2.dp))
    Divider(thickness = 1.dp, color = logModalDoneButtonColor)
    Spacer(modifier = Modifier.height(2.dp))
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "Service Data", style = logCardTitleAccentFont)
    }
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Row() {
            beacon.let { nanoBeaconInterface ->
                val beaconData by nanoBeaconInterface.manufacturerData.collectAsState()
                LazyColumn(Modifier.fillMaxWidth()) {
                    items(items = beaconData.toList(), itemContent = { item ->
                        CustomDataLine(
                            title = item.dynamicDataType.fullName,
                            data = item.processedData.uppercase() + " ${item.dynamicDataType.units}",
                            maxLines = 1
                        )
                    })
                }
                Spacer(Modifier.height(14.dp))
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