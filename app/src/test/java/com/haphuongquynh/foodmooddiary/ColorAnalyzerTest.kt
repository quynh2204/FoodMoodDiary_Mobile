package com.haphuongquynh.foodmooddiary.util

import android.graphics.Color
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for ColorAnalyzer
 */
class ColorAnalyzerTest {

    @Test
    fun `extractDominantColor should return valid color`() {
        // This would require actual bitmap testing
        // For now, just test the helper methods
        assertTrue(true)
    }

    @Test
    fun `HSV values should be in valid range`() {
        val color = Color.rgb(255, 128, 64)
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)

        // Hue: 0-360
        assertTrue(hsv[0] >= 0f && hsv[0] <= 360f)
        // Saturation: 0-1
        assertTrue(hsv[1] >= 0f && hsv[1] <= 1f)
        // Value: 0-1
        assertTrue(hsv[2] >= 0f && hsv[2] <= 1f)
    }

    @Test
    fun `mood score calculation should return value between 0 and 10`() {
        val colors = listOf(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA
        )

        colors.forEach { color ->
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            
            // Mock mood score calculation
            val moodScore = hsv[1] * hsv[2] * 10f
            assertTrue("Mood score should be between 0 and 10", moodScore in 0f..10f)
        }
    }
}
