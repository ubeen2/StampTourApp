package com.example.stamptourapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stamptourapp.core.navigation.AppNavGraph
import com.example.stamptourapp.core.navigation.Routes
import com.example.stamptourapp.core.ui.navigation.AppBottomBar
import com.example.stamptourapp.core.ui.navigation.AppTopBar
import com.example.stamptourapp.ui.theme.StampTourAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StampTourAppTheme {
                MainApp()
            }
        }
    }
}

@Composable
private fun MainApp() {
    val navController = rememberNavController()

    // ✅ 현재 Destination 가져오기 (route 단순 비교보다 안정적)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // ✅ 탭 화면(하단바/상단바 표시 대상)
    val showBars = currentDestination?.hierarchy?.any { dest ->
        dest.route == Routes.HOME ||
                dest.route == Routes.MAP ||
                dest.route == Routes.STAMPBOOK ||
                dest.route == Routes.MYPAGE
    } == true

    Scaffold(
        topBar = {
            if (showBars) {
                AppTopBar(
                    onLogoClick = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onProfileClick = {
                        navController.navigate(Routes.MYPAGE) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBars) {
                AppBottomBar(navController)
            }
        }
    ) { innerPadding ->
        // ✅ innerPadding 적용은 이미 잘 돼 있었음 (바텀바 클릭 먹힘 방지)
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
