package com.haphuongquynh.foodmooddiary.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * Composable function to handle shake detection
 * Automatically starts/stops based on lifecycle
 */
@Composable
fun rememberShakeDetector(onShake: () -> Unit): ShakeDetector {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val shakeDetector = remember {
        ShakeDetector(context, onShake)
    }
    
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> shakeDetector.start()
                Lifecycle.Event.ON_PAUSE -> shakeDetector.stop()
                else -> {}
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            shakeDetector.stop()
        }
    }
    
    return shakeDetector
}

/**
 * Composable function to handle light sensor
 * Returns current dark mode state based on ambient light
 */
@Composable
fun rememberLightSensor(enabled: Boolean = true): Boolean {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var isDarkMode by remember { mutableStateOf(false) }
    
    DisposableEffect(lifecycleOwner, enabled) {
        if (!enabled) {
            return@DisposableEffect onDispose { }
        }
        
        val lightSensor = LightSensor(context) { dark ->
            isDarkMode = dark
        }
        
        if (!lightSensor.isAvailable()) {
            return@DisposableEffect onDispose { }
        }
        
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> lightSensor.start()
                Lifecycle.Event.ON_PAUSE -> lightSensor.stop()
                else -> {}
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            lightSensor.stop()
        }
    }
    
    return isDarkMode
}

/**
 * Check if sensor is available
 */
fun hasSensorSupport(context: Context, sensorType: Int): Boolean {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? android.hardware.SensorManager
    return sensorManager?.getDefaultSensor(sensorType) != null
}
