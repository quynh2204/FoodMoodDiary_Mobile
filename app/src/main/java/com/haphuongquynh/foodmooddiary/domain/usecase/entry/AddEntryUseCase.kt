package com.haphuongquynh.foodmooddiary.domain.usecase.entry

import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.repository.FoodEntryRepository
import com.haphuongquynh.foodmooddiary.util.common.Resource
import javax.inject.Inject

/**
 * Use case for adding new food entry
 * Handles validation and business logic
 */
class AddEntryUseCase @Inject constructor(
    private val repository: FoodEntryRepository
) {
    /**
     * Execute add entry operation
     * @param entry FoodEntry to add
     * @return Resource with added entry or error
     */
    suspend operator fun invoke(entry: FoodEntry): Resource<FoodEntry> {
        // Validate food name
        if (entry.foodName.isBlank()) {
            return Resource.error("Food name cannot be empty")
        }
        
        if (entry.foodName.length < 2) {
            return Resource.error("Food name must be at least 2 characters")
        }
        
        // Validate user ID
        if (entry.userId.isBlank()) {
            return Resource.error("User must be authenticated")
        }
        
        return repository.addEntry(entry)
    }
}
