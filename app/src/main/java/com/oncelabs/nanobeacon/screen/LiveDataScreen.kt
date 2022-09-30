package com.oncelabs.nanobeacon.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.oncelabs.nanobeacon.components.InplayTopBar
import com.oncelabs.nanobeacon.components.LiveDataSheet
import com.oncelabs.nanobeacon.model.ADXL367Data
import com.oncelabs.nanobeacon.viewModel.LiveDataViewModel

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

    var test = remember {
        mutableStateOf(false)
    }

    val activeBeacons = remember { mutableStateListOf<Pair<String,List<Pair<Long, ADXL367Data>>>>() }//SnapshotStateList<BeaconChartData>() }
    liveDataViewModel.beacons.observe(context as LifecycleOwner){
        activeBeacons.clear()
        activeBeacons.addAll(it)
    }

    InplayTopBar(title = "Live Data")
    LazyColumn(modifier = Modifier
        .padding(bottom = 80.dp, top = 80.dp)
        .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        items(activeBeacons) { it->
            Row(Modifier.fillMaxWidth()) {
                Spacer(Modifier.weight(0.02f))
                Column(Modifier.weight(0.96f)) {
                    LiveDataSheet(name = it.first, data = it.second)
                }
                Spacer(Modifier.weight(0.02f))
            }
            Spacer(Modifier.height(20.dp))
        }

    }
}