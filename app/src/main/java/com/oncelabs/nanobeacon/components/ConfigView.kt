package com.oncelabs.nanobeacon.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.oncelabs.nanobeacon.ui.theme.*
import com.oncelabs.nanobeaconlib.enums.ConfigType
import com.oncelabs.nanobeaconlib.model.ParsedConfigData

@Composable
fun ConfigView(parsedConfigData: ParsedConfigData?) {
    parsedConfigData?.let { clearedConfigData ->
        LazyColumn(Modifier.padding(bottom = 5.dp)) {
            item {
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
                            Spacer(modifier = Modifier.height(5.dp))
                            SectionTitle(title = "Global")
                            Row(Modifier.fillMaxWidth()) {
                                Spacer(Modifier.weight(0.05f))
                                Column(Modifier.weight(0.95f)) {
                                    clearedConfigData.txPower?.let {
                                        GlobalDataItem(
                                            title = "Tx Power Level",
                                            data = it.toString(),
                                            unit = "dBm"
                                        )
                                    }

                                    clearedConfigData.sleepAftTx?.let {
                                        GlobalDataItem(
                                            title = "Sleep After Tx",
                                            data = it.toString(),
                                            unit = ""
                                        )
                                    }

                                    GlobalDataItem(
                                        title = "On-Chip Temperature Unit",
                                        data = clearedConfigData.tempUnit.toString(),
                                        unit = "C"
                                    )
                                    GlobalDataItem(
                                        title = "On-Chip VCC Unit",
                                        data = clearedConfigData.vccUnit.toString(),
                                        unit = "V"
                                    )

                                    val channels =
                                        "" + (clearedConfigData.ch0
                                            ?: "") + " " + (clearedConfigData.ch1
                                            ?: "") + " " + (clearedConfigData.ch2 ?: "")
                                    if (channels.isNotBlank()) {
                                        GlobalDataItem(
                                            title = "Channels",
                                            data = channels,
                                            unit = ""
                                        )
                                    }
                                }
                            }
                            for (i in clearedConfigData.advSetData.indices) {
                                var adv = clearedConfigData.advSetData[i]
                                SectionTitle(title = "Advertising Set #${i + 1}")
                                Row(Modifier.fillMaxWidth()) {
                                    Spacer(Modifier.weight(0.05f))
                                    Column(Modifier.weight(0.95f)) {

                                        AdvDataItem(
                                                title = "UI Format",
                                                data = adv.ui_format.label
                                        )

                                        adv.interval?.let {
                                            AdvDataItem(
                                                title = "Advertising Interval",
                                                data = it.toString()
                                            )
                                        }

                                        adv.advModeTrigEn?.let {
                                            AdvDataItem(title = "Advertising Mode", data = it.label)
                                        }

                                        adv.bdAddr?.let {
                                            AdvDataItem(
                                                title = "Bluetooth Address",
                                                data = it,
                                                prefix = "0x"
                                            )
                                        }

                                        adv.parsedPayloadItems?.let { parsedPayload ->
                                            parsedPayload.deviceName?.let {
                                                AdvDataItem(title = "Device Name", data = it)
                                            }

                                            parsedPayload.txPower?.let {
                                                AdvDataItem(title = "Tx Power", data = it)
                                            }
                                            parsedPayload.manufacturerData?.let { manufacturerData ->
                                                for (dataItem in manufacturerData.toList()) {
                                                    if (adv.ui_format == ConfigType.IBEACON) {
                                                        AdvDataItem(
                                                            title = dataItem.first.fullName,
                                                            data = dataItem.second.rawData
                                                        )
                                                    } else {
                                                        AdvDataItem(title = dataItem.first.fullName,
                                                            bigEndian = dataItem.second.bigEndian
                                                                ?: false,
                                                            encrypted = dataItem.second.encrypted
                                                                ?: false
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                    Spacer(Modifier.weight(0.025f))
                }
            }

        }
    } ?: run {

    }
}

@Composable
fun GlobalDataItem(title: String, data: String, unit: String?) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(bottom = 2.dp),
            text = title,
            style = logItemTitleFont,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier.padding(bottom = 2.dp),
            text = "$data $unit",
            style = logTextFont,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
    Spacer(modifier = Modifier.height(14.dp))
}

@Composable
fun AdvDataItem(
    title: String,
    data: String? = null,
    unit: String? = null,
    bigEndian: Boolean = false,
    encrypted: Boolean = false,
    prefix: String? = null
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(bottom = 2.dp),
            text = title,
            style = logItemTitleFont,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (!data.isNullOrBlank()) {
            Text(
                modifier = Modifier.padding(bottom = 2.dp),
                text = "${prefix ?: ""}$data ${unit ?: ""}",
                style = logTextFont,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(Modifier.fillMaxWidth()) {
            if (bigEndian) {
                Text("big-endian", style = CustomItemSubFont)
                Spacer(Modifier.width(5.dp))
            }
            if (encrypted) {
                Text("encrypted", style = CustomItemSubFont)
            }
        }
    }
    Spacer(modifier = Modifier.height(14.dp))
}


@Composable
fun SectionTitle(title: String) {
    Text(text = title, style = logCardTitleAccentFont)
    Spacer(modifier = Modifier.height(14.dp))
}