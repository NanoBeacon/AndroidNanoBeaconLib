package com.oncelabs.nanobeacon.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.oncelabs.nanobeacon.navigation.BottomNav
import com.oncelabs.nanobeacon.navigation.Navigation

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(bottomBar = { BottomNav(navController = navController) }) {
        Box(modifier =
            Modifier
                .padding(it)) {
            Navigation(navController)
        }
    }
}