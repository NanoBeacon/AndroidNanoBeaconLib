package com.oncelabs.nanobeacon.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.ui.theme.cardBackground
import com.oncelabs.nanobeacon.ui.theme.cardTextFont

@Composable
fun AddConfigModal(
    modifier: Modifier = Modifier,
    configData: ConfigData?,
    shouldShow: Boolean,
    onDismiss: () -> Unit = {},
    onConfirm : () -> Unit = {},
    title : String
) {

    if (shouldShow) {
        Dialog(
            onDismissRequest = {onDismiss()},
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = modifier
                    .height(200.dp)
                    .background(cardBackground, shape = RoundedCornerShape(12.dp))
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(45.dp)) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.15f)
                                .padding(3.dp),
                            text = title,
                            style = MaterialTheme.typography.h6,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.weight(0.1f))
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .weight(0.8f),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(Modifier.weight(0.15f))
                        Row() {
                           /* Text(
                                text = configData?.advSet?.get(0)?.id?.toString() ?: "N/A",
                                style = cardTextFont,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(0.2f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )*/
                        }
                        Spacer(Modifier.weight(0.13f))

                    }
                    Row() {
                        Text(
                            modifier = Modifier
                                .weight(0.5f)
                                .clickable { onDismiss() },
                            style = cardTextFont,
                            color = Color.White,
                            text = "Cancel",
                            textAlign = TextAlign.Center
                        )

                        Text(
                            modifier = Modifier
                                .weight(0.5f)
                                .clickable { onConfirm() },
                            style = cardTextFont,
                            color = Color.White,
                            text = "Confirm",
                            textAlign = TextAlign.Center
                        )

                    }
                    }

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
fun PreviewRemoveDeviceModal() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AddConfigModal(configData = null, shouldShow = true, title = "Add Config?")
    }
}