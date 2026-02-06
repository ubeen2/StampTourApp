package com.example.stamptourapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.stamptourapp.feature.home.HomeScreen
import com.example.stamptourapp.ui.theme.StampTourAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StampTourAppTheme {
                HomeScreen(
                    onGoProgramAll = { },
                    onGoEventAll = { },
                    onGoStampList = { },
                    onGoMap = { },
                    onGoCoupons = { }
                )
            }
        }
    }
}
