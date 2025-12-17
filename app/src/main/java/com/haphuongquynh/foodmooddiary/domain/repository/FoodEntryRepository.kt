package com.haphuongquynh.foodmooddiary.domain.repository

import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Food Entry operations
 * Follows Repository Pattern and Offline-First strategy
 */
interface FoodEntryRepository {
    
    /**
     * Get all food entries for current user
     * @return Flow emitting list of entries (from local DB)
     */
    fun getAllEntries(): Flow<List<FoodEntry>>
    
    /**
     * Get entries within date range
     * @param startDate Start timestamp
     * @param endDate End timestamp
     * @return Flow emitting filtered entries
     */
    fun getEntriesInDateRange(startDate: Long, endDate: Long): Flow<List<FoodEntry>>
    
    /**
     * Get entry by ID
     * @param entryId Entry ID
     * @return Flow emitting entry or null
     */
    fun getEntryById(entryId: String): Flow<FoodEntry?>
    
    /**
     * Add new food entry
     * @param entry FoodEntry to add
     * @return Resource indicating success or error
     */
    suspend fun addEntry(entry: FoodEntry): Resource<FoodEntry>
    
    /**
     * Update existing entry
     * @param entry Updated FoodEntry
     * @return Resource indicating success or error
     */
    suspend fun updateEntry(entry: FoodEntry): Resource<FoodEntry>
    
    /**
     * Delete entry
     * @param entryId Entry ID to delete
     * @return Resource indicating success or error
     */
    suspend fun deleteEntry(entryId: String): Resource<Unit>
    
    /**
     * Sync local entries with Firebase
     * Upload unsynced entries and download remote changes
     */
    suspend fun syncEntries(): Resource<Unit>
    
    /**
     * Upload photo to Firebase Storage
     * @param localPath Local file path
     * @return Resource with remote URL or error
     */
    suspend fun uploadPhoto(localPath: String): Resource<String>
}
