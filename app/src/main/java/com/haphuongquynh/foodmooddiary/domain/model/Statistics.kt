package com.haphuongquynh.foodmooddiary.domain.model

/**
 * Statistics models for data aggregation and visualization
 */

/**
 * Mood trend data point for line chart
 * Includes individual entries for calendar day preview
 */
data class MoodTrendPoint(
    val date: Long,
    val averageMoodScore: Float,
    val entryCount: Int,
    val entries: List<DayEntry> = emptyList()
)

/**
 * Individual entry data for calendar day preview
 */
data class DayEntry(
    val id: String,
    val foodName: String,
    val mood: String?,        // emoji
    val photoUrl: String?,
    val localPhotoPath: String?,
    val timestamp: Long
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
    BREAKFAST, LUNCH, DINNER, SNACK
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

/**
 * Local mood analysis result (used by HomeAIViewModel)
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
 * Local color analysis result (used by HomeAIViewModel)
 */
data class LocalColorAnalysis(
    val dominantMood: MoodType?,
    val colorDistribution: Map<MoodType, Int>,
    val insight: String,
    val suggestion: String
)
