package com.oncelabs.nanobeacon.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.ui.theme.*


@Composable
fun ProjectConfigurationModal(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    configurations: List<ConfigData>,
    openFileSelector : () -> Unit
) {


    if (isOpen) {
        Dialog(onDismissRequest = onDismiss) {
            Column(Modifier.fillMaxSize()) {
                Spacer(Modifier.weight(0.2f))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .weight(0.6f)
                        .background(color = cardBackground, shape = RoundedCornerShape(22.dp))
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .weight(0.15f)
                        ) {
                            Text("Project Configurations", style = logModalTitleFont)
                        }
                        LazyColumn(
                            Modifier
                                .fillMaxWidth()
                                .weight(0.85f)
                        ) {
                            items(configurations) {
                                configurationItem(id = it.version ?: "1.0")
                                Spacer(Modifier.height(10.dp))
                            }
                            item {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(modifier  = Modifier.clickable { openFileSelector() },text = "Add Configuration +", style = logButtonFont)
                                }
                            }

                        }


                        Column(
                            Modifier
                                .fillMaxWidth()
                                .weight(0.2f), Arrangement.Bottom
                        ) {
                            Button(
                                modifier = Modifier.fillMaxWidth().height(35.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = logModalDoneButtonColor),
                                content = {
                                    Text("Done", style = logDoneFont, color = Color.White)
                                },
                                onClick = onDismiss,
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                }
                Spacer(Modifier.weight(0.2f))
            }
        }
    }
}

@Composable
fun configurationItem(id: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = logModalItemBackgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(start = 11.dp)
    ) {
        Row(
            Modifier
                .fillMaxHeight()
                .weight(0.75f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(id, style = logModalItemFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Row(
            Modifier
                .fillMaxHeight()
                .weight(0.25f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                Icons.Default.Close,
                "Delete Item",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

