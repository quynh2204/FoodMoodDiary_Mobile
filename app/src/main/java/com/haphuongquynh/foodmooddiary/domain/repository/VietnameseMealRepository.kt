package com.haphuongquynh.foodmooddiary.domain.repository

import com.haphuongquynh.foodmooddiary.domain.model.VietnameseMeal
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing Vietnamese meal data from Firestore
 */
interface VietnameseMealRepository {
    /**
     * Get all Vietnamese meals from Firestore (realtime)
     */
    fun getAllMeals(): Flow<List<VietnameseMeal>>

    /**
     * Get meals filtered by category (realtime)
     * @param category e.g., "Món nước", "Món khô", "Tráng miệng"
     */
    fun getMealsByCategory(category: String): Flow<List<VietnameseMeal>>

    /**
     * Get a specific meal by ID
     */
    suspend fun getMealById(id: String): VietnameseMeal?

    /**
     * Search meals by name, category, or tags
     */
    fun searchMeals(query: String): Flow<List<VietnameseMeal>>
}
