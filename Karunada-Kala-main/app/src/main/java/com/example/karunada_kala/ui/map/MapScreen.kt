package com.example.karunada_kala.ui.map

import kotlinx.coroutines.launch

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    onNavigateToProfile: (String) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val artisans by viewModel.filteredArtisans.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val scope = rememberCoroutineScope()

    var hasLocationPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasLocationPermission = isGranted }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Default to Karnataka center roughly
    val karnatakaCenter = LatLng(15.3173, 75.7139)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(karnatakaCenter, 6f)
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val fusedLocationClient = remember { com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context) }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            uiSettings = MapUiSettings(myLocationButtonEnabled = false) // We'll use our own
        ) {
            artisans.filter { it.lat != 0.0 || it.lng != 0.0 }.forEach { artisan ->
                val markerColor = if (artisan.isPerformer) {
                    BitmapDescriptorFactory.HUE_RED
                } else {
                    BitmapDescriptorFactory.HUE_YELLOW
                }

                Marker(
                    state = MarkerState(position = LatLng(artisan.lat, artisan.lng)),
                    title = artisan.name,
                    snippet = artisan.type,
                    icon = BitmapDescriptorFactory.defaultMarker(markerColor),
                    onClick = {
                        false 
                    },
                    onInfoWindowClick = {
                        onNavigateToProfile(artisan.id)
                    }
                )
            }
        }

        // Filter Toggle Overlay
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                FilterChip(
                    selected = filter == MapFilter.ALL,
                    onClick = { viewModel.setFilter(MapFilter.ALL) },
                    label = { Text("All") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = filter == MapFilter.MAKERS,
                    onClick = { viewModel.setFilter(MapFilter.MAKERS) },
                    label = { Text("Makers") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = filter == MapFilter.PERFORMERS,
                    onClick = { viewModel.setFilter(MapFilter.PERFORMERS) },
                    label = { Text("Performers") }
                )
            }
        }
        // Center on Me FAB
        if (hasLocationPermission) {
            FloatingActionButton(
                onClick = {
                    try {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            location?.let {
                                scope.launch {
                                    cameraPositionState.animate(
                                        com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                                            LatLng(it.latitude, it.longitude),
                                            12f
                                        )
                                    )
                                }
                            }
                        }
                    } catch (e: SecurityException) {}
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 80.dp, end = 16.dp), // Avoid bottom nav
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Center on me") 
            }
        }
    }
}
