package com.oncelabs.nanobeacon.screen

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.oncelabs.nanobeacon.navigation.BottomNav
import com.oncelabs.nanobeacon.navigation.Navigation

@Composable
fun MainScreenView() {
    val navController = rememberNavController()
    Scaffold(bottomBar = { BottomNav(navController = navController) }) {
        Navigation(navController)
    }
}