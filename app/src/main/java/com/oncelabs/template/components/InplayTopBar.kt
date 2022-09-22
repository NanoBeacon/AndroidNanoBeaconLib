package com.oncelabs.template.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.dp
import com.oncelabs.template.R
import com.oncelabs.template.ui.theme.logTitleFont
import com.oncelabs.template.ui.theme.topBarBackground

@Composable
fun InplayTopBar(title: String) {
    TopAppBar(
        modifier = Modifier.height(75.dp),
        backgroundColor = topBarBackground,
        title = {
            Box(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = Color.White,
                        style = logTitleFont,
                    )
                }
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.inplay_logo),
                        contentDescription = "Company logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.padding(end = 5.dp).size(45.dp)
                    )
                }
            }
        },
    )
}