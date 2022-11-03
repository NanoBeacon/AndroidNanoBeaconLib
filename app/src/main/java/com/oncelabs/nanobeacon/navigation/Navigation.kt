package com.oncelabs.nanobeacon.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.oncelabs.nanobeacon.screen.LiveDataScreen
import com.oncelabs.nanobeacon.screen.LogScreen

/**
 * Define all associated [Composable] Screens with their given [Screen]
 */
@Composable
fun Navigation(navController : NavHostController) {

    NavHost(navController = navController, startDestination = Screen.LogScreen.route) {

        composable(route = Screen.LogScreen.route) {
            LogScreen()
        }

        composable(route = Screen.LiveDataScreen.route) {
            LiveDataScreen()
        }
    }
}