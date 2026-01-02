package com.haphuongquynh.foodmooddiary.domain.model

import androidx.compose.ui.graphics.Color

/**
 * 5 Core Mood Types for Food Mood Diary
 * User selects mood when adding food entry
 *
 * score: điểm quy đổi cảm xúc (0–10)
 */
enum class MoodType(
    val emoji: String,
    val label: String,
    val labelVi: String,
    val color: Color,
    val colorInt: Int,
    val score: Float               // ĐIỂM QUY ĐỔI
) {
    HAPPY(
        emoji = "😊",
        label = "Happy",
        labelVi = "Vui vẻ",
        color = Color(0xFFFFD93D),
        colorInt = 0xFFFFD93D.toInt(),
        score = 7.5f
    ),

    SAD(
        emoji = "😢",
        label = "Sad",
        labelVi = "Buồn",
        color = Color(0xFF6C9BCF),
        colorInt = 0xFF6C9BCF.toInt(),
        score = 2.0f
    ),

    ANGRY(
        emoji = "😠",
        label = "Angry",
        labelVi = "Tức giận",
        color = Color(0xFFFF6B6B),
        colorInt = 0xFFFF6B6B.toInt(),
        score = 3.5f
    ),

    TIRED(
        emoji = "😫",
        label = "Tired",
        labelVi = "Mệt mỏi",
        color = Color(0xFF95A5A6),
        colorInt = 0xFF95A5A6.toInt(),
        score = 4.5f
    ),

    ENERGETIC(
        emoji = "💪",
        label = "Energetic",
        labelVi = "Năng lượng",
        color = Color(0xFF4ECDC4),
        colorInt = 0xFF4ECDC4.toInt(),
        score = 9.0f
    );

    companion object {

        /** Tìm Mood từ emoji */
        fun fromEmoji(emoji: String): MoodType? =
            entries.find { it.emoji == emoji }

        /** Tìm Mood từ label EN / VI */
        fun fromLabel(label: String): MoodType? =
            entries.find {
                it.label.equals(label, ignoreCase = true) ||
                it.labelVi.equals(label, ignoreCase = true)
            }

        /** Tìm Mood từ màu - với fallback */
        fun fromColorInt(colorInt: Int): MoodType? {
            // Exact match first
            entries.find { it.colorInt == colorInt }?.let { return it }
            
            // Fallback: if colorInt is non-zero, try to guess based on color
            if (colorInt != 0) {
                // Extract RGB components (handle signed int)
                val r = (colorInt shr 16) and 0xFF
                val g = (colorInt shr 8) and 0xFF
                val b = colorInt and 0xFF
                
                // Log for debugging
                android.util.Log.d("MoodType", "Analyzing color: R=$r, G=$g, B=$b (raw=$colorInt)")
                
                // Improved heuristic based on color characteristics
                return when {
                    // Yellow/Orange tones (HAPPY) - high red, medium-high green, low blue
                    r > 200 && g > 100 && b < 100 -> HAPPY
                    // Pure yellow
                    r > 220 && g > 200 && b < 80 -> HAPPY
                    // Red tones (ANGRY)
                    r > 200 && g < 120 && b < 120 -> ANGRY
                    // Blue tones (SAD)
                    b > 150 && r < 150 && g < 180 -> SAD
                    // Gray tones (TIRED)
                    r in 100..180 && g in 100..180 && b in 100..180 -> TIRED
                    // Cyan/Green tones (ENERGETIC)
                    g > 180 && b > 150 && r < 150 -> ENERGETIC
                    // Orange (could be HAPPY or warm feeling)
                    r > 200 && g in 100..200 && b < 100 -> HAPPY
                    else -> {
                        android.util.Log.d("MoodType", "No match found, defaulting to HAPPY")
                        HAPPY // Default fallback
                    }
                }
            }
            return null
        }
    }
}