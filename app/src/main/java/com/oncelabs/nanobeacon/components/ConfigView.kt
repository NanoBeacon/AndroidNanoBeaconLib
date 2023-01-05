package com.oncelabs.nanobeacon.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.oncelabs.nanobeacon.enum.*
import com.oncelabs.nanobeacon.ui.theme.*
import com.oncelabs.nanobeaconlib.enums.AdvMode
import com.oncelabs.nanobeaconlib.enums.ChannelMode
import com.oncelabs.nanobeaconlib.enums.ConfigType
import com.oncelabs.nanobeaconlib.model.GlobalGpio
import com.oncelabs.nanobeaconlib.model.ParsedConfigData

@Composable
fun ConfigView(parsedConfigData: ParsedConfigData?) {

    parsedConfigData?.let { clearedConfigData ->
        // parsedConfigData exists show config
        LazyColumn(Modifier.padding(bottom = 5.dp)) {
            item {
                //Card container
                Row(Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(0.025f))
                    Column(Modifier.weight(0.95f)) {
                        //Config Card
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .background(topBarBackground, RoundedCornerShape(10.dp))
                                .padding(8.dp)
                        ) {
                            GlobalView(clearedConfigData = clearedConfigData)
                            AdvView(clearedConfigData = clearedConfigData)
                        }
                    }
                    Spacer(Modifier.weight(0.05f))
                }
            }
        }
    } ?: run {
        // parsedConfigData does not exist show empty card
        EmptyConfigCard()
    }
}

@Composable
fun EmptyConfigCard() {
    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.weight(0.025f))
        Column(Modifier.weight(0.95f), verticalArrangement = Arrangement.Center) {
            //Config Card
            Column(
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(topBarBackground, RoundedCornerShape(10.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No configuration", style = cardTextFont)
            }
        }
        Spacer(Modifier.weight(0.025f))
    }
}

