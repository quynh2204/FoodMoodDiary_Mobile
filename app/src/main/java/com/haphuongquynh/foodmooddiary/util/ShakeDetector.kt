package com.haphuongquynh.foodmooddiary.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Shake detector using accelerometer sensor
 * Detects shake gesture to quickly add food entry
 */
class ShakeDetector(
    context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    private var lastShakeTime = 0L
    private var shakeCount = 0
    
    companion object {
        private const val SHAKE_THRESHOLD = 15f // m/s^2
        private const val SHAKE_TIME_WINDOW = 500L // milliseconds
        private const val SHAKE_COUNT_THRESHOLD = 2 // number of shakes required
    }

    /**
     * Start listening for shake events
     */
    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    /**
     * Stop listening for shake events
     */
    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = it.values[0]
                val y = it.values[1]
                val z = it.values[2]

                // Calculate acceleration magnitude (excluding gravity)
                val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat() - SensorManager.GRAVITY_EARTH

                if (acceleration > SHAKE_THRESHOLD) {
                    val currentTime = System.currentTimeMillis()
                    
                    if (currentTime - lastShakeTime < SHAKE_TIME_WINDOW) {
                        shakeCount++
                        
                        if (shakeCount >= SHAKE_COUNT_THRESHOLD) {
                            // Shake detected!
                            onShake()
                            shakeCount = 0
                        }
                    } else {
                        shakeCount = 1
                    }
                    
                    lastShakeTime = currentTime
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }
}
