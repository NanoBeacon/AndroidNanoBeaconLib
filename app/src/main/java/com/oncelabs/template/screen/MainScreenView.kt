package com.oncelabs.template.screen

import androidx.compose.material.BottomNavigation
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.oncelabs.template.navigation.BottomNav
import com.oncelabs.template.navigation.Navigation

@Composable
fun MainScreenView() {
    val navController = rememberNavController()
    Scaffold(bottomBar = { BottomNav(navController = navController) }) {
        Navigation(navController)
    }
}