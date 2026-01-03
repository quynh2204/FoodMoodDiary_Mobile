package com.haphuongquynh.foodmooddiary.util.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location as AndroidLocation
import android.os.Build
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
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Location manager for getting current location
 * Uses IP-based geolocation first (more accurate for emulator/laptop)
 * Falls back to GPS if IP fails
 * 
 * On emulator: Gets location based on host machine's real IP address
 */
@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    // Check if running on emulator
    private val isEmulator: Boolean by lazy {
        (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk" == Build.PRODUCT
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu"))
    }

    /**
     * Get current location
     * Priority: IP-based geolocation -> GPS (for real device)
     * IP geolocation is more accurate on emulator since GPS returns fake Google HQ location
     * @return Location or null if all methods fail
     */
    suspend fun getCurrentLocation(): Location? {
        android.util.Log.d("LocationManager", "isEmulator: $isEmulator")
        
        // Try IP geolocation first - more accurate for emulator/laptop
        val ipLocation = getLocationFromIP()
        if (ipLocation != null) {
            android.util.Log.d("LocationManager", "Got location from IP: ${ipLocation.address}")
            return ipLocation
        }
        
        // Fall back to GPS if IP fails (useful for real device)
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
     * For emulator: Gets the host machine's real public IP
     * Uses multiple services for reliability
     */
    private suspend fun getLocationFromIP(): Location? {
        return withContext(Dispatchers.IO) {
            // Try multiple services - they will get the real public IP of the host machine
            // even when running on emulator
            tryIpInfo() ?: tryIpApi() ?: tryIpWhois()
        }
    }
    
    /**
     * Try ipinfo.io service
     * This service gets the public IP of the network connection (host machine's IP)
     */
    private fun tryIpInfo(): Location? {
        return try {
            android.util.Log.d("LocationManager", "Trying ipinfo.io...")
            val connection = URL("https://ipinfo.io/json").openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty("Accept", "application/json")
            
            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()
            
            val json = JSONObject(response)
            
            // Log the IP being used
            val ip = json.optString("ip", "unknown")
            android.util.Log.d("LocationManager", "Public IP detected: $ip")
            
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
                
                android.util.Log.d("LocationManager", "ipinfo.io: IP=$ip, Location=$lat, $lon - $address")
                
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
     * Returns location based on public IP
     */
    private fun tryIpApi(): Location? {
        return try {
            android.util.Log.d("LocationManager", "Trying ip-api.com...")
            val connection = URL("http://ip-api.com/json/?fields=status,query,city,regionName,country,lat,lon").openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            
            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()
            
            val json = JSONObject(response)
            
            if (json.getString("status") == "success") {
                val ip = json.optString("query", "unknown")
                val lat = json.getDouble("lat")
                val lon = json.getDouble("lon")
                val city = json.optString("city", "")
                val region = json.optString("regionName", "")
                val country = json.optString("country", "")
                
                val address = listOf(city, region, country)
                    .filter { it.isNotEmpty() }
                    .joinToString(", ")
                
                android.util.Log.d("LocationManager", "ip-api.com: IP=$ip, Location=$lat, $lon - $address")
                
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
     * Try ipwhois.app service (second backup)
     * Another reliable IP geolocation service
     */
    private fun tryIpWhois(): Location? {
        return try {
            android.util.Log.d("LocationManager", "Trying ipwhois.app...")
            val connection = URL("https://ipwhois.app/json/?objects=ip,city,region,country,latitude,longitude,success").openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            
            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()
            
            val json = JSONObject(response)
            
            if (json.optBoolean("success", false)) {
                val ip = json.optString("ip", "unknown")
                val lat = json.getDouble("latitude")
                val lon = json.getDouble("longitude")
                val city = json.optString("city", "")
                val region = json.optString("region", "")
                val country = json.optString("country", "")
                
                val address = listOf(city, region, country)
                    .filter { it.isNotEmpty() }
                    .joinToString(", ")
                
                android.util.Log.d("LocationManager", "ipwhois.app: IP=$ip, Location=$lat, $lon - $address")
                
                Location(
                    latitude = lat,
                    longitude = lon,
                    address = address.ifEmpty { "Lat: $lat, Lng: $lon" }
                )
            } else null
        } catch (e: Exception) {
            android.util.Log.e("LocationManager", "ipwhois.app failed", e)
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
