package com.oncelabs.nanobeacon.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * All possible routes & associated args should be defined here
 */
sealed class Screen(val route: String, val icon : ImageVector, val title : String) {
    //object LiveDataScreen : Screen("liveData", Icons.Default.QueryStats, "Live Data")
    object ScannerScreen : Screen("scanner", Icons.Default.Description, "Scanner")
    object QrScanScreen : Screen("config", Icons.Default.QrCodeScanner, "Config")

}

