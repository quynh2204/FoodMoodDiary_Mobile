package com.haphuongquynh.foodmooddiary.util.color

import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import javax.inject.Inject

/**
 * Utility class for analyzing colors from images
 * Uses Android Palette API to extract dominant colors
 */
class ColorAnalyzer @Inject constructor() {

    /**
     * Extract dominant color from bitmap
     * @param bitmap Image to analyze
     * @return ARGB color value (most vibrant color or dominant color)
     */
    fun extractDominantColor(bitmap: Bitmap): Int {
        val palette = Palette.from(bitmap).generate()
        
        // Priority order: Vibrant > Light Vibrant > Dark Vibrant > Dominant
        return palette.vibrantSwatch?.rgb
            ?: palette.lightVibrantSwatch?.rgb
            ?: palette.darkVibrantSwatch?.rgb
            ?: palette.dominantSwatch?.rgb
            ?: android.graphics.Color.GRAY // Default fallback
    }

    /**
     * Extract multiple colors from bitmap
     * @param bitmap Image to analyze
     * @return List of color swatches with their properties
     */
    fun extractColorPalette(bitmap: Bitmap): List<ColorSwatch> {
        val palette = Palette.from(bitmap).generate()
        val swatches = mutableListOf<ColorSwatch>()

        palette.vibrantSwatch?.let {
            swatches.add(ColorSwatch("Vibrant", it.rgb, it.population))
        }
        palette.lightVibrantSwatch?.let {
            swatches.add(ColorSwatch("Light Vibrant", it.rgb, it.population))
        }
        palette.darkVibrantSwatch?.let {
            swatches.add(ColorSwatch("Dark Vibrant", it.rgb, it.population))
        }
        palette.mutedSwatch?.let {
            swatches.add(ColorSwatch("Muted", it.rgb, it.population))
        }
        palette.lightMutedSwatch?.let {
            swatches.add(ColorSwatch("Light Muted", it.rgb, it.population))
        }
        palette.darkMutedSwatch?.let {
            swatches.add(ColorSwatch("Dark Muted", it.rgb, it.population))
        }

        return swatches
    }

    /**
     * Get complementary color for text on given background
     */
    fun getTextColor(backgroundColor: Int): Int {
        val darkness = 1 - (0.299 * android.graphics.Color.red(backgroundColor) +
                0.587 * android.graphics.Color.green(backgroundColor) +
                0.114 * android.graphics.Color.blue(backgroundColor)) / 255
        return if (darkness < 0.5) android.graphics.Color.BLACK else android.graphics.Color.WHITE
    }

    /**
     * Map mood emoji to representative color
     * This color is used to calculate mood score based on HSV
     */
    fun getMoodColor(moodEmoji: String): Int {
        return when (moodEmoji) {
            "😊" -> android.graphics.Color.parseColor("#FFC107") // Yellow - Happy
            "😌" -> android.graphics.Color.parseColor("#81C784") // Green - Calm
            "😔" -> android.graphics.Color.parseColor("#42A5F5") // Blue - Sad
            "😫" -> android.graphics.Color.parseColor("#EF5350") // Red - Stressed
            "🎉" -> android.graphics.Color.parseColor("#FF7043") // Orange - Celebration
            "💪" -> android.graphics.Color.parseColor("#AB47BC") // Purple - Strong/Motivated
            else -> android.graphics.Color.parseColor("#9E9E9E") // Gray - Unknown
        }
    }
}

/**
 * Data class representing a color swatch
 */
data class ColorSwatch(
    val name: String,
    val rgb: Int,
    val population: Int
)
