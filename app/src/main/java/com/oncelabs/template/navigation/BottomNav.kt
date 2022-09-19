package com.oncelabs.template.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.oncelabs.template.ui.theme.bottomNavBackground
import com.oncelabs.template.ui.theme.iconSelected

@Composable
fun BottomNav(navController: NavController) {
    val items = listOf(
        Screen.LiveDataScreen,
        Screen.LogScreen
    )

    BottomNavigation(
        backgroundColor = bottomNavBackground,
        contentColor = Color.White,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
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
                icon = { Icon(item.icon, "navIcon", modifier = Modifier.size(30.dp)) },
                label = { Text(text = item.title, fontSize = 12.sp) },
                selectedContentColor = iconSelected,
                unselectedContentColor = Color.White,
                alwaysShowLabel = true,
            )
        }

    }
}