package com.oncelabs.template.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oncelabs.template.components.InplayTopBar
import com.oncelabs.template.components.LiveDataCard
import com.oncelabs.template.components.LogAdvertisementCard

@Composable
fun LiveDataScreen() {
    LiveDataContent()
}

@Composable
fun LiveDataContent() {

    val list : List<String> = listOf("ADXL367_Temp", "ADXL367_Temp")

    InplayTopBar(title = "Live Data")
    LazyColumn(modifier = Modifier
        .padding(bottom = 80.dp, top = 80.dp)
        .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        items(list) { it->
            Row(Modifier.fillMaxWidth()) {
                Spacer(Modifier.weight(0.02f))
                Column(Modifier.weight(0.96f)) {
                    LiveDataCard(name = it)
                }
                Spacer(Modifier.weight(0.02f))
            }

            Spacer(Modifier.height(20.dp))

        }

    }
}