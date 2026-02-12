package com.example.stamptourapp.core.ui.navigation

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
import androidx.navigation.NavDestination.Companion.hierarchy
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
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { item ->
            // ✅ route가 완전 일치가 아니라도(하위 화면/파라미터) 탭이 맞게 선택되도록
            val selected = currentDestination
                ?.hierarchy
                ?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                icon = item.icon,
                label = { Text(item.label) }
            )

        }
    }
}
