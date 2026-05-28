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
data class MapScreenDestination(
    val centerLat: Double = 0.0,
    val centerLng: Double = 0.0,
    val shouldCenter: Boolean = false,
)

@Serializable
object ListScreenDestination

@Serializable
data class AddEditScreenDestination(
    val lat: Double,
    val lng: Double,
    val locationId: Long = 0L,
)

enum class TopLevelDestination(val icon: ImageVector, val label: String) {
    MAP(Icons.Rounded.Map, "Map"),
    LIST(Icons.AutoMirrored.Rounded.List, "List"),
}

private fun NavDestination?.currentTopLevel(): TopLevelDestination? = when {
    this?.hasRoute(MapScreenDestination::class) == true -> TopLevelDestination.MAP
    this?.hasRoute(ListScreenDestination::class) == true -> TopLevelDestination.LIST
    else -> null
}

@Composable
fun MapTrackerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = MapScreenDestination(),
        modifier = modifier,
    ) {
        composable<MapScreenDestination> { backStackEntry ->
            val dest = backStackEntry.toRoute<MapScreenDestination>()
            MapRoute(
                onNavigateToAddEdit = { lat, lng ->
                    navController.navigate(AddEditScreenDestination(lat, lng))
                },
                centerLat = dest.centerLat,
                centerLng = dest.centerLng,
                shouldCenter = dest.shouldCenter,
            )
        }
        composable<ListScreenDestination> {
            ListRoute(
                onNavigateToEditLocation = { id, lat, lng ->
                    navController.navigate(AddEditScreenDestination(lat, lng, id))
                },
                onNavigateToMap = { lat, lng ->
                    navController.navigate(MapScreenDestination(lat, lng, true)) {
                        popUpTo<MapScreenDestination> { inclusive = true; saveState = false }
                        launchSingleTop = false
                    }
                },
            )
        }
        composable<AddEditScreenDestination> { backStackEntry ->
            val dest = backStackEntry.toRoute<AddEditScreenDestination>()
            AddEditRoute(
                lat = dest.lat,
                lng = dest.lng,
                locationId = dest.locationId,
                onNavigateBack = navController::popBackStack,
            )
        }
    }
}

@Composable
fun MapTrackerBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentTopLevel = navBackStackEntry?.destination.currentTopLevel()

    if (currentTopLevel == null) return

    NavigationBar {
        TopLevelDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = destination == currentTopLevel,
                onClick = {
                    when (destination) {
                        TopLevelDestination.MAP -> navController.navigate(MapScreenDestination()) {
                            popUpTo<MapScreenDestination> { saveState = true; inclusive = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                        TopLevelDestination.LIST -> navController.navigate(ListScreenDestination) {
                            popUpTo<MapScreenDestination> { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label) },
            )
        }
    }
}
