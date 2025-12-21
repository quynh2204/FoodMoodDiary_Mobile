package com.haphuongquynh.foodmooddiary.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.model.Location
import com.haphuongquynh.foodmooddiary.domain.usecase.auth.GetCurrentUserUseCase
import com.haphuongquynh.foodmooddiary.domain.usecase.entry.AddEntryUseCase
import com.haphuongquynh.foodmooddiary.domain.usecase.entry.DeleteEntryUseCase
import com.haphuongquynh.foodmooddiary.domain.usecase.entry.GetEntriesUseCase
import com.haphuongquynh.foodmooddiary.domain.usecase.entry.UpdateEntryUseCase
import com.haphuongquynh.foodmooddiary.utils.ColorAnalyzer
import com.haphuongquynh.foodmooddiary.utils.LocationManager
import com.haphuongquynh.foodmooddiary.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth

/**
 * ViewModel for Food Entry operations
 * Handles CRUD, camera, color analysis, and location
 */
@HiltViewModel
class FoodEntryViewModel @Inject constructor(
    private val addEntryUseCase: AddEntryUseCase,
    private val getEntriesUseCase: GetEntriesUseCase,
    private val updateEntryUseCase: UpdateEntryUseCase,
    private val deleteEntryUseCase: DeleteEntryUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val colorAnalyzer: ColorAnalyzer,
    private val locationManager: LocationManager,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    // All entries list
    val entries: StateFlow<List<FoodEntry>> = getEntriesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current user
    private val currentUser = getCurrentUserUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // UI State for add/edit operations
    private val _entryState = MutableStateFlow<EntryState>(EntryState.Idle)
    val entryState: StateFlow<EntryState> = _entryState.asStateFlow()

    // Current photo data
    private val _currentPhoto = MutableStateFlow<PhotoData?>(null)
    val currentPhoto: StateFlow<PhotoData?> = _currentPhoto.asStateFlow()

    // Current location
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    /**
     * Process captured photo: extract color and save
     */
    fun processPhoto(file: File, bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                // Extract dominant color from photo
                val dominantColor = colorAnalyzer.extractDominantColor(bitmap)
                
                _currentPhoto.value = PhotoData(
                    file = file,
                    bitmap = bitmap,
                    dominantColor = dominantColor
                )
            } catch (e: Exception) {
                android.util.Log.e("FoodEntryVM", "Photo processing failed", e)
            }
        }
    }

    /**
     * Get current location
     */
    fun fetchCurrentLocation() {
        viewModelScope.launch {
            try {
                val location = locationManager.getCurrentLocation()
                _currentLocation.value = location
            } catch (e: Exception) {
                android.util.Log.e("FoodEntryVM", "Location fetch failed", e)
            }
        }
    }

    /**
     * Add new food entry
     */
    fun addEntry(
        foodName: String,
        notes: String = "",
        moodColor: Int,
        mood: String? = null,
        photoFile: File? = null,
        mealType: String = "Dinner",
        rating: Int = 0
    ) {
        viewModelScope.launch {
            _entryState.value = EntryState.Loading

            // Get userId directly from FirebaseAuth to avoid StateFlow delay issues
            val userId = firebaseAuth.currentUser?.uid
            if (userId == null) {
                _entryState.value = EntryState.Error("User not authenticated")
                return@launch
            }

            val entry = FoodEntry(
                userId = userId,
                foodName = foodName,
                notes = notes,
                moodColor = moodColor,
                mood = mood,
                localPhotoPath = _currentPhoto.value?.file?.absolutePath,
                location = _currentLocation.value,
                timestamp = System.currentTimeMillis()
            )

            val result = addEntryUseCase(entry)
            
            _entryState.value = when (result) {
                is Resource.Success -> {
                    clearCurrentData()
                    EntryState.Success("Entry added successfully")
                }
                is Resource.Error -> EntryState.Error(result.message)
                is Resource.Loading -> EntryState.Loading
            }
        }
    }

    /**
     * Update existing entry
     */
    fun updateEntry(entry: FoodEntry) {
        viewModelScope.launch {
            _entryState.value = EntryState.Loading
            
            val result = updateEntryUseCase(entry)
            
            _entryState.value = when (result) {
                is Resource.Success -> EntryState.Success("Entry updated successfully")
                is Resource.Error -> EntryState.Error(result.message)
                is Resource.Loading -> EntryState.Loading
            }
        }
    }

    /**
     * Delete entry
     */
    fun deleteEntry(entryId: String) {
        viewModelScope.launch {
            _entryState.value = EntryState.Loading
            
            val result = deleteEntryUseCase(entryId)
            
            _entryState.value = when (result) {
                is Resource.Success -> EntryState.Success("Entry deleted successfully")
                is Resource.Error -> EntryState.Error(result.message)
                is Resource.Loading -> EntryState.Loading
            }
        }
    }

    /**
     * Reset entry state
     */
    fun resetEntryState() {
        _entryState.value = EntryState.Idle
    }

    /**
     * Clear current photo and location data
     */
    fun clearCurrentData() {
        _currentPhoto.value = null
        _currentLocation.value = null
    }

    /**
     * Get entries in date range (for statistics)
     */
    fun getEntriesInDateRange(startDate: Long, endDate: Long): Flow<List<FoodEntry>> {
        return getEntriesUseCase.getInDateRange(startDate, endDate)
    }
}

/**
 * UI State for entry operations
 */
sealed class EntryState {
    data object Idle : EntryState()
    data object Loading : EntryState()
    data class Success(val message: String) : EntryState()
    data class Error(val message: String) : EntryState()
}

/**
 * Photo data holder
 */
data class PhotoData(
    val file: File,
    val bitmap: Bitmap,
    val dominantColor: Int
)
