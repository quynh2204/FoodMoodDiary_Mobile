package com.haphuongquynh.foodmooddiary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.haphuongquynh.foodmooddiary.data.local.dao.FoodEntryDao
import com.haphuongquynh.foodmooddiary.data.local.entity.FoodEntryEntity
import com.haphuongquynh.foodmooddiary.domain.model.*
import com.haphuongquynh.foodmooddiary.domain.repository.StatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * ViewModel for Statistics Screen
 * Handles chart data preparation and insights generation
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository,
    private val foodEntryDao: FoodEntryDao,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    // Date range for filtering
    private val _dateRange = MutableStateFlow(DateRange.LAST_7_DAYS)
    val dateRange: StateFlow<DateRange> = _dateRange.asStateFlow()

    // Current date range timestamps
    private val currentDateRange: Flow<Pair<Long, Long>> = _dateRange.map { range ->
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_YEAR, -range.days)
        val startDate = calendar.timeInMillis

        startDate to endDate
    }

    // Mood trend data for line chart
    val moodTrend: StateFlow<List<MoodTrendPoint>> = currentDateRange
        .flatMapLatest { (start, end) ->
            statisticsRepository.getMoodTrend(start, end)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Top foods data for bar chart
    val topFoods: StateFlow<List<FoodFrequency>> = currentDateRange
        .flatMapLatest { (start, end) ->
            statisticsRepository.getTopFoods(start, end, limit = 10)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Meal distribution for pie chart
    val mealDistribution: StateFlow<List<MealDistribution>> = currentDateRange
        .flatMapLatest { (start, end) ->
            statisticsRepository.getMealDistribution(start, end)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Color distribution for pie chart
    val colorDistribution: StateFlow<List<ColorDistribution>> = currentDateRange
        .flatMapLatest { (start, end) ->
            statisticsRepository.getColorDistribution(start, end)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // AI-generated insights
    val insights: StateFlow<List<Insight>> = currentDateRange
        .flatMapLatest { (start, end) ->
            statisticsRepository.generateInsights(start, end)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Weekly summary
    val weeklySummary: StateFlow<WeeklySummary?> = flow {
        val calendar = Calendar.getInstance()
        // Get start of current week (Monday)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        
        statisticsRepository.getWeeklySummary(calendar.timeInMillis).collect { emit(it) }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /**
     * Change date range filter
     */
    fun setDateRange(range: DateRange) {
        _dateRange.value = range
    }

    /**
     * Get current streak
     */
    fun getCurrentStreak(callback: (Int) -> Unit) {
        viewModelScope.launch {
            val streak = statisticsRepository.getCurrentStreak()
            callback(streak)
        }
    }

    /**
     * DEBUG: Generate fake test data for multiple days
     * Call this to populate the database with sample entries
     */
    fun generateTestData() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val now = System.currentTimeMillis()

            // Sample foods with Vietnamese names
            val foods = listOf(
                "Phở bò" to "Breakfast",
                "Bánh mì" to "Breakfast",
                "Cơm tấm" to "Lunch",
                "Bún chả" to "Lunch",
                "Cơm gà" to "Dinner",
                "Hủ tiếu" to "Dinner",
                "Trà sữa" to "Snack",
                "Bánh tráng trộn" to "Snack"
            )

            // MoodType emojis and their colors
            val moods = listOf(
                MoodType.HAPPY,
                MoodType.SAD,
                MoodType.ANGRY,
                MoodType.TIRED,
                MoodType.ENERGETIC
            )

            val entries = mutableListOf<FoodEntryEntity>()

            // Generate entries for past 14 days
            for (dayOffset in 0..13) {
                calendar.timeInMillis = now
                calendar.add(Calendar.DAY_OF_YEAR, -dayOffset)

                // 1-3 meals per day
                val mealsPerDay = (1..3).random()

                for (mealIndex in 0 until mealsPerDay) {
                    val (foodName, mealType) = foods.random()
                    val mood = moods.random()

                    // Set time based on meal type
                    when (mealType) {
                        "Breakfast" -> calendar.set(Calendar.HOUR_OF_DAY, (7..9).random())
                        "Lunch" -> calendar.set(Calendar.HOUR_OF_DAY, (11..13).random())
                        "Dinner" -> calendar.set(Calendar.HOUR_OF_DAY, (18..20).random())
                        else -> calendar.set(Calendar.HOUR_OF_DAY, (14..16).random())
                    }
                    calendar.set(Calendar.MINUTE, (0..59).random())

                    val timestamp = calendar.timeInMillis

                    entries.add(
                        FoodEntryEntity(
                            id = UUID.randomUUID().toString(),
                            userId = userId,
                            foodName = foodName,
                            notes = "Test entry",
                            photoUrl = null,
                            localPhotoPath = null,
                            moodColor = mood.colorInt,
                            mood = mood.emoji,
                            mealType = mealType,
                            location = null,
                            timestamp = timestamp,
                            createdAt = timestamp,
                            updatedAt = timestamp,
                            isSynced = false
                        )
                    )
                }
            }

            // Insert all entries
            foodEntryDao.insertEntries(entries)
        }
    }

    /**
     * DEBUG: Clear all test data
     */
    fun clearTestData() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            foodEntryDao.deleteAllEntriesForUser(userId)
        }
    }
}

/**
 * Date range options for filtering
 */
enum class DateRange(val days: Int, val label: String) {
    LAST_7_DAYS(7, "Last 7 Days"),
    LAST_30_DAYS(30, "Last 30 Days"),
    LAST_90_DAYS(90, "Last 3 Months"),
    LAST_YEAR(365, "Last Year"),
    ALL_TIME(Int.MAX_VALUE, "All Time")
}
