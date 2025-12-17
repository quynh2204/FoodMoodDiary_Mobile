package com.haphuongquynh.foodmooddiary.domain.usecase.entry

import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.repository.FoodEntryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting food entries
 */
class GetEntriesUseCase @Inject constructor(
    private val repository: FoodEntryRepository
) {
    /**
     * Get all entries
     */
    operator fun invoke(): Flow<List<FoodEntry>> {
        return repository.getAllEntries()
    }
    
    /**
     * Get entries in date range
     */
    fun getInDateRange(startDate: Long, endDate: Long): Flow<List<FoodEntry>> {
        return repository.getEntriesInDateRange(startDate, endDate)
    }
}
