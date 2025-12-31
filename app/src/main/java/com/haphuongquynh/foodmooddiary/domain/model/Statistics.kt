package com.haphuongquynh.foodmooddiary.domain.model

/**
 * Statistics models for data aggregation and visualization
 */

/**
 * Mood trend data point for line chart
 */
data class MoodTrendPoint(
    val date: Long,
    val averageMoodScore: Float,
    val entryCount: Int
)

/**
 * Food frequency data for bar chart
 */
data class FoodFrequency(
    val foodName: String,
    val count: Int,
    val averageMoodScore: Float
)

/**
 * Meal type distribution for pie chart
 */
data class MealDistribution(
    val mealType: MealType,
    val count: Int,
    val percentage: Float
)

enum class MealType {
    BREAKFAST, LUNCH, DINNER, SNACK;
    
    companion object {
        fun fromString(value: String): MealType {
            return try {
                valueOf(value.uppercase())
            } catch (e: Exception) {
                DINNER // Default to Dinner
            }
        }
    }
}

/**
 * Color distribution for pie chart
 */
data class ColorDistribution(
    val colorValue: Int,
    val colorName: String,
    val count: Int,
    val percentage: Float
)

/**
 * Weekly statistics summary
 */
data class WeeklySummary(
    val weekStartDate: Long,
    val totalEntries: Int,
    val averageMoodScore: Float,
    val mostFrequentFood: String?,
    val dominantColor: Int,
    val streak: Int
)

/**
 * Insights generated from pattern analysis
 */
data class Insight(
    val id: String,
    val title: String,
    val description: String,
    val type: InsightType,
    val actionable: Boolean = false,
    val icon: String = "lightbulb"
)

enum class InsightType {
    MOOD_PATTERN,      // "Your mood improves after breakfast"
    FOOD_CORRELATION,  // "Pizza seems to brighten your day"
    TIME_PATTERN,      // "You eat most frequently at 7 PM"
    COLOR_PATTERN,     // "Green foods dominate your meals"
    STREAK,            // "5 days logging streak!"
    RECOMMENDATION     // "Try adding more vegetables"
}
