package com.haphuongquynh.foodmooddiary.util.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location as AndroidLocation
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.haphuongquynh.foodmooddiary.domain.model.Location
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Location manager for getting current location
 * Uses FusedLocationProviderClient for efficient location access
 */
@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Get current location
     * @return Location or null if permission denied or location unavailable
     */
    suspend fun getCurrentLocation(): Location? {
        return try {
            // Check permission
            if (!hasLocationPermission()) {
                android.util.Log.w("LocationManager", "Location permission not granted")
                return null
            }

            // Get current location
            val cancellationToken = CancellationTokenSource()
            val androidLocation = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationToken.token
            ).await()

            androidLocation?.let {
                // Get address from coordinates
                val address = getAddressFromLocation(it.latitude, it.longitude)
                
                Location(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    address = address
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("LocationManager", "Failed to get location", e)
            null
        }
    }

    /**
     * Get last known location (faster but may be outdated)
     */
    suspend fun getLastKnownLocation(): Location? {
        return try {
            if (!hasLocationPermission()) {
                return null
            }

            val androidLocation = fusedLocationClient.lastLocation.await()
            androidLocation?.let {
                val address = getAddressFromLocation(it.latitude, it.longitude)
                Location(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    address = address
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("LocationManager", "Failed to get last location", e)
            null
        }
    }

    /**
     * Check if location permission is granted
     */
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Get address from coordinates using Geocoder
     */
    @Suppress("DEPRECATION")
    private fun getAddressFromLocation(latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = Geocoder(context, java.util.Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                buildString {
                    // Try to build readable address
                    val parts = mutableListOf<String>()
                    address.thoroughfare?.let { parts.add(it) } // Street
                    address.subAdminArea?.let { parts.add(it) } // District
                    address.locality?.let { parts.add(it) } // City
                    address.countryName?.let { parts.add(it) } // Country
                    
                    if (parts.isNotEmpty()) {
                        append(parts.joinToString(", "))
                    } else {
                        // Fallback to full address line
                        address.getAddressLine(0)?.let { append(it) }
                    }
                }
            } else {
                "Lat: ${String.format("%.4f", latitude)}, Lng: ${String.format("%.4f", longitude)}"
            }
        } catch (e: IOException) {
            android.util.Log.e("LocationManager", "Geocoder failed", e)
            "Lat: ${String.format("%.4f", latitude)}, Lng: ${String.format("%.4f", longitude)}"
        }
    }
}
