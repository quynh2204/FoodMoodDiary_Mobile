package com.haphuongquynh.foodmooddiary.domain.repository

import com.haphuongquynh.foodmooddiary.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for statistics and analytics
 */
interface StatisticsRepository {
    
    /**
     * Get mood trend over time
     * @param startDate Start timestamp
     * @param endDate End timestamp
     * @return Flow of mood trend points
     */
    fun getMoodTrend(startDate: Long, endDate: Long): Flow<List<MoodTrendPoint>>
    
    /**
     * Get top foods by frequency
     * @param startDate Start timestamp
     * @param endDate End timestamp
     * @param limit Max number of results
     * @return Flow of food frequency data
     */
    fun getTopFoods(startDate: Long, endDate: Long, limit: Int = 10): Flow<List<FoodFrequency>>
    
    /**
     * Get meal type distribution
     * @param startDate Start timestamp
     * @param endDate End timestamp
     * @return Flow of meal distribution data
     */
    fun getMealDistribution(startDate: Long, endDate: Long): Flow<List<MealDistribution>>
    
    /**
     * Get color distribution
     * @param startDate Start timestamp
     * @param endDate End timestamp
     * @return Flow of color distribution data
     */
    fun getColorDistribution(startDate: Long, endDate: Long): Flow<List<ColorDistribution>>
    
    /**
     * Get weekly summary
     * @param weekStartDate Start of week timestamp
     * @return Flow of weekly summary
     */
    fun getWeeklySummary(weekStartDate: Long): Flow<WeeklySummary?>
    
    /**
     * Generate AI insights from entries
     * @param startDate Start timestamp
     * @param endDate End timestamp
     * @return Flow of insights
     */
    fun generateInsights(startDate: Long, endDate: Long): Flow<List<Insight>>
    
    /**
     * Calculate current streak (consecutive days with entries)
     * @return Current streak count
     */
    suspend fun getCurrentStreak(): Int

    /**
     * Get total entry count (all time)
     * @return Flow of total count
     */
    fun getTotalEntryCount(): Flow<Int>

    /**
     * Get most logged food name (all time)
     * @return Flow of top food name
     */
    fun getTopFoodAllTime(): Flow<String?>

    /**
     * Get average mood score (all time)
     * @return Flow of average mood score (0-10)
     */
    fun getAverageMoodAllTime(): Flow<Float>
}
