package com.oncelabs.nanobeacon.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.oncelabs.nanobeacon.R
import com.oncelabs.nanobeacon.ui.theme.logTitleFont
import com.oncelabs.nanobeacon.ui.theme.topBarBackground

@Composable
fun DetailedViewTopBar(
    title: String,
    onBackPress : () -> Unit
) {
    TopAppBar(
        modifier = Modifier.height(45.dp),
        backgroundColor = topBarBackground,
        title = {
            Box(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.fillMaxHeight().width(40.dp)
                            .padding(5.dp)
                            .clickable { onBackPress() },
                        tint = Color.White
                    )
                }
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = Color.White,
                        style = logTitleFont,
                    )
                }
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.nanobeacon_logo_white_s),
                        contentDescription = "Company logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.padding(5.dp)//.size(85.dp)
                    )
                }
            }
        },
    )
}