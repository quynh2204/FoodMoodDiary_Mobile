package com.haphuongquynh.foodmooddiary.domain.usecase.entry

import com.haphuongquynh.foodmooddiary.domain.repository.FoodEntryRepository
import com.haphuongquynh.foodmooddiary.util.common.Resource
import javax.inject.Inject

/**
 * Use case for deleting food entry
 */
class DeleteEntryUseCase @Inject constructor(
    private val repository: FoodEntryRepository
) {
    suspend operator fun invoke(entryId: String): Resource<Unit> {
        if (entryId.isBlank()) {
            return Resource.error("Entry ID is required")
        }
        
        return repository.deleteEntry(entryId)
    }
}
