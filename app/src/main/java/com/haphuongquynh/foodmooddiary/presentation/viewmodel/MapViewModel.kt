package com.haphuongquynh.foodmooddiary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.repository.FoodEntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Map Screen
 * Day 21: Google Maps Integration
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val foodEntryRepository: FoodEntryRepository
) : ViewModel() {

    private val _entriesWithLocation = MutableStateFlow<List<FoodEntry>>(emptyList())
    val entriesWithLocation: StateFlow<List<FoodEntry>> = _entriesWithLocation.asStateFlow()

    private val _selectedEntry = MutableStateFlow<FoodEntry?>(null)
    val selectedEntry: StateFlow<FoodEntry?> = _selectedEntry.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadEntriesWithLocation()
    }

    /**
     * Load all food entries that have location data
     */
    private fun loadEntriesWithLocation() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                foodEntryRepository.getAllEntries().collect { entries ->
                    // Filter entries that have location
                    _entriesWithLocation.value = entries.filter { 
                        it.location != null
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Select entry to show details
     */
    fun selectEntry(entry: FoodEntry?) {
        _selectedEntry.value = entry
    }

    /**
     * Get all unique locations for heat map
     */
    fun getHeatMapData(): List<Pair<Double, Double>> {
        return _entriesWithLocation.value.mapNotNull { entry ->
            entry.location?.let { location ->
                Pair(location.latitude, location.longitude)
            }
        }
    }

    /**
     * Filter entries by mood color range
     */
    fun filterByMoodColor(colorRange: IntRange?) {
        viewModelScope.launch {
            foodEntryRepository.getAllEntries().collect { allEntries ->
                _entriesWithLocation.value = if (colorRange != null) {
                    allEntries.filter { entry ->
                        entry.location != null &&
                        entry.moodColor in colorRange
                    }
                } else {
                    allEntries.filter { entry ->
                        entry.location != null
                    }
                }
            }
        }
    }

    /**
     * Refresh entries
     */
    fun refresh() {
        loadEntriesWithLocation()
    }
}
