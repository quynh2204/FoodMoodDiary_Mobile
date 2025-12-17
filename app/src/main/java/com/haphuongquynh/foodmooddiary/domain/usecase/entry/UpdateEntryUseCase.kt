package com.haphuongquynh.foodmooddiary.domain.usecase.entry

import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.repository.FoodEntryRepository
import com.haphuongquynh.foodmooddiary.utils.Resource
import javax.inject.Inject

/**
 * Use case for updating food entry
 */
class UpdateEntryUseCase @Inject constructor(
    private val repository: FoodEntryRepository
) {
    suspend operator fun invoke(entry: FoodEntry): Resource<FoodEntry> {
        if (entry.foodName.isBlank()) {
            return Resource.error("Food name cannot be empty")
        }
        
        if (entry.id.isBlank()) {
            return Resource.error("Entry ID is required for update")
        }
        
        return repository.updateEntry(entry)
    }
}
