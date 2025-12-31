package com.haphuongquynh.foodmooddiary.data.repository

import android.graphics.Color
import com.google.firebase.auth.FirebaseAuth
import com.haphuongquynh.foodmooddiary.data.local.dao.FoodEntryDao
import com.haphuongquynh.foodmooddiary.domain.model.*
import com.haphuongquynh.foodmooddiary.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of StatisticsRepository
 * Performs data aggregation and analysis on local database
 */
@Singleton
class StatisticsRepositoryImpl @Inject constructor(
    private val foodEntryDao: FoodEntryDao,
    private val firebaseAuth: FirebaseAuth
) : StatisticsRepository {

    private val currentUserId: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    override fun getMoodTrend(startDate: Long, endDate: Long): Flow<List<MoodTrendPoint>> {
        return foodEntryDao.getEntriesInDateRange(currentUserId, startDate, endDate)
            .map { entries ->
                // Group by day and calculate average mood score
                entries.groupBy { getDayStartTimestamp(it.timestamp) }
                    .map { (dayTimestamp, dayEntries) ->
                        MoodTrendPoint(
                            date = dayTimestamp,
                            averageMoodScore = calculateAverageMoodScore(dayEntries.map { it.moodColor }),
                            entryCount = dayEntries.size
                        )
                    }
                    .sortedBy { it.date }
            }
    }

    override fun getTopFoods(startDate: Long, endDate: Long, limit: Int): Flow<List<FoodFrequency>> {
        return foodEntryDao.getEntriesInDateRange(currentUserId, startDate, endDate)
            .map { entries ->
                entries.groupBy { it.foodName.lowercase().trim() }
                    .map { (foodName, foodEntries) ->
                        FoodFrequency(
                            foodName = foodName.replaceFirstChar { it.uppercase() },
                            count = foodEntries.size,
                            averageMoodScore = calculateAverageMoodScore(foodEntries.map { it.moodColor })
                        )
                    }
                    .sortedByDescending { it.count }
                    .take(limit)
            }
    }

    override fun getMealDistribution(startDate: Long, endDate: Long): Flow<List<MealDistribution>> {
        return foodEntryDao.getEntriesInDateRange(currentUserId, startDate, endDate)
            .map { entries ->
                val total = entries.size.toFloat()
                if (total == 0f) return@map emptyList()

                // Use actual mealType stored in entries
                val distribution = entries.groupBy { it.mealType }
                    .map { (mealType, mealEntries) ->
                        MealDistribution(
                            mealType = MealType.fromString(mealType),
                            count = mealEntries.size,
                            percentage = (mealEntries.size / total) * 100f
                        )
                    }
                    .sortedByDescending { it.count }

                distribution
            }
    }

    override fun getColorDistribution(startDate: Long, endDate: Long): Flow<List<ColorDistribution>> {
        return foodEntryDao.getEntriesInDateRange(currentUserId, startDate, endDate)
            .map { entries ->
                val total = entries.size.toFloat()
                if (total == 0f) return@map emptyList()

                entries.groupBy { categorizeColor(it.moodColor) }
                    .map { (colorCategory, colorEntries) ->
                        ColorDistribution(
                            colorValue = colorEntries.first().moodColor,
                            colorName = colorCategory,
                            count = colorEntries.size,
                            percentage = (colorEntries.size / total) * 100f
                        )
                    }
                    .sortedByDescending { it.count }
            }
    }

    override fun getWeeklySummary(weekStartDate: Long): Flow<WeeklySummary?> {
        val weekEndDate = weekStartDate + (7 * 24 * 60 * 60 * 1000L)
        
        return foodEntryDao.getEntriesInDateRange(currentUserId, weekStartDate, weekEndDate)
            .map { entries ->
                if (entries.isEmpty()) return@map null

                val topFood = entries.groupBy { it.foodName }
                    .maxByOrNull { it.value.size }
                    ?.key

                val dominantColor = entries.groupBy { it.moodColor }
                    .maxByOrNull { it.value.size }
                    ?.key ?: Color.GRAY

                WeeklySummary(
                    weekStartDate = weekStartDate,
                    totalEntries = entries.size,
                    averageMoodScore = calculateAverageMoodScore(entries.map { it.moodColor }),
                    mostFrequentFood = topFood,
                    dominantColor = dominantColor,
                    streak = 0 // Will be calculated separately
                )
            }
    }

    override fun generateInsights(startDate: Long, endDate: Long): Flow<List<Insight>> {
        return foodEntryDao.getEntriesInDateRange(currentUserId, startDate, endDate)
            .map { entries ->
                val insights = mutableListOf<Insight>()

                // Mood pattern insight
                val avgMoodScore = calculateAverageMoodScore(entries.map { it.moodColor })
                if (avgMoodScore > 7f) {
                    insights.add(
                        Insight(
                            id = "mood_positive",
                            title = "Great Mood Trend! 🌟",
                            description = "Your average mood score is ${avgMoodScore.toInt()}/10. Keep it up!",
                            type = InsightType.MOOD_PATTERN
                        )
                    )
                }

                // Food correlation insight
                val topFoods = entries.groupBy { it.foodName }
                    .map { (name, list) -> 
                        name to calculateAverageMoodScore(list.map { it.moodColor }) 
                    }
                    .sortedByDescending { it.second }

                if (topFoods.isNotEmpty()) {
                    val (happyFood, score) = topFoods.first()
                    insights.add(
                        Insight(
                            id = "food_happy",
                            title = "$happyFood makes you happy! 😊",
                            description = "Your mood averages ${score.toInt()}/10 when eating $happyFood",
                            type = InsightType.FOOD_CORRELATION,
                            actionable = true
                        )
                    )
                }

                // Time pattern insight
                val timeDistribution = entries.groupBy { 
                    Calendar.getInstance().apply { timeInMillis = it.timestamp }.get(Calendar.HOUR_OF_DAY)
                }
                val peakHour = timeDistribution.maxByOrNull { it.value.size }?.key
                if (peakHour != null) {
                    insights.add(
                        Insight(
                            id = "time_pattern",
                            title = "Peak Eating Time ⏰",
                            description = "You log most meals around ${peakHour}:00",
                            type = InsightType.TIME_PATTERN
                        )
                    )
                }

                // Color pattern
                val dominantColorName = entries.groupBy { categorizeColor(it.moodColor) }
                    .maxByOrNull { it.value.size }
                    ?.key
                if (dominantColorName != null) {
                    insights.add(
                        Insight(
                            id = "color_pattern",
                            title = "$dominantColorName Vibes 🎨",
                            description = "$dominantColorName colors dominate your food choices",
                            type = InsightType.COLOR_PATTERN
                        )
                    )
                }

                insights
            }
    }

    override suspend fun getCurrentStreak(): Int {
        val allEntries = foodEntryDao.getAllEntries(currentUserId)
        // This would need to be calculated properly
        // For now, return 0 as placeholder
        return 0
    }

    /**
     * Helper: Get day start timestamp (midnight)
     */
    private fun getDayStartTimestamp(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /**
     * Helper: Calculate mood score from color (0-10 scale)
     * Uses hue to determine mood (warm colors = positive mood)
     * Saturation and brightness adjust the intensity
     */
    private fun calculateAverageMoodScore(colors: List<Int>): Float {
        if (colors.isEmpty()) return 5f
        
        return colors.map { color ->
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            
            val hue = hsv[0]
            val saturation = hsv[1] // 0-1
            val brightness = hsv[2] // 0-1
            
            // Map hue to mood base score
            // Red/Orange/Yellow (0-90°) = happy (8-10)
            // Green (90-150°) = calm (6-8)  
            // Blue/Cyan (150-270°) = neutral/sad (3-5)
            // Purple/Magenta (270-360°) = creative (6-8)
            val hueScore = when {
                hue < 60 -> 8f + (hue / 60f) * 2f // Red to Yellow: 8-10
                hue < 120 -> 6f + ((hue - 60f) / 60f) * 2f // Yellow to Green: 6-8
                hue < 180 -> 5f + ((hue - 120f) / 60f) * 1f // Green to Cyan: 5-6
                hue < 240 -> 3f + ((hue - 180f) / 60f) * 2f // Cyan to Blue: 3-5
                hue < 300 -> 2f + ((hue - 240f) / 60f) * 1f // Blue to Magenta: 2-3
                else -> 4f + ((hue - 300f) / 60f) * 4f // Magenta to Red: 4-8
            }
            
            // Adjust by saturation and brightness
            (hueScore * saturation * brightness).coerceIn(0f, 10f)
        }.average().toFloat()
    }

    /**
     * Helper: Categorize meal type by time
     */
    private fun getMealTypeFromTimestamp(timestamp: Long): MealType {
        val hour = Calendar.getInstance().apply { 
            timeInMillis = timestamp 
        }.get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 6..10 -> MealType.BREAKFAST
            in 11..14 -> MealType.LUNCH
            in 17..21 -> MealType.DINNER
            else -> MealType.SNACK
        }
    }

    /**
     * Helper: Categorize color by hue
     */
    private fun categorizeColor(color: Int): String {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val hue = hsv[0]

        return when {
            hue < 30 -> "Red"
            hue < 60 -> "Orange"
            hue < 90 -> "Yellow"
            hue < 150 -> "Green"
            hue < 210 -> "Cyan"
            hue < 270 -> "Blue"
            hue < 330 -> "Purple"
            else -> "Red"
        }
    }
}
