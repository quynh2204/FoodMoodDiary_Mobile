package com.haphuongquynh.foodmooddiary.domain.model

import androidx.compose.ui.graphics.Color

/**
 * 5 Core Mood Types for Food Mood Diary
 * User selects mood when adding food entry
 */
enum class MoodType(
    val emoji: String,
    val label: String,
    val labelVi: String,
    val color: Color,
    val colorInt: Int
) {
    HAPPY(
        emoji = "😊",
        label = "Happy",
        labelVi = "Vui vẻ",
        color = Color(0xFFFFD93D),
        colorInt = 0xFFFFD93D.toInt()
    ),
    SAD(
        emoji = "😢",
        label = "Sad", 
        labelVi = "Buồn",
        color = Color(0xFF6C9BCF),
        colorInt = 0xFF6C9BCF.toInt()
    ),
    ANGRY(
        emoji = "😠",
        label = "Angry",
        labelVi = "Tức giận",
        color = Color(0xFFFF6B6B),
        colorInt = 0xFFFF6B6B.toInt()
    ),
    TIRED(
        emoji = "😫",
        label = "Tired",
        labelVi = "Mệt mỏi",
        color = Color(0xFF95A5A6),
        colorInt = 0xFF95A5A6.toInt()
    ),
    ENERGETIC(
        emoji = "💪",
        label = "Energetic",
        labelVi = "Năng lượng",
        color = Color(0xFF4ECDC4),
        colorInt = 0xFF4ECDC4.toInt()
    );
    
    companion object {
        fun fromEmoji(emoji: String): MoodType? {
            return entries.find { it.emoji == emoji }
        }
        
        fun fromLabel(label: String): MoodType? {
            return entries.find { 
                it.label.equals(label, ignoreCase = true) || 
                it.labelVi.equals(label, ignoreCase = true) 
            }
        }
        
        fun fromColorInt(colorInt: Int): MoodType? {
            return entries.find { it.colorInt == colorInt }
        }
    }
}

/**
 * Mood Analysis Result based on entries
 */
data class MoodAnalysis(
    val dominantMood: MoodType?,
    val moodCounts: Map<MoodType, Int>,
    val totalEntries: Int,
    val happyPercentage: Int,
    val insight: String,
    val suggestion: String
)

/**
 * Nutrition Summary without API
 */
data class LocalNutritionSummary(
    val totalCalories: Int,
    val avgCaloriesPerMeal: Int,
    val totalProtein: Int,
    val totalCarbs: Int,
    val totalFat: Int,
    val mealCount: Int,
    val rating: String,
    val insight: String
)

/**
 * Color Palette Analysis based on mood colors
 */
data class LocalColorAnalysis(
    val dominantMood: MoodType?,
    val colorDistribution: Map<MoodType, Int>,
    val insight: String,
    val suggestion: String
)
