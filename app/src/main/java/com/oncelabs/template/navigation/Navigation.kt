package com.oncelabs.template.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.oncelabs.template.screen.LiveDataScreen
import com.oncelabs.template.screen.LogScreen

/**
 * Define all associated [Composable] Screens with their given [Screen]
 */
@Composable
fun Navigation(navController : NavHostController) {

    NavHost(navController = navController, startDestination = Screen.LiveDataScreen.route) {
        composable(route = Screen.LiveDataScreen.route) {
            LiveDataScreen()
        }

        composable(route = Screen.LogScreen.route) {
            LogScreen()
        }
    }
}