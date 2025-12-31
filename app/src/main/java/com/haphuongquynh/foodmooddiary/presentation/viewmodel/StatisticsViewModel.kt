package com.haphuongquynh.foodmooddiary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val statisticsRepository: StatisticsRepository
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
}

/**
 * Date range options for filtering
 */
enum class DateRange(val days: Int, val label: String) {
    LAST_7_DAYS(7, "7 ngày"),
    LAST_14_DAYS(14, "14 ngày"),
    LAST_30_DAYS(30, "30 ngày"),
    LAST_90_DAYS(90, "90 ngày"),
    LAST_YEAR(365, "12 tháng"),
    ALL_TIME(Int.MAX_VALUE, "Tất cả")
}
