package com.oncelabs.nanobeacon.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.oncelabs.nanobeacon.screen.LiveDataScreen
import com.oncelabs.nanobeacon.screen.ScannerScreen

/**
 * Define all associated [Composable] Screens with their given [Screen]
 */
@ExperimentalMaterialApi
@Composable
fun Navigation(navController : NavHostController) {
    NavHost(navController = navController, startDestination = Screen.ScannerScreen.route) {
        composable(route = Screen.ScannerScreen.route) {
            ScannerScreen()
        }

        composable(route = Screen.LiveDataScreen.route) {
            LiveDataScreen()
        }
    }
}