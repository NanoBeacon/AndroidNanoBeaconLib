package com.oncelabs.nanobeacon.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.oncelabs.nanobeacon.ui.theme.bottomNavBackground
import com.oncelabs.nanobeacon.ui.theme.iconSelected

@Composable
fun BottomNav(navController: NavController) {
    val items = listOf(
        Screen.LogScreen,
        Screen.LiveDataScreen
    )

    BottomNavigation(
        modifier = Modifier.height(65.dp),
        backgroundColor = bottomNavBackground,
        contentColor = Color.White,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
                BottomNavigationItem(
                    modifier = Modifier.offset(y = (-5).dp),
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let { screen_route ->
                                popUpTo(screen_route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(item.icon, "navIcon", modifier = Modifier.size(25.dp)) },
                    label = { Text(text = item.title, fontSize = 14.sp) },
                    selectedContentColor = iconSelected,
                    unselectedContentColor = Color.White,
                    alwaysShowLabel = true,
                )
            }


    }
}