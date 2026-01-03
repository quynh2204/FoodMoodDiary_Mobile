package com.haphuongquynh.foodmooddiary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.model.VietnameseMeal
import com.haphuongquynh.foodmooddiary.domain.repository.FoodEntryRepository
import com.haphuongquynh.foodmooddiary.domain.repository.SavedVietnamMealRepository
import com.haphuongquynh.foodmooddiary.domain.repository.VietnameseMealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel for Discovery feature
 *
 * - Uses Firestore for Vietnamese meal data
 * - Saved meals per user account on Firebase
 */
@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val foodEntryRepository: FoodEntryRepository,
    private val savedVietnamMealRepository: SavedVietnamMealRepository,
    private val vietnameseMealRepository: VietnameseMealRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /* =============================
       Vietnamese Discover States
       ============================= */

    data class RecommendedMeal(
        val meal: VietnameseMeal,
        val reason: String,
        val score: Float
    )

    private val _vietnamMeals = MutableStateFlow<List<VietnameseMeal>>(emptyList())
    val vietnamMeals: StateFlow<List<VietnameseMeal>> = _vietnamMeals.asStateFlow()

    private val _filteredVietnamMeals = MutableStateFlow<List<VietnameseMeal>>(emptyList())
    val filteredVietnamMeals: StateFlow<List<VietnameseMeal>> = _filteredVietnamMeals.asStateFlow()

    private val _selectedMainCategory = MutableStateFlow("Tất cả")
    val selectedMainCategory: StateFlow<String> = _selectedMainCategory.asStateFlow()

    private val _recommendations = MutableStateFlow<List<RecommendedMeal>>(emptyList())
    val recommendations: StateFlow<List<RecommendedMeal>> = _recommendations.asStateFlow()

    private val _recommendationReason = MutableStateFlow("")
    val recommendationReason: StateFlow<String> = _recommendationReason.asStateFlow()

    // Saved Vietnamese meals per user (Firebase: users/{userId}/savedVietnamMeals)
    private val _savedVietnamMeals = MutableStateFlow<Set<String>>(emptySet())
    val savedVietnamMeals: StateFlow<Set<String>> = _savedVietnamMeals.asStateFlow()

    init {
        loadVietnamMeals()
        applyVietnamFilter("Tất cả")
        generateRecommendations()
        loadSavedVietnamMeals()
        _isLoading.value = false
        _error.value = null
    }

    /**
     * Load saved Vietnam meals from Firebase (real-time per user)
     */
    private fun loadSavedVietnamMeals() {
        viewModelScope.launch {
            savedVietnamMealRepository.getSavedMealIds().collect { mealIds ->
                _savedVietnamMeals.value = mealIds
            }
        }
    }

    /* =============================
       Vietnamese Discover logic
       ============================= */

    fun setVietnamCategory(category: String) {
        _selectedMainCategory.value = category
        applyVietnamFilter(category)
    }

    private fun applyVietnamFilter(category: String) {
        val all = _vietnamMeals.value
        val filtered = when (category) {
            "Món nước", "Món khô", "Tráng miệng" -> all.filter { it.category == category }
            else -> all
        }
        _filteredVietnamMeals.value = filtered
    }

    /* =============================
       Saved Vietnamese Meals (Firebase)
       ============================= */

    fun toggleSaveVietnamMeal(mealId: String) {
        viewModelScope.launch {
            // Optimistic UI update
            val current = _savedVietnamMeals.value.toMutableSet()
            val wasContained = current.contains(mealId)
            if (wasContained) {
                current.remove(mealId)
            } else {
                current.add(mealId)
            }
            _savedVietnamMeals.value = current

            // Persist to Firebase
            val result = savedVietnamMealRepository.toggleSave(mealId)
            if (result.isFailure) {
                // Revert on failure
                val reverted = _savedVietnamMeals.value.toMutableSet()
                if (wasContained) {
                    reverted.add(mealId)
                } else {
                    reverted.remove(mealId)
                }
                _savedVietnamMeals.value = reverted
            }
        }
    }

    fun isVietnamMealSaved(mealId: String): Boolean {
        return _savedVietnamMeals.value.contains(mealId)
    }

    fun getSavedVietnamMealsList(): List<VietnameseMeal> {
        return _vietnamMeals.value.filter { _savedVietnamMeals.value.contains(it.id) }
    }

    /* =============================
       Recommendation Algorithm
       ============================= */

    fun generateRecommendations() {
        viewModelScope.launch {
            try {
                val entries = foodEntryRepository.getAllEntries().first()
                val allMeals = _vietnamMeals.value

                if (allMeals.isEmpty()) {
                    return@launch
                }

                if (entries.isEmpty()) {
                    // New user - use time-based recommendations
                    val recommendations = computeTimeBasedRecommendations(allMeals)
                    _recommendations.value = recommendations
                    _recommendationReason.value = getTimeBasedMessage()
                    return@launch
                }

                val recommendations = computeRecommendations(entries, allMeals)
                _recommendations.value = recommendations
                _recommendationReason.value = "Gợi ý dựa trên sở thích của bạn"
            } catch (e: Exception) {
                // Fallback to time-based
                val allMeals = _vietnamMeals.value
                if (allMeals.isNotEmpty()) {
                    _recommendations.value = computeTimeBasedRecommendations(allMeals)
                    _recommendationReason.value = getTimeBasedMessage()
                }
            }
        }
    }

    private fun getTimeBasedMessage(): String {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (currentHour) {
            in 6..10 -> "Món ngon cho bữa sáng"
            in 11..14 -> "Món ngon cho bữa trưa"
            in 15..17 -> "Món ngon buổi chiều"
            in 18..21 -> "Món ngon cho bữa tối"
            else -> "Món ngon đêm khuya"
        }
    }

    private fun computeTimeBasedRecommendations(allMeals: List<VietnameseMeal>): List<RecommendedMeal> {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val timeBasedCategory = when (currentHour) {
            in 6..10 -> "Món nước"
            in 11..14 -> "Món khô"
            in 15..17 -> "Tráng miệng"
            in 18..21 -> "Món khô"
            else -> "Món nước"
        }

        val timeLabel = when (currentHour) {
            in 6..10 -> "Bữa sáng"
            in 11..14 -> "Bữa trưa"
            in 15..17 -> "Buổi chiều"
            in 18..21 -> "Bữa tối"
            else -> "Đêm khuya"
        }

        // Prioritize time-appropriate meals, then add variety from other categories
        val primaryMeals = allMeals.filter { it.category == timeBasedCategory }.shuffled()
        val secondaryMeals = allMeals.filter { it.category != timeBasedCategory }.shuffled()

        val result = mutableListOf<RecommendedMeal>()

        // Add 4 from primary category
        primaryMeals.take(4).forEach { meal ->
            result.add(RecommendedMeal(meal, timeLabel, 0.8f))
        }

        // Add 2 from other categories for variety
        secondaryMeals.take(2).forEach { meal ->
            result.add(RecommendedMeal(meal, "Món mới", 0.5f))
        }

        return result.take(6)
    }

    private fun computeRecommendations(
        entries: List<FoodEntry>,
        allMeals: List<VietnameseMeal>
    ): List<RecommendedMeal> {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        // Get user's recent food names (lowercase for matching)
        val recentFoods = entries.take(20).map { it.foodName.lowercase() }
        val foodFrequency = recentFoods.groupingBy { it }.eachCount()

        // Determine preferred category based on time of day
        val timeBasedCategory = when (currentHour) {
            in 6..10 -> "Món nước"      // Breakfast - soups
            in 11..14 -> "Món khô"      // Lunch - main dishes
            in 15..17 -> "Tráng miệng"  // Afternoon - desserts
            in 18..21 -> "Món khô"      // Dinner - main dishes
            else -> "Món nước"          // Late night - soups
        }

        return allMeals.map { meal ->
            var score = 0f
            var reason = ""

            // 1. Food name matching (40% weight)
            val nameMatch = recentFoods.any { recent ->
                meal.name.lowercase().contains(recent) ||
                recent.contains(meal.name.lowercase().take(4))
            }
            if (nameMatch) {
                score += 0.4f
                reason = "Tương tự"
            }

            // 2. Time-based recommendation (30% weight)
            if (meal.category == timeBasedCategory) {
                score += 0.3f
                if (reason.isEmpty()) {
                    reason = when (timeBasedCategory) {
                        "Món nước" -> "Bữa sáng"
                        "Món khô" -> "Bữa chính"
                        "Tráng miệng" -> "Buổi chiều"
                        else -> "Gợi ý"
                    }
                }
            }

            // 3. Variety bonus (30% weight) - items not eaten recently
            val notRecentlyEaten = !recentFoods.any { recent ->
                meal.name.lowercase().contains(recent) ||
                recent.contains(meal.name.lowercase())
            }
            if (notRecentlyEaten) {
                score += 0.3f
                if (reason.isEmpty()) {
                    reason = "Món mới"
                }
            }

            // Default reason
            if (reason.isEmpty()) {
                reason = "Gợi ý"
            }

            RecommendedMeal(meal, reason, score)
        }.sortedByDescending { it.score }.take(6)
    }

    private fun loadVietnamMeals() {
        viewModelScope.launch {
            vietnameseMealRepository.getAllMeals().collect { meals ->
                _vietnamMeals.value = meals
                applyVietnamFilter(_selectedMainCategory.value)
                generateRecommendations()
            }
        }
    }

    /* =============================
       Utils
       ============================= */

    fun clearError() {
        _error.value = null
    }
}
