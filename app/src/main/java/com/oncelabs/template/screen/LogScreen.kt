package com.oncelabs.template.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.oncelabs.template.components.InplayTopBar
import com.oncelabs.template.components.LogAdvertisementCard
import com.oncelabs.template.components.ProjectConfigurationModal
import com.oncelabs.template.model.Advertisement
import com.oncelabs.template.ui.theme.logFloatingButtonColor

@Composable
fun LogScreen(){
    LogScreenContent()

}

@Composable
fun LogScreenContent() {
    val advertisement: Advertisement =
        Advertisement(
            "10:44:10.76",
            "00:01:02:03:04:05",
            "-76",
            "false",
            "0F-03-0A-07-05-09-0E",
            "6",
            "ADXL367_Temp",
            "1000ms",
            "Single Trigger",
            "Low Trigger 1",
            "GPIO2",
            "Disabled"
        )

    val advertisementTwo: Advertisement =
        Advertisement(
            "10:44:10.76",
            "00:01:02:03:04:05",
            "-76",
            "false",
            "0F-03-0A",
            "6",
            "ADXL367_Temp",
            "1000ms",
            "Single Trigger",
            "Low Trigger 1",
            "GPIO2",
            "Disabled"
        )
    val holderList : List<Advertisement> = listOf(advertisement, advertisementTwo, advertisement)
    val modalIsOpen = remember { mutableStateOf(false)}

    InplayTopBar("Log")

    LazyColumn(modifier = Modifier
        .padding(bottom = 80.dp, top = 80.dp)
        .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        items(holderList) { it ->
            Row(Modifier.fillMaxWidth()) {
                Spacer(Modifier.weight(0.1f))
                Column(Modifier.weight(0.8f)) {
                    LogAdvertisementCard(advertisement = it)
                }
                Spacer(Modifier.weight(0.1f))
            }
            Spacer(Modifier.height(20.dp))
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp, end = 20.dp), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.End) {
        FloatingActionButton(onClick = { modalIsOpen.value = true }, backgroundColor = logFloatingButtonColor, contentColor = Color.White) {
            Icon(Icons.Default.FilterAlt, "filter Settings", modifier = Modifier.size(36.dp))
        }
    }

    ProjectConfigurationModal(isOpen = modalIsOpen.value, {
        modalIsOpen.value = false
    }, listOf("deneineffefe", "fenjfnjfewfdfsfwgwgwgarhqbebeERGahtghaq", "efw", "wfe", "wef", "wef", "Wef"))
}