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
 * Clean Location Manager
 * Strategy:
 * - Emulator: IP-based location (GPS is fake)
 * - Real device: GPS first, then IP fallback
 */
@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    private val isEmulator: Boolean by lazy {
        Build.FINGERPRINT.startsWith("generic") ||
        Build.MODEL.contains("Emulator") ||
        Build.MODEL.contains("Android SDK built for x86") ||
        Build.HARDWARE.contains("goldfish") ||
        Build.HARDWARE.contains("ranchu")
    }

    /**
     * Get current location
     */
    suspend fun getCurrentLocation(): Location? {
        android.util.Log.d("LocationManager", "Getting location... isEmulator: $isEmulator")
        
        return if (isEmulator) {
            getLocationFromIP() ?: getGPSLocation()
        } else {
            getGPSLocation() ?: getLocationFromIP()
        }
    }
    
    /**
     * GPS location
     */
    private suspend fun getGPSLocation(): Location? {
        return try {
            if (!hasLocationPermission()) return null

            val androidLocation = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()

            androidLocation?.let {
                val address = geocodeLocation(it.latitude, it.longitude)
                android.util.Log.d("LocationManager", "GPS: ${it.latitude}, ${it.longitude}")
                Location(it.latitude, it.longitude, address)
            }
        } catch (e: Exception) {
            android.util.Log.e("LocationManager", "GPS failed", e)
            null
        }
    }

    /**
     * IP-based location
     */
    private suspend fun getLocationFromIP(): Location? {
        return withContext(Dispatchers.IO) {
            tryIpInfo() ?: tryIpApiCo()
        }
    }
    
    private fun tryIpInfo(): Location? {
        return try {
            android.util.Log.d("LocationManager", "Trying ipinfo.io...")
            val connection = URL("https://ipinfo.io/json").openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            
            if (connection.responseCode != 200) {
                connection.disconnect()
                return null
            }
            
            val json = JSONObject(connection.inputStream.bufferedReader().readText())
            connection.disconnect()
            
            val loc = json.optString("loc", "")
            if (!loc.contains(",")) return null
            
            val parts = loc.split(",")
            val lat = parts[0].toDoubleOrNull() ?: return null
            val lon = parts[1].toDoubleOrNull() ?: return null
            
            // For IP-based location, use city-level address from API (more accurate)
            val city = json.optString("city", "")
            val region = json.optString("region", "")  
            val country = json.optString("country", "")
            
            val address = listOf(city, region, country)
                .filter { it.isNotEmpty() }
                .joinToString(", ")
            
            android.util.Log.d("LocationManager", "ipinfo.io: $lat, $lon - $address")
            
            Location(lat, lon, address.ifEmpty { null })
        } catch (e: Exception) {
            android.util.Log.e("LocationManager", "ipinfo.io failed: ${e.message}")
            null
        }
    }
    
    private fun tryIpApiCo(): Location? {
        return try {
            android.util.Log.d("LocationManager", "Trying ipapi.co...")
            val connection = URL("https://ipapi.co/json/").openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            
            if (connection.responseCode != 200) {
                connection.disconnect()
                return null
            }
            
            val json = JSONObject(connection.inputStream.bufferedReader().readText())
            connection.disconnect()
            
            if (json.has("error")) return null
            
            val lat = json.optDouble("latitude", 0.0)
            val lon = json.optDouble("longitude", 0.0)
            if (lat == 0.0 && lon == 0.0) return null
            
            // For IP-based location, use city-level address from API  
            val city = json.optString("city", "")
            val region = json.optString("region", "")
            val country = json.optString("country_name", "")
            
            val address = listOf(city, region, country)
                .filter { it.isNotEmpty() }
                .joinToString(", ")
            
            android.util.Log.d("LocationManager", "ipapi.co: $lat, $lon - $address")
            
            Location(lat, lon, address.ifEmpty { null })
        } catch (e: Exception) {
            android.util.Log.e("LocationManager", "ipapi.co failed: ${e.message}")
            null
        }
    }

    /**
     * Geocode coordinates to detailed address
     */
    @Suppress("DEPRECATION")
    private fun geocodeLocation(lat: Double, lon: Double): String? {
        return try {
            val geocoder = Geocoder(context, java.util.Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            
            addresses?.firstOrNull()?.let { address ->
                buildString {
                    val parts = mutableListOf<String>()
                    
                    // Street address
                    address.subThoroughfare?.let { num ->
                        address.thoroughfare?.let { street ->
                            parts.add("$num $street")
                        } ?: parts.add(num)
                    } ?: address.thoroughfare?.let { parts.add(it) }
                    
                    // District, City, Country
                    address.subAdminArea?.let { parts.add(it) }
                    address.locality?.let { parts.add(it) }
                    address.countryName?.let { parts.add(it) }
                    
                    if (parts.isNotEmpty()) {
                        append(parts.joinToString(", "))
                    } else {
                        address.getAddressLine(0)?.let { append(it) }
                    }
                }
            }
        } catch (e: IOException) {
            android.util.Log.e("LocationManager", "Geocoder failed", e)
            null
        }
    }

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
