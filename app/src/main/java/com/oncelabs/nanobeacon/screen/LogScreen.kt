package com.oncelabs.nanobeacon.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oncelabs.nanobeacon.components.InplayTopBar
import com.oncelabs.nanobeacon.components.LogAdvertisementCard
import com.oncelabs.nanobeacon.components.ProjectConfigurationModal
import com.oncelabs.nanobeacon.model.Advertisement
import com.oncelabs.nanobeacon.ui.theme.logFloatingButtonColor
import com.oncelabs.nanobeacon.viewModel.LiveDataViewModel
import com.oncelabs.nanobeacon.viewModel.LogViewModel

@Composable
fun LogScreen(){
    LogScreenContent()

}

@Composable
fun LogScreenContent(
    logDataViewModel: LogViewModel = hiltViewModel()
) {

    val beaconDataLog by logDataViewModel.beaconDataLog.observeAsState()

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
        items(beaconDataLog ?: listOf()) { it ->
            Row(Modifier.fillMaxWidth()) {
                Spacer(Modifier.weight(0.1f))
                Column(Modifier.weight(0.8f)) {
                    LogAdvertisementCard(beaconData = it)
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