@Composable
fun AdvView(clearedConfigData: ParsedConfigData) {
    for (i in clearedConfigData.advSetData.indices) {
        var adv = clearedConfigData.advSetData[i]
        SectionTitle(title = "Advertising Set #${i + 1}")
        Row(
            Modifier.fillMaxWidth()
        ) {
            Spacer(Modifier.weight(0.05f))
            Column(Modifier.weight(0.95f)) {

                AdvDataItem(
                    title = "Advertising Data Format",
                    data = adv.ui_format.label
                )

                adv.interval?.let {
                    AdvDataItem(
                        title = "Advertising Interval",
                        data = it.toString(),
                        unit = "ms"
                    )
                }

                AdvDataItem(
                    title = "Channels",
                    data = ChannelMode.values()[adv.chCtrl].channels
                )

                adv.advModeTrigEn?.let {
                    AdvDataItem(title = "Advertising Mode", data = it.label)
                }

                adv.bdAddr?.let {
                    AdvDataItem(
                        title = "Bluetooth Address",
                        data = it,
                        prefix = ""
                    )
                }

                adv.parsedPayloadItems?.let { parsedPayload ->
                    parsedPayload.deviceName?.let {
                        AdvDataItem(title = "Device Name", data = it)
                    }

                    parsedPayload.txPower?.let {
                        AdvDataItem(title = "Tx Power", data = it)
                    }

                    AdvRandomDelayType.fromCode(adv.randDlyType)?.let {
                        AdvDataItem(
                            title = "Advertising Random Delay",
                            data = it
                        )
                    }

                    parsedPayload.manufacturerData?.let { manufacturerData ->
                        if (adv.ui_format == ConfigType.UID) {
                            SubSectionTitle(title = "Service Data Items:")
                        } else {
                            SubSectionTitle(title = "Manufacturer Data Items:")
                        }
                        Column(
                            modifier = Modifier
                                .padding(start = 12.dp)
                        ) {
                            for (dataItem in manufacturerData.toList()) {
                                when (adv.ui_format) {
                                    ConfigType.IBEACON -> {
                                        AdvDataItem(
                                            title = dataItem.dynamicType.fullName,
                                            data = dataItem.rawData,
                                            prefix = "0x"
                                        )
                                    }
                                    ConfigType.UID -> {
                                        AdvDataItem(
                                            title = dataItem.dynamicType.fullName + " (${dataItem.len} Byte)",
                                            data = dataItem.rawData,
                                            bigEndian = dataItem.bigEndian
                                                ?: false,
                                            encrypted = dataItem.encrypted
                                                ?: false,
                                            showFlags = true
                                        )
                                    }
                                    else -> {
                                        AdvDataItem(
                                            title = dataItem.dynamicType.fullName + " (${dataItem.len} Byte)",
                                            bigEndian = dataItem.bigEndian
                                                ?: false,
                                            encrypted = dataItem.encrypted
                                                ?: false,
                                            showFlags = true
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                adv.advModeTrigEn?.let { triggerMode ->
                    if (triggerMode == AdvMode.TRIGGERED) {
                        SubSectionTitle(title = "Triggered Adv Settings:")
                        Column(
                            modifier = Modifier
                                .padding(start = 12.dp)
                        ) {
                            adv.postTrigNumAdv?.let {
                                AdvDataItem(
                                    title = "Adv Event Count",
                                    data = it.toString()
                                )
                            }
                            adv.postTrigCtrlMode?.let {
                                AdvDataItem(
                                    title = "Trigger Event Reset",
                                    data = it.trigerResetsCount.toString(),
                                )
                                AdvDataItem(
                                    title = "Trigger Setting",
                                    data = it.label
                                )
                            }
                            adv.trigCheckPeriod?.let {
                                AdvDataItem(
                                    title = "Trigger Check Period",
                                    data = it.toString(),
                                    unit = "ms"
                                )
                            }

                            adv.triggers?.let {
                                for (trigger in it) {
                                    if (clearedConfigData.globalTrigSettings?.contains(trigger) == true) {
                                        val triggerData =
                                            clearedConfigData.globalTrigSettings?.get(trigger)
                                        TriggerDataItem(
                                            title = trigger.fullName,
                                            data = GlobalTriggerSourceName.fullNameFromAbrv(
                                                triggerData?.src
                                            ),
                                            triggerData?.threshold
                                        )
                                    }
                                }
                            }
                            adv.gpioTriggers?.let {
                                for (trigger in it) {
                                    if (clearedConfigData.globalGpioTriggerSrc?.contains(trigger) == true) {
                                        val triggerData =
                                            clearedConfigData.globalGpioTriggerSrc?.get(trigger)
                                        triggerData?.let {
                                            GlobalGpioTriggerItem(it)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
    }
}


@Composable
fun GlobalView(clearedConfigData: ParsedConfigData) {
    Spacer(modifier = Modifier.height(5.dp))
    //Global
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
            /*
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
             */
        }
    }
    Divider(
        color = logModalItemBackgroundColor, modifier = Modifier
            .fillMaxWidth()
            .width(1.dp)
    )
    Spacer(modifier = Modifier.height(14.dp))
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
fun GlobalGpioTriggerItem(globalGpio: GlobalGpio) {
    GlobalGpioTriggerName.nameFromId(globalGpio.id)?.let {
        Column(Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.padding(bottom = 2.dp),
                text = it,
                style = logItemTitleFont,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            SingleDataLine(title = "Digital", data = DigitalInputName.fromAbrv(globalGpio.digital))
            SingleDataLine(title = "Wakeup", data = WakeupName.fromAbrv(globalGpio.wakeup))
            SingleDataLine(
                title = "Advertisement Trigger",
                data = AdvTriggerName.fromAbrv(globalGpio.advTrig)
            )
            SingleDataLine(title = "Latch", data = LatchName.fromAbrv(globalGpio.latch))
        }
        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
fun SingleDataLine(
    title: String,
    data: String?,
) {
    data?.let {
        Row(
            modifier =
            Modifier.padding(bottom = 2.dp, top = 1.dp)
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AdvDataItem(
    title: String,
    data: String? = null,
    unit: String? = null,
    bigEndian: Boolean = false,
    encrypted: Boolean = false,
    prefix: String? = null,
    showFlags: Boolean = false
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(bottom = 2.dp),
            text = title,
            style = configTitleFont,
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
            if (showFlags) {
                var endianess = if (bigEndian) "big-endian" else "little-endian"
                var encryptedness = if (encrypted) "encrypted" else "unencrypted"
                Text(endianess, style = CustomItemSubFont)
                Spacer(Modifier.width(5.dp))
                Text(encryptedness, style = CustomItemSubFont)
            }
        }
    }
    Spacer(modifier = Modifier.height(14.dp))
}

@Composable
fun TriggerDataItem(
    title: String,
    data: String? = null,
    threshold: Int? = null
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(bottom = 2.dp),
            text = title,
            style = configTitleFont,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (!data.isNullOrBlank()) {
            Text(
                modifier = Modifier.padding(bottom = 2.dp),
                text = "$data",
                style = logTextFont,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        threshold?.let {
            Text(
                modifier = Modifier.padding(bottom = 2.dp),
                text = "$it",
                style = logTextFont,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    Spacer(modifier = Modifier.height(14.dp))
}

@Composable
fun AdvDataEntryItem(
    title: String,
    data: String? = null,
    unit: String? = null,
    bigEndian: Boolean = false,
    encrypted: Boolean = false,
    prefix: String? = null
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(bottom = 0.dp, start = 0.dp),
            text = (prefix ?: "") + title,
            style = configTitleFont,
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
    Spacer(modifier = Modifier.height(4.dp))
}


@Composable
fun SectionTitle(title: String) {
    Text(text = title, style = configurationSectionTitle)
    Spacer(modifier = Modifier.height(14.dp))
}

@Composable
fun SubSectionTitle(title: String) {
    Text(text = title, style = configurationSubSectionTitle)
    Spacer(modifier = Modifier.height(10.dp))
}