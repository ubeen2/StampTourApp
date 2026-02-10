package com.example.stamptourapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

    //  현재 화면(route) 가져오기
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    //  TopBar & BottomBar를 보여줄 화면(홈/지도/스탬프만)
    val showBars = currentRoute in listOf(
        Routes.HOME,
        Routes.MAP,
        Routes.STAMPBOOK,
        Routes.MYPAGE

    )

    Scaffold(
        topBar = {
            if (showBars) {
                AppTopBar(
                    onLogoClick = {
                        navController.navigate(Routes.HOME) {
                            launchSingleTop = true
                        }
                    },
                    onProfileClick = {
                        navController.navigate(Routes.MYPAGE) {
                            launchSingleTop = true
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
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
