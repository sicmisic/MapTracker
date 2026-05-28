package com.example.maptracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.maptracker.presentation.ui.navigation.MapTrackerBottomBar
import com.example.maptracker.presentation.ui.navigation.MapTrackerNavHost
import com.example.maptracker.ui.theme.MapTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapTrackerTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { MapTrackerBottomBar(navController = navController) },
                ) { paddingValues ->
                    MapTrackerNavHost(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues),
                    )
                }
            }
        }
    }
}
