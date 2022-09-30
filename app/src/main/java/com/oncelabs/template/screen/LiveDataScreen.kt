package com.oncelabs.template.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.Snapshot
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
import com.oncelabs.template.components.LiveDataSheet
import com.oncelabs.template.components.LogAdvertisementCard
import com.oncelabs.template.device.ADXL367
import com.oncelabs.template.model.ADXL367Data
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