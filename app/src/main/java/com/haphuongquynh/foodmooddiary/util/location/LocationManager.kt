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
 * Strategy:
 * - Real device: GPS first, then IP fallback
 * - Emulator: IP first (GPS returns fake Google HQ location)
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
     * Get current location with best accuracy
     * Strategy: Try GPS first (allows manual location on emulator), then IP fallback
     */
    suspend fun getCurrentLocation(): Location? {
        android.util.Log.d("LocationManager", "=== Getting location ===")
        android.util.Log.d("LocationManager", "Is Emulator: $isEmulator")
        
        // Try GPS first for both emulator and real device
        val gpsLocation = getGPSLocation()
        if (gpsLocation != null) {
            android.util.Log.d("LocationManager", "✓ Using GPS location: ${gpsLocation.address}")
            return gpsLocation
        }
        
        android.util.Log.w("LocationManager", "⚠ GPS failed, trying IP-based location")
        
        // Fallback to IP-based location
        val ipLocation = getLocationFromIP()
        if (ipLocation != null) {
            android.util.Log.d("LocationManager", "✓ Using IP location: ${ipLocation.address}")
            return ipLocation
        }
        
        android.util.Log.e("LocationManager", "✗ All location methods failed")
        return null
    }
    
    /**
     * Get location from GPS
     */
    private suspend fun getGPSLocation(): Location? {
        return try {
            if (!hasLocationPermission()) {
                android.util.Log.w("LocationManager", "Location permission not granted")
                return null
            }

            val cancellationToken = CancellationTokenSource()
            val androidLocation = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY, // Use high accuracy for real device
                cancellationToken.token
            ).await()

            androidLocation?.let {
                val address = getAddressFromLocation(it.latitude, it.longitude)
                android.util.Log.d("LocationManager", "GPS Location: ${it.latitude}, ${it.longitude} - $address")
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
     * Get location from IP address
     * Uses multiple reliable services with HTTPS
     */
    private suspend fun getLocationFromIP(): Location? {
        return withContext(Dispatchers.IO) {
            // Try services in order of reliability
            tryIpApiCo() ?: tryIpInfo() ?: tryIpWhois() ?: tryGeoJs()
        }
    }
    
    /**
     * Try ipapi.co - Very reliable, HTTPS, no API key needed
     * 1000 requests/day free
     */
    private fun tryIpApiCo(): Location? {
        return try {
            android.util.Log.d("LocationManager", "Trying ipapi.co...")
            val connection = URL("https://ipapi.co/json/").openConnection() as HttpURLConnection
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            connection.setRequestProperty("User-Agent", "FoodMoodDiary/1.0")
            connection.setRequestProperty("Accept", "application/json")
            
            if (connection.responseCode != 200) {
                connection.disconnect()
                return null
            }
            
            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()
            
            val json = JSONObject(response)
            
            // Check for error
            if (json.has("error")) {
                android.util.Log.e("LocationManager", "ipapi.co error: ${json.optString("reason")}")
                return null
            }
            
            val ip = json.optString("ip", "unknown")
            val lat = json.optDouble("latitude", 0.0)
            val lon = json.optDouble("longitude", 0.0)
            
            if (lat == 0.0 && lon == 0.0) return null
            
            val city = json.optString("city", "")
            val region = json.optString("region", "")
            val country = json.optString("country_name", "")
            
            val address = listOf(city, region, country)
                .filter { it.isNotEmpty() }
                .joinToString(", ")
            
            android.util.Log.d("LocationManager", "ipapi.co SUCCESS: IP=$ip, Location=$lat, $lon - $address")
            
            Location(
                latitude = lat,
                longitude = lon,
                address = address.ifEmpty { "Lat: $lat, Lng: $lon" }
            )
        } catch (e: Exception) {
            android.util.Log.e("LocationManager", "ipapi.co failed: ${e.message}")
            null
        }
    }
    
    /**
     * Try ipinfo.io service - HTTPS, reliable
     */
    private fun tryIpInfo(): Location? {
        return try {
            android.util.Log.d("LocationManager", "Trying ipinfo.io...")
            val connection = URL("https://ipinfo.io/json").openConnection() as HttpURLConnection
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            connection.setRequestProperty("Accept", "application/json")
            
            if (connection.responseCode != 200) {
                connection.disconnect()
                return null
            }
            
            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()
            
            val json = JSONObject(response)
            
            val ip = json.optString("ip", "unknown")
            val loc = json.optString("loc", "")
            
            if (loc.isEmpty() || !loc.contains(",")) return null
            
            val parts = loc.split(",")
            val lat = parts[0].toDoubleOrNull() ?: return null
            val lon = parts[1].toDoubleOrNull() ?: return null
            
            val city = json.optString("city", "")
            val region = json.optString("region", "")
            val country = json.optString("country", "")
            
            val address = listOf(city, region, country)
                .filter { it.isNotEmpty() }
                .joinToString(", ")
            
            android.util.Log.d("LocationManager", "ipinfo.io SUCCESS: IP=$ip, Location=$lat, $lon - $address")
            
            Location(
                latitude = lat,
                longitude = lon,
                address = address.ifEmpty { "Lat: $lat, Lng: $lon" }
            )
        } catch (e: Exception) {
            android.util.Log.e("LocationManager", "ipinfo.io failed: ${e.message}")
            null
        }
    }
    
    /**
     * Try ipwhois.app service - HTTPS backup
     */
    private fun tryIpWhois(): Location? {
        return try {
            android.util.Log.d("LocationManager", "Trying ipwhois.app...")
            val connection = URL("https://ipwhois.app/json/?objects=ip,city,region,country,latitude,longitude,success").openConnection() as HttpURLConnection
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            
            if (connection.responseCode != 200) {
                connection.disconnect()
                return null
            }
            
            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()
            
            val json = JSONObject(response)
            
            if (!json.optBoolean("success", false)) return null
            
            val ip = json.optString("ip", "unknown")
            val lat = json.optDouble("latitude", 0.0)
            val lon = json.optDouble("longitude", 0.0)
            
            if (lat == 0.0 && lon == 0.0) return null
            
            val city = json.optString("city", "")
            val region = json.optString("region", "")
            val country = json.optString("country", "")
            
            val address = listOf(city, region, country)
                .filter { it.isNotEmpty() }
                .joinToString(", ")
            
            android.util.Log.d("LocationManager", "ipwhois.app SUCCESS: IP=$ip, Location=$lat, $lon - $address")
            
            Location(
                latitude = lat,
                longitude = lon,
                address = address.ifEmpty { "Lat: $lat, Lng: $lon" }
            )
        } catch (e: Exception) {
            android.util.Log.e("LocationManager", "ipwhois.app failed: ${e.message}")
            null
        }
    }
    
    /**
     * Try get.geojs.io - HTTPS, no rate limit, very reliable
     */
    private fun tryGeoJs(): Location? {
        return try {
            android.util.Log.d("LocationManager", "Trying geojs.io...")
            val connection = URL("https://get.geojs.io/v1/ip/geo.json").openConnection() as HttpURLConnection
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            
            if (connection.responseCode != 200) {
                connection.disconnect()
                return null
            }
            
            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()
            
            val json = JSONObject(response)
            
            val ip = json.optString("ip", "unknown")
            val lat = json.optString("latitude", "").toDoubleOrNull() ?: return null
            val lon = json.optString("longitude", "").toDoubleOrNull() ?: return null
            
            val city = json.optString("city", "")
            val region = json.optString("region", "")
            val country = json.optString("country", "")
            
            val address = listOf(city, region, country)
                .filter { it.isNotEmpty() }
                .joinToString(", ")
            
            android.util.Log.d("LocationManager", "geojs.io SUCCESS: IP=$ip, Location=$lat, $lon - $address")
            
            Location(
                latitude = lat,
                longitude = lon,
                address = address.ifEmpty { "Lat: $lat, Lng: $lon" }
            )
        } catch (e: Exception) {
            android.util.Log.e("LocationManager", "geojs.io failed: ${e.message}")
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
    fun hasLocationPermission(): Boolean {
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
     * Returns detailed address with street number, street name, district, city, country
     */
    @Suppress("DEPRECATION")
    private fun getAddressFromLocation(latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = Geocoder(context, java.util.Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                buildString {
                    val parts = mutableListOf<String>()
                    
                    // Get detailed street address (includes street number)
                    address.subThoroughfare?.let { streetNumber ->
                        address.thoroughfare?.let { streetName ->
                            parts.add("$streetNumber $streetName")
                        } ?: parts.add(streetNumber)
                    } ?: address.thoroughfare?.let { parts.add(it) }
                    
                    // Add district (subAdminArea)
                    address.subAdminArea?.let { parts.add(it) }
                    
                    // Add city (locality)
                    address.locality?.let { parts.add(it) }
                    
                    // Add country
                    address.countryName?.let { parts.add(it) }
                    
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
