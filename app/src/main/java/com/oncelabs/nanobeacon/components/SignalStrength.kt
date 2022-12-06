package com.oncelabs.nanobeacon.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun SignalStrength(
    modifier: Modifier,
    rawSignal: Int)
{

    fun convertRawSignalToLevel(rawSignal: Int) : Int {
        return when (rawSignal){
            in -60..-40 -> 5
            in -70..-60 -> 4
            in -80..-70 -> 3
            in -90..-80 -> 2
            else -> 1
        }
    }

    var level = convertRawSignalToLevel(rawSignal)
    val color = MaterialTheme.colors.primary

    level = if(level > 5) 5 else level
    level = if (level < 1) 1 else level

    Row(
        modifier.aspectRatio(1.3f),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        for(i in 1..5){
            Box(
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .fillMaxHeight(i / 5f)
                    .width(6.dp)
                    .background(
                        if (i <= level) color else color.copy(
                            0.25f
                        )
                    )
            )
        }
    }
}

