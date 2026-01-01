package com.haphuongquynh.foodmooddiary.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing saved Vietnamese meals (favorites)
 * Persists to Firebase Firestore under user's document
 */
interface SavedVietnamMealRepository {

    /**
     * Get all saved meal IDs as a Flow (real-time updates)
     */
    fun getSavedMealIds(): Flow<Set<String>>

    /**
     * Save a meal to favorites
     */
    suspend fun saveMeal(mealId: String): Result<Unit>

    /**
     * Remove a meal from favorites
     */
    suspend fun removeMeal(mealId: String): Result<Unit>

    /**
     * Toggle save state for a meal
     */
    suspend fun toggleSave(mealId: String): Result<Boolean>

    /**
     * Check if a meal is saved
     */
    suspend fun isSaved(mealId: String): Boolean
}
