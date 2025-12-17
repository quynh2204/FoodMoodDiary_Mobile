package com.haphuongquynh.foodmooddiary.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * Light sensor to adjust UI brightness based on ambient light
 */
class LightSensor(
    context: Context,
    private val onLightChanged: (Boolean) -> Unit // true = dark, false = bright
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    
    private var isDarkMode = false
    
    companion object {
        private const val DARK_THRESHOLD = 50f // lux
        private const val BRIGHT_THRESHOLD = 200f // lux
    }

    /**
     * Check if light sensor is available
     */
    fun isAvailable(): Boolean = lightSensor != null

    /**
     * Start listening for light changes
     */
    fun start() {
        lightSensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    /**
     * Stop listening for light changes
     */
    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_LIGHT) {
                val lux = it.values[0]
                
                // Hysteresis to prevent flickering
                val shouldBeDark = when {
                    lux < DARK_THRESHOLD -> true
                    lux > BRIGHT_THRESHOLD -> false
                    else -> isDarkMode
                }
                
                if (shouldBeDark != isDarkMode) {
                    isDarkMode = shouldBeDark
                    onLightChanged(isDarkMode)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }
}
