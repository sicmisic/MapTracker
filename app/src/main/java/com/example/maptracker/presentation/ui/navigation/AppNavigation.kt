package com.example.maptracker.presentation.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.example.maptracker.presentation.ui.screens.AddEditRoute
import com.example.maptracker.presentation.ui.screens.ListRoute
import com.example.maptracker.presentation.ui.screens.MapRoute
import kotlinx.serialization.Serializable

@Serializable
object MapScreenDestination

@Serializable
object ListScreenDestination

@Serializable
data class AddEditScreenDestination(val lat: Double, val lng: Double)

enum class TopLevelDestination(
    val route: Any,
    val icon: ImageVector,
    val label: String,
) {
    MAP(MapScreenDestination, Icons.Rounded.Map, "Map"),
    LIST(ListScreenDestination, Icons.AutoMirrored.Rounded.List, "List"),
}

@Composable
fun MapTrackerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = MapScreenDestination,
        modifier = modifier,
    ) {
        composable<MapScreenDestination> {
            MapRoute(
                onNavigateToAddEdit = { lat, lng ->
                    navController.navigate(AddEditScreenDestination(lat, lng))
                },
            )
        }
        composable<ListScreenDestination> {
            ListRoute(
                onNavigateToAddEdit = { lat, lng ->
                    navController.navigate(AddEditScreenDestination(lat, lng))
                },
            )
        }
        composable<AddEditScreenDestination> { backStackEntry ->
            val dest = backStackEntry.toRoute<AddEditScreenDestination>()
            AddEditRoute(
                lat = dest.lat,
                lng = dest.lng,
                onNavigateBack = navController::popBackStack,
            )
        }
    }
}

@Composable
fun MapTrackerBottomBar(
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = navBackStackEntry?.destination

    // Only show bottom bar on top-level destinations
    val showBottomBar = TopLevelDestination.entries.any { dest ->
        currentDestination?.hasRoute(dest.route::class) == true
    }
    if (!showBottomBar) return

    NavigationBar {
        TopLevelDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = currentDestination?.hasRoute(destination.route::class) == true,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label) },
            )
        }
    }
}
