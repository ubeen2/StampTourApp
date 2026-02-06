package com.example.stamptourapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.stamptourapp.core.navigation.Routes

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

@Composable
fun AppBottomBar(navController: NavController) {

    val items = listOf(
        BottomNavItem(Routes.HOME, "홈") { Icon(Icons.Filled.Home, contentDescription = null) },
        BottomNavItem(Routes.MAP, "지도") { Icon(Icons.Filled.LocationOn, contentDescription = null) },
        BottomNavItem(Routes.STAMPBOOK, "스탬프") { Icon(Icons.Filled.Star, contentDescription = null) },
        BottomNavItem(Routes.MYPAGE, "마이") { Icon(Icons.Filled.Person, contentDescription = null) },
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = item.icon,
                label = { Text(item.label) }
            )
        }
    }
}
