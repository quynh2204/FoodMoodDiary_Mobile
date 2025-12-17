package com.haphuongquynh.foodmooddiary.domain.repository

import com.haphuongquynh.foodmooddiary.domain.model.Meal
import kotlinx.coroutines.flow.Flow

/**
 * Repository for discovering meals from TheMealDB API
 */
interface MealRepository {
    suspend fun getRandomMeal(): Result<Meal>
    suspend fun searchMealsByName(name: String): Result<List<Meal>>
    suspend fun getMealById(id: String): Result<Meal>
    suspend fun filterByCategory(category: String): Result<List<Meal>>
    suspend fun filterByArea(area: String): Result<List<Meal>>
    suspend fun getAllCategories(): Result<List<String>>
    suspend fun getAllAreas(): Result<List<String>>
    fun getFavoriteMeals(): Flow<List<Meal>>
    suspend fun addToFavorites(meal: Meal): Result<Unit>
    suspend fun removeFromFavorites(mealId: String): Result<Unit>
    suspend fun isFavorite(mealId: String): Boolean
}
