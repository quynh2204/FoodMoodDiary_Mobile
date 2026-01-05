package com.haphuongquynh.foodmooddiary

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.haphuongquynh.foodmooddiary.presentation.navigation.FoodMoodDiaryNavigation
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.ThemeViewModel
import com.haphuongquynh.foodmooddiary.ui.theme.FoodMoodDiaryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()
    
    // Store deep link data
    private var deepLinkData by mutableStateOf<DeepLinkData?>(null)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach { (permission, isGranted) ->
            if (isGranted) {
                // Permission granted
            } else {
                // Permission denied - handle gracefully
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle deep link
        handleDeepLink(intent)
        
        // Request runtime permissions
        requestRuntimePermissions()
        
        setContent {
            val themeMode by themeViewModel.themeMode.collectAsState()
            val systemDarkTheme = isSystemInDarkTheme()

            val isDarkTheme = when (themeMode) {
                "Light" -> false
                "Dark" -> true
                else -> systemDarkTheme // "Auto" follows system
            }

            FoodMoodDiaryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FoodMoodDiaryNavigation(
                        deepLinkData = deepLinkData
                    )
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }
    
    /**
     * Handle deep link from Firebase password reset email
     */
    private fun handleDeepLink(intent: Intent?) {
        val data: Uri? = intent?.data
        
        if (data != null) {
            // Check if it's a Firebase auth action
            val mode = data.getQueryParameter("mode")
            val oobCode = data.getQueryParameter("oobCode")
            
            if (mode == "resetPassword" && !oobCode.isNullOrEmpty()) {
                deepLinkData = DeepLinkData(
                    action = "resetPassword",
                    oobCode = oobCode
                )
            }
        }
    }

    /**
     * Request necessary runtime permissions based on Android version
     */
    private fun requestRuntimePermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // Camera permission (Android 6.0+)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        // Location permissions (Android 6.0+)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        // Notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Media permissions (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            // For Android 12 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        // Request all needed permissions at once
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}

/**
 * Data class for deep link information
 */
data class DeepLinkData(
    val action: String,
    val oobCode: String
)