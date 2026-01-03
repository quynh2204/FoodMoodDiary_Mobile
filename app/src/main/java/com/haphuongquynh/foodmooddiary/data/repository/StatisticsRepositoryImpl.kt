package com.haphuongquynh.foodmooddiary.data.repository

import android.graphics.Color
import com.google.firebase.auth.FirebaseAuth
import com.haphuongquynh.foodmooddiary.data.local.dao.FoodEntryDao
import com.haphuongquynh.foodmooddiary.domain.model.*
import com.haphuongquynh.foodmooddiary.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
                            averageMoodScore = calculateAverageMoodScore(dayEntries.map { it.mood }),
                            entryCount = dayEntries.size,
                            entries = dayEntries.map { entry ->
                                DayEntry(
                                    id = entry.id,
                                    foodName = entry.foodName,
                                    mood = entry.mood,
                                    photoUrl = entry.photoUrl,
                                    localPhotoPath = entry.localPhotoPath,
                                    timestamp = entry.timestamp
                                )
                            }.sortedByDescending { it.timestamp }
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
                            averageMoodScore = calculateAverageMoodScore(foodEntries.map { it.mood })
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

                // Use mealType from entry if available, fallback to timestamp-based detection
                val distribution = entries.groupBy { entry ->
                    entry.mealType?.let { parseMealType(it) } ?: getMealTypeFromTimestamp(entry.timestamp)
                }
                    .map { (mealType, mealEntries) ->
                        MealDistribution(
                            mealType = mealType,
                            count = mealEntries.size,
                            percentage = (mealEntries.size / total) * 100f
                        )
                    }
                    .sortedByDescending { it.count }

                distribution
            }
    }

    /**
     * Helper: Parse mealType string to MealType enum
     */
    private fun parseMealType(mealType: String): MealType {
        return when (mealType.lowercase()) {
            "breakfast", "sáng" -> MealType.BREAKFAST
            "lunch", "trưa" -> MealType.LUNCH
            "dinner", "tối" -> MealType.DINNER
            else -> MealType.SNACK
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
                    averageMoodScore = calculateAverageMoodScore(entries.map { it.mood }),
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
                val avgMoodScore = calculateAverageMoodScore(entries.map { it.mood })
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
                        name to calculateAverageMoodScore(list.map { it.mood })
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
        val allEntries = foodEntryDao.getAllEntriesOnce(currentUserId)
        if (allEntries.isEmpty()) return 0

        // Calculate streak: consecutive days with entries ending today
        val sortedDays = allEntries
            .map { getDayStartTimestamp(it.timestamp) }
            .distinct()
            .sortedDescending()

        val today = getDayStartTimestamp(System.currentTimeMillis())
        val oneDayMs = 24 * 60 * 60 * 1000L

        // If no entry today, streak is 0
        if (sortedDays.firstOrNull() != today) return 0

        var streak = 1
        var expectedDay = today - oneDayMs

        for (i in 1 until sortedDays.size) {
            if (sortedDays[i] == expectedDay) {
                streak++
                expectedDay -= oneDayMs
            } else {
                break
            }
        }

        return streak
    }

    override fun getTotalEntryCount(): Flow<Int> = flow {
        val entries = foodEntryDao.getAllEntriesOnce(currentUserId)
        emit(entries.size)
    }

    override fun getTopFoodAllTime(): Flow<String?> = flow {
        val entries = foodEntryDao.getAllEntriesOnce(currentUserId)
        val topFood = entries.groupBy { it.foodName.lowercase().trim() }
            .maxByOrNull { it.value.size }
            ?.key
            ?.replaceFirstChar { it.uppercase() }
        emit(topFood)
    }

    override fun getAverageMoodAllTime(): Flow<Float> = flow {
        val entries = foodEntryDao.getAllEntriesOnce(currentUserId)
        if (entries.isEmpty()) {
            emit(0f)
        } else {
            emit(calculateAverageMoodScore(entries.map { it.mood }))
        }
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
     * Helper: Calculate mood score from MoodType.score (0-10 scale)
     * Uses the mood emoji to lookup MoodType.score value
     * Fallback to 5.0f if mood is null/invalid
     */
    private fun calculateAverageMoodScore(moods: List<String?>): Float {
        if (moods.isEmpty()) return 5f

        return moods.map { mood ->
            mood?.let { MoodType.fromEmoji(it)?.score } ?: 5f
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
