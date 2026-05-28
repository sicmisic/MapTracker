package com.example.maptracker.presentation.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.maptracker.domain.model.Location
import com.example.maptracker.presentation.viewmodel.LocationsUiState
import com.example.maptracker.presentation.viewmodel.LocationViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
internal fun MapRoute(
    onNavigateToAddEdit: (Double, Double) -> Unit,
    centerLat: Double = 0.0,
    centerLng: Double = 0.0,
    shouldCenter: Boolean = false,
    viewModel: LocationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.locationsUiState.collectAsStateWithLifecycle()
    MapScreen(
        uiState = uiState,
        onNavigateToAddEdit = onNavigateToAddEdit,
        centerLat = centerLat,
        centerLng = centerLng,
        shouldCenter = shouldCenter,
    )
}

@Composable
internal fun MapScreen(
    uiState: LocationsUiState,
    onNavigateToAddEdit: (Double, Double) -> Unit,
    centerLat: Double = 0.0,
    centerLng: Double = 0.0,
    shouldCenter: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(48.8566, 2.3522), 5f)
    }

    LaunchedEffect(shouldCenter, centerLat, centerLng) {
        if (shouldCenter) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(centerLat, centerLng), 15f)
            )
        }
    }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun centerOnUserLocation() {
        if (hasLocationPermission) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                loc?.let {
                    scope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
                        )
                    }
                }
            }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            onMapLongClick = { latLng ->
                onNavigateToAddEdit(latLng.latitude, latLng.longitude)
            },
        ) {
            uiState.locations.forEach { location ->
                LocationMarker(location = location)
            }
        }

        FloatingActionButton(
            onClick = ::centerOnUserLocation,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(Icons.Rounded.MyLocation, contentDescription = "Center on my location")
        }
    }
}

@Composable
private fun LocationMarker(location: Location) {
    val icon = remember(location.colorHex) {
        val color = android.graphics.Color.parseColor(location.colorHex)
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(color, hsv)
        BitmapDescriptorFactory.defaultMarker(hsv[0])
    }
    Marker(
        state = MarkerState(position = LatLng(location.latitude, location.longitude)),
        title = location.title,
        snippet = location.note.ifBlank { null },
        icon = icon,
    )
}
