package com.oncelabs.template.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.oncelabs.template.components.InplayTopBar
import com.oncelabs.template.components.LiveDataCard
import com.oncelabs.template.components.LogAdvertisementCard
import com.oncelabs.template.nanoBeaconLib.manager.ADXLData
import com.oncelabs.template.viewModel.LiveDataViewModel
import java.util.*

@Composable
fun LiveDataScreen() {
    LiveDataContent()
}

@Composable
fun LiveDataContent(
    liveDataViewModel: LiveDataViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    var list = listOf("ADXL367_Temp")

    val rssi = remember { mutableStateOf(liveDataViewModel.rssi.value) }
    liveDataViewModel.rssi.observe(context as LifecycleOwner){
        rssi.value = it
    }

    val temp = remember { mutableStateOf(liveDataViewModel.temp.value) }
    liveDataViewModel.temp.observe(context as LifecycleOwner){
        temp.value = it
    }

    val x = remember { mutableStateOf(liveDataViewModel.x.value)}
    liveDataViewModel.x.observe(context as LifecycleOwner){
        x.value = it
    }

    val y = remember { mutableStateOf(liveDataViewModel.y.value) }
    liveDataViewModel.y.observe(context as LifecycleOwner){
        y.value = it
    }

    var name = remember { "TEST" }
    val z = remember { mutableStateOf(liveDataViewModel.z.value) }
    liveDataViewModel.z.observe(context as LifecycleOwner){
        z.value = it
    }

    InplayTopBar(title = "Live Data")
    LazyColumn(modifier = Modifier
        .padding(bottom = 80.dp, top = 80.dp)
        .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        items(list) { it->
            Row(Modifier.fillMaxWidth()) {
                Spacer(Modifier.weight(0.02f))
                Column(Modifier.weight(0.96f)) {
                    LiveDataCard(
                        name = it,
                        rssi = rssi.value ?: listOf(),
                        temp = temp.value ?: listOf(),
                        x = x.value ?: listOf(),
                        y = y.value ?: listOf(),
                        z = z.value ?: listOf(),
                    )
                }
                Spacer(Modifier.weight(0.02f))
            }

            Spacer(Modifier.height(20.dp))

        }

    }
}