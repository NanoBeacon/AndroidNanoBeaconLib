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
import com.oncelabs.template.ui.theme.*


@Composable
fun LiveDataCard(name: String) {

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
                        chartData = listOf(
                            Pair(20f, 40f),
                            Pair(50f, 50f),
                            Pair(80f, 70f),
                            Pair(90f, 40f)
                        )
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
                        chartData = listOf(
                            Pair(20f, 40f),
                            Pair(50f, 50f),
                            Pair(80f, 70f),
                            Pair(90f, 40f)
                        )
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
                            listOf(
                                Pair(2f, -1f),
                                Pair(4f, 1f),
                                Pair(6f, 2f),
                                Pair(8f, 3f),
                                Pair(10f, 4f),
                                Pair(12f, 5f),
                            ), listOf(
                                Pair(2f, -10f),
                                Pair(4f, 10f),
                                Pair(6f, 20f),
                                Pair(8f, 30f),
                                Pair(10f, 40f),
                                Pair(12f, 50f),
                            ),
                            listOf(
                                Pair(2f, -6f),
                                Pair( 4f, 4f),
                                Pair(6f, 8f),
                                Pair(8f, 11f),
                                Pair(10f, 14f),
                                Pair(12f, 20f),
                            )
                        )
                    )
                }
            }

        }
    }

}