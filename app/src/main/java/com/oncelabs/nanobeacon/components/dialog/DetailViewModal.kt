package com.oncelabs.nanobeacon.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.oncelabs.nanobeacon.components.DataLine
import com.oncelabs.nanobeacon.components.DetailedViewCard
import com.oncelabs.nanobeacon.components.DetailedViewTopBar
import com.oncelabs.nanobeacon.ui.theme.cardBackground
import com.oncelabs.nanobeacon.ui.theme.cardTextFont
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconInterface

@Composable
fun DetailViewModal(
    modifier: Modifier = Modifier,
    shouldShow : Boolean,
    onDismiss : () -> Unit = {},
    beacon: NanoBeaconInterface?
) {
    beacon?.let {
        if (shouldShow) {

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                DetailedViewTopBar(title = "Detailed View") { onDismiss() }
                Spacer(Modifier.height(7.dp))
                DetailedViewCard(beacon = beacon)

            }
        }
    }
}