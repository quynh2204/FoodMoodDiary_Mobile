package com.haphuongquynh.foodmooddiary.presentation.screens.map

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.haphuongquynh.foodmooddiary.ui.theme.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.haphuongquynh.foodmooddiary.R
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.MapViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Map Screen - Day 21
 * Display all food entries with locations on Google Maps
 * Features: Clustering, Heat Map, Custom Markers
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    onNavigateBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val entriesWithLocation by viewModel.entriesWithLocation.collectAsStateWithLifecycle()
    val selectedEntry by viewModel.selectedEntry.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var showHeatMap by remember { mutableStateOf(false) }
    var mapType by remember { mutableIntStateOf(GoogleMap.MAP_TYPE_NORMAL) }

    // Location permission
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        if (!locationPermissionState.allPermissionsGranted) {
            locationPermissionState.launchMultiplePermissionRequest()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackPrimary)
    ) {
        // Map content
        Box(modifier = Modifier.fillMaxSize()) {
            if (locationPermissionState.allPermissionsGranted) {
                GoogleMapView(
                    entries = entriesWithLocation,
                    showHeatMap = showHeatMap,
                    mapType = mapType,
                    onMarkerClick = { viewModel.selectEntry(it) },
                    modifier = Modifier.fillMaxSize()
                )

                // Selected entry bottom sheet
                selectedEntry?.let { entry ->
                    EntryBottomSheet(
                        entry = entry,
                        onDismiss = { viewModel.selectEntry(null) },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                // Loading indicator
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                // Permission not granted
                PermissionDeniedContent(
                    onRequestPermission = {
                        locationPermissionState.launchMultiplePermissionRequest()
                    },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        
        // Floating action buttons
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Map type toggle
            FloatingActionButton(
                onClick = { 
                    mapType = when (mapType) {
                        GoogleMap.MAP_TYPE_NORMAL -> GoogleMap.MAP_TYPE_SATELLITE
                        GoogleMap.MAP_TYPE_SATELLITE -> GoogleMap.MAP_TYPE_TERRAIN
                        else -> GoogleMap.MAP_TYPE_NORMAL
                    }
                },
                containerColor = Color(0xFF2C2C2E),
                contentColor = Color.White,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Map, "Map Type", modifier = Modifier.size(24.dp))
            }
            
            // Heat map toggle
            FloatingActionButton(
                onClick = { showHeatMap = !showHeatMap },
                containerColor = if (showHeatMap) PastelGreen else BlackSecondary,
                contentColor = WhiteText,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    if (showHeatMap) Icons.Default.Layers else Icons.Default.LayersClear,
                    "Toggle Heat Map",
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Refresh
            FloatingActionButton(
                onClick = { viewModel.refresh() },
                containerColor = Color(0xFF2C2C2E),
                contentColor = Color.White,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Refresh, "Refresh", modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun GoogleMapView(
    entries: List<FoodEntry>,
    showHeatMap: Boolean,
    mapType: Int,
    onMarkerClick: (FoodEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var map by remember { mutableStateOf<GoogleMap?>(null) }

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                onCreate(null)
                onResume()
                getMapAsync { googleMap ->
                    map = googleMap
                    
                    // Map settings
                    googleMap.mapType = mapType
                    googleMap.uiSettings.isZoomControlsEnabled = true
                    googleMap.uiSettings.isMyLocationButtonEnabled = true
                    
                    // Try to enable my location
                    try {
                        googleMap.isMyLocationEnabled = true
                    } catch (e: SecurityException) {
                        // Permission not granted
                    }

                    // Setup cluster manager
                    val clusterManager = ClusterManager<FoodEntryClusterItem>(ctx, googleMap)
                    
                    // Custom cluster renderer
                    clusterManager.renderer = object : DefaultClusterRenderer<FoodEntryClusterItem>(ctx, googleMap, clusterManager) {
                        override fun onBeforeClusterItemRendered(item: FoodEntryClusterItem, markerOptions: MarkerOptions) {
                            // Custom marker with mood color
                            markerOptions.icon(
                                BitmapDescriptorFactory.defaultMarker(
                                    getHueFromColor(item.entry.moodColor)
                                )
                            )
                            markerOptions.title(item.entry.foodName)
                        }
                    }

                    // Add markers
                    clusterManager.clearItems()
                    entries.forEach { entry ->
                        entry.location?.let { location ->
                            clusterManager.addItem(
                                FoodEntryClusterItem(
                                    latLng = LatLng(location.latitude, location.longitude),
                                    entry = entry
                                )
                            )
                        }
                    }
                    clusterManager.cluster()

                    // Set click listeners
                    googleMap.setOnCameraIdleListener(clusterManager)
                    googleMap.setOnMarkerClickListener(clusterManager)
                    clusterManager.setOnClusterItemClickListener { item ->
                        onMarkerClick(item.entry)
                        true
                    }

                    // Move camera to show all markers
                    if (entries.isNotEmpty()) {
                        val boundsBuilder = LatLngBounds.Builder()
                        entries.forEach { entry ->
                            entry.location?.let { location ->
                                boundsBuilder.include(LatLng(location.latitude, location.longitude))
                            }
                        }
                        val bounds = boundsBuilder.build()
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(bounds, 100)
                        )
                    }
                }
            }
        },
        update = { mapView ->
            map?.let { googleMap ->
                googleMap.mapType = mapType
                // Update heat map visibility if needed
            }
        },
        modifier = modifier
    )
}

@Composable
fun EntryBottomSheet(
    entry: FoodEntry,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2E)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.foodName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = WhiteText
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, "Close", tint = WhiteText)
                }
            }

            // Photo
            if (entry.photoUrl != null || entry.localPhotoPath != null) {
                AsyncImage(
                    model = entry.photoUrl ?: entry.localPhotoPath,
                    contentDescription = "Food Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(entry.moodColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            // Details
            if (entry.notes.isNotBlank()) {
                Text(
                    text = entry.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Text(
                    text = dateFormat.format(Date(entry.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            entry.location?.let { location ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = PastelGreen
                    )
                    Text(
                        text = location.address ?: "${location.latitude}, ${location.longitude}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionDeniedContent(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            Icons.Default.LocationOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Text(
            text = "Cần quyền truy cập vị trí",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Text(
            text = "Vui lòng cấp quyền truy cập vị trí để xem bản đồ các địa điểm",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Button(
            onClick = onRequestPermission,
            colors = ButtonDefaults.buttonColors(
                containerColor = PastelGreen
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Cấp quyền", color = Color.White)
        }
    }
}

/**
 * Cluster item for grouping nearby markers
 */
data class FoodEntryClusterItem(
    private val latLng: LatLng,
    val entry: FoodEntry
) : com.google.maps.android.clustering.ClusterItem {
    override fun getPosition(): LatLng = latLng
    override fun getTitle(): String = entry.foodName
    override fun getSnippet(): String = entry.notes
    override fun getZIndex(): Float = 0f
}

/**
 * Convert Android color to Google Maps hue (0-360)
 */
private fun getHueFromColor(color: Int): Float {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(color, hsv)
    return hsv[0]
}
