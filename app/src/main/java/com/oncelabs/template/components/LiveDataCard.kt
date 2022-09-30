package com.oncelabs.template.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oncelabs.template.chart.LineChartView
import com.oncelabs.template.chart.MultipleLinesChartView
import com.oncelabs.template.model.ADXL367Data
import com.oncelabs.template.ui.theme.*


@Composable
fun LiveDataCard(
    name: String,
    rssi: List<Pair<Float, Float>>,
    temp: List<Pair<Float, Float>>,
    x: List<Pair<Float, Float>>,
    y: List<Pair<Float, Float>>,
    z: List<Pair<Float, Float>>
) {

    Column(
        Modifier
            .fillMaxWidth()
            .height(750.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(color = cardBackground, shape = RoundedCornerShape(8.dp))
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text(name, style = liveCardNameFont)

                Spacer(Modifier.height(23.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(25.dp)
                ) {
                    Row(
                        Modifier
                            .weight(0.40f)
                            .fillMaxHeight(),
                        Arrangement.Start,
                        Alignment.Bottom
                    ) {
                        Text("RSSI: -62", style = liveTypeFont)
                    }
                    Row(
                        Modifier
                            .weight(0.60f)
                            .fillMaxHeight(), Arrangement.End, Alignment.Bottom
                    ) {
                        Text("Interval (est) 98ms", style = liveSubTypeFont)
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    LineChartView(
                        chartData = rssi
                    )
                }

                Spacer(Modifier.height(23.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(25.dp)
                ) {
                    Row(Modifier.fillMaxSize(), Arrangement.Start, Alignment.Bottom) {
                        Text("Temperature: 73.2°F", style = liveTypeFont)
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    LineChartView(
                        chartData = temp
                    )
                }

                Spacer(Modifier.height(23.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(25.dp)
                ) {
                    Row(Modifier.fillMaxSize(), Arrangement.Start, Alignment.Bottom) {
                        Text("Acceleration", style = liveTypeFont)
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    MultipleLinesChartView(
                        chartData = listOf(
                            x, y, z
                        )
                    )
                }
            }

        }
    }

}

@Composable
fun LiveDataSheet(
    name: String,
    data: List<Pair<Long, ADXL367Data>>,
) {

    Column(
        Modifier
            .fillMaxWidth()
            .height(750.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(color = cardBackground, shape = RoundedCornerShape(8.dp))
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text(name, style = liveCardNameFont)

                Spacer(Modifier.height(23.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(25.dp)
                ) {
                    Row(
                        Modifier
                            .weight(0.40f)
                            .fillMaxHeight(),
                        Arrangement.Start,
                        Alignment.Bottom
                    ) {
                        Text("RSSI: -62", style = liveTypeFont)
                    }
                    Row(
                        Modifier
                            .weight(0.60f)
                            .fillMaxHeight(), Arrangement.End, Alignment.Bottom
                    ) {
                        Text("Interval (est) 98ms", style = liveSubTypeFont)
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    LineChartView(
                        chartData = data.map { Pair(it.first.toFloat(), it.second.rssi.toFloat()) }
                    )
                }

                Spacer(Modifier.height(23.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(25.dp)
                ) {
                    Row(Modifier.fillMaxSize(), Arrangement.Start, Alignment.Bottom) {
                        Text("Temperature: 73.2°F", style = liveTypeFont)
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    LineChartView(
                        chartData = data.map { Pair(it.first.toFloat(), it.second.temp.toFloat()) }
                    )
                }

                Spacer(Modifier.height(23.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(25.dp)
                ) {
                    Row(Modifier.fillMaxSize(), Arrangement.Start, Alignment.Bottom) {
                        Text("Acceleration", style = liveTypeFont)
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    MultipleLinesChartView(
                        chartData = listOf(
                            data.map { Pair(it.first.toFloat(), it.second.xAccel.toFloat()) },
                            data.map { Pair(it.first.toFloat(), it.second.yAccel.toFloat()) },
                            data.map { Pair(it.first.toFloat(), it.second.zAccel.toFloat()) }
                        )
                    )
                }
            }

        }
    }

}