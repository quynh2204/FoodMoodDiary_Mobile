package com.haphuongquynh.foodmooddiary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haphuongquynh.foodmooddiary.domain.model.Meal
import com.haphuongquynh.foodmooddiary.domain.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Discovery feature
 */
@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _currentMeal = MutableStateFlow<Meal?>(null)
    val currentMeal: StateFlow<Meal?> = _currentMeal.asStateFlow()

    private val _favorites = MutableStateFlow<List<Meal>>(emptyList())
    val favorites: StateFlow<List<Meal>> = _favorites.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Meal>>(emptyList())
    val searchResults: StateFlow<List<Meal>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _areas = MutableStateFlow<List<String>>(emptyList())
    val areas: StateFlow<List<String>> = _areas.asStateFlow()

    init {
        loadFavorites()
        loadCategories()
        loadAreas()
        loadRandomMeal()
    }

    fun loadRandomMeal() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            mealRepository.getRandomMeal()
                .onSuccess { meal ->
                    _currentMeal.value = meal
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load random meal"
                    _isLoading.value = false
                }
        }
    }

    fun searchMealsByName(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            mealRepository.searchMealsByName(query)
                .onSuccess { meals ->
                    _searchResults.value = meals
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to search meals"
                    _isLoading.value = false
                }
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            mealRepository.filterByCategory(category)
                .onSuccess { meals ->
                    _searchResults.value = meals
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to filter by category"
                    _isLoading.value = false
                }
        }
    }

    fun filterByArea(area: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            mealRepository.filterByArea(area)
                .onSuccess { meals ->
                    _searchResults.value = meals
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to filter by area"
                    _isLoading.value = false
                }
        }
    }

    fun loadMealById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            mealRepository.getMealById(id)
                .onSuccess { meal ->
                    _currentMeal.value = meal
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load meal"
                    _isLoading.value = false
                }
        }
    }

    fun toggleFavorite(meal: Meal) {
        viewModelScope.launch {
            if (meal.isFavorite) {
                mealRepository.removeFromFavorites(meal.id)
                    .onSuccess {
                        _currentMeal.value = _currentMeal.value?.copy(isFavorite = false)
                        loadFavorites()
                    }
                    .onFailure { exception ->
                        _error.value = exception.message ?: "Failed to remove from favorites"
                    }
            } else {
                mealRepository.addToFavorites(meal)
                    .onSuccess {
                        _currentMeal.value = _currentMeal.value?.copy(isFavorite = true)
                        loadFavorites()
                    }
                    .onFailure { exception ->
                        _error.value = exception.message ?: "Failed to add to favorites"
                    }
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            mealRepository.getFavoriteMeals().collect { meals ->
                _favorites.value = meals
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            mealRepository.getAllCategories().onSuccess { categories ->
                _categories.value = categories
            }
        }
    }

    private fun loadAreas() {
        viewModelScope.launch {
            mealRepository.getAllAreas().onSuccess { areas ->
                _areas.value = areas
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }
}
