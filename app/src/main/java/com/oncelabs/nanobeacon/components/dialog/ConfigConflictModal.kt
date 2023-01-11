package com.oncelabs.nanobeacon.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.oncelabs.nanobeacon.enums.ConflictItem
import com.oncelabs.nanobeacon.ui.theme.cardBackground
import com.oncelabs.nanobeacon.ui.theme.cardTextFont

@Composable
fun ConfigConflictModal(
    modifier: Modifier = Modifier,
    shouldShow: Boolean,
    onDismiss: () -> Unit = {},
    conflicts: List<ConflictItem>
) {
    if (shouldShow) {
        Dialog(
            onDismissRequest = { onDismiss() },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = modifier
                    .height(200.dp)
                    .background(cardBackground, shape = RoundedCornerShape(12.dp))
            ) {

                Column(Modifier.fillMaxWidth().padding(15.dp)) {

                    Row(
                        Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.15f)
                                .padding(3.dp),
                            text = "Configuration Conflicts",
                            style = MaterialTheme.typography.h6,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }



                    Column(
                        Modifier
                            .fillMaxWidth()
                            .weight(0.1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        for (conflict in conflicts) {
                            Text(
                                conflict.getMsg(),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
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
                    }

                }

            }
        }
    }
}

