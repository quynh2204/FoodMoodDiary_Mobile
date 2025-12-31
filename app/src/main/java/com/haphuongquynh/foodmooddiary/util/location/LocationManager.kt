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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Location manager for getting current location
 * Uses IP-based geolocation first (more accurate for emulator/laptop)
 * Falls back to GPS if IP fails
 */
@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Get current location
     * Priority: IP-based geolocation -> GPS (for real device)
     * IP geolocation is more accurate on emulator since GPS returns fake Google HQ location
     * @return Location or null if all methods fail
     */
    suspend fun getCurrentLocation(): Location? {
        // Try IP geolocation first - more accurate for emulator/laptop
        val ipLocation = getLocationFromIP()
        if (ipLocation != null) {
            android.util.Log.d("LocationManager", "Got location from IP: ${ipLocation.address}")
            return ipLocation
        }
        
        // Fall back to GPS if IP fails
        return try {
            if (!hasLocationPermission()) {
                android.util.Log.w("LocationManager", "Location permission not granted")
                return null
            }

            val cancellationToken = CancellationTokenSource()
            val androidLocation = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationToken.token
            ).await()

            androidLocation?.let {
                val address = getAddressFromLocation(it.latitude, it.longitude)
                Location(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    address = address
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("LocationManager", "GPS failed", e)
            null
        }
    }

    /**
     * Get location from IP address (works on laptop/emulator)
     * Uses multiple services for reliability
     */
    private suspend fun getLocationFromIP(): Location? {
        return withContext(Dispatchers.IO) {
            // Try ipinfo.io first (more reliable)
            tryIpInfo() ?: tryIpApi()
        }
    }
    
    /**
     * Try ipinfo.io service
     */
    private fun tryIpInfo(): Location? {
        return try {
            android.util.Log.d("LocationManager", "Trying ipinfo.io...")
            val url = URL("https://ipinfo.io/json")
            val response = url.readText()
            val json = JSONObject(response)
            
            val loc = json.optString("loc", "")
            if (loc.isNotEmpty() && loc.contains(",")) {
                val parts = loc.split(",")
                val lat = parts[0].toDouble()
                val lon = parts[1].toDouble()
                val city = json.optString("city", "")
                val region = json.optString("region", "")
                val country = json.optString("country", "")
                
                val address = listOf(city, region, country)
                    .filter { it.isNotEmpty() }
                    .joinToString(", ")
                
                android.util.Log.d("LocationManager", "ipinfo.io: $lat, $lon - $address")
                
                Location(
                    latitude = lat,
                    longitude = lon,
                    address = address.ifEmpty { "Lat: $lat, Lng: $lon" }
                )
            } else null
        } catch (e: Exception) {
            android.util.Log.e("LocationManager", "ipinfo.io failed", e)
            null
        }
    }
    
    /**
     * Try ip-api.com service (backup)
     */
    private fun tryIpApi(): Location? {
        return try {
            android.util.Log.d("LocationManager", "Trying ip-api.com...")
            val url = URL("http://ip-api.com/json/?fields=status,city,regionName,country,lat,lon")
            val response = url.readText()
            val json = JSONObject(response)
            
            if (json.getString("status") == "success") {
                val lat = json.getDouble("lat")
                val lon = json.getDouble("lon")
                val city = json.optString("city", "")
                val region = json.optString("regionName", "")
                val country = json.optString("country", "")
                
                val address = listOf(city, region, country)
                    .filter { it.isNotEmpty() }
                    .joinToString(", ")
                
                android.util.Log.d("LocationManager", "ip-api.com: $lat, $lon - $address")
                
                Location(
                    latitude = lat,
                    longitude = lon,
                    address = address.ifEmpty { "Lat: $lat, Lng: $lon" }
                )
            } else null
        } catch (e: Exception) {
            android.util.Log.e("LocationManager", "ip-api.com failed", e)
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
