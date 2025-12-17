package com.haphuongquynh.foodmooddiary.data.repository

import com.haphuongquynh.foodmooddiary.data.local.dao.FavoriteMealDao
import com.haphuongquynh.foodmooddiary.data.local.entity.FavoriteMealEntity
import com.haphuongquynh.foodmooddiary.data.remote.MealApiService
import com.haphuongquynh.foodmooddiary.domain.model.Meal
import com.haphuongquynh.foodmooddiary.domain.repository.MealRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of MealRepository
 */
class MealRepositoryImpl @Inject constructor(
    private val mealApiService: MealApiService,
    private val favoriteMealDao: FavoriteMealDao,
    private val firebaseAuth: FirebaseAuth
) : MealRepository {

    override suspend fun getRandomMeal(): Result<Meal> {
        return try {
            val response = mealApiService.getRandomMeal()
            if (response.isSuccessful && response.body()?.meals?.isNotEmpty() == true) {
                val meal = response.body()!!.meals!!.first().toDomainModel()
                Result.success(meal.copy(isFavorite = isFavorite(meal.id)))
            } else {
                Result.failure(Exception("No meal found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchMealsByName(name: String): Result<List<Meal>> {
        return try {
            val response = mealApiService.searchMealsByName(name)
            if (response.isSuccessful && response.body()?.meals != null) {
                val meals = response.body()!!.meals!!.map { dto ->
                    dto.toDomainModel().copy(isFavorite = isFavorite(dto.idMeal))
                }
                Result.success(meals)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMealById(id: String): Result<Meal> {
        return try {
            val response = mealApiService.getMealById(id)
            if (response.isSuccessful && response.body()?.meals?.isNotEmpty() == true) {
                val meal = response.body()!!.meals!!.first().toDomainModel()
                Result.success(meal.copy(isFavorite = isFavorite(meal.id)))
            } else {
                Result.failure(Exception("Meal not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun filterByCategory(category: String): Result<List<Meal>> {
        return try {
            val response = mealApiService.filterByCategory(category)
            if (response.isSuccessful && response.body()?.meals != null) {
                val meals = response.body()!!.meals!!.map { dto ->
                    dto.toDomainModel().copy(isFavorite = isFavorite(dto.idMeal))
                }
                Result.success(meals)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun filterByArea(area: String): Result<List<Meal>> {
        return try {
            val response = mealApiService.filterByArea(area)
            if (response.isSuccessful && response.body()?.meals != null) {
                val meals = response.body()!!.meals!!.map { dto ->
                    dto.toDomainModel().copy(isFavorite = isFavorite(dto.idMeal))
                }
                Result.success(meals)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllCategories(): Result<List<String>> {
        return try {
            val response = mealApiService.getAllCategories()
            if (response.isSuccessful && response.body()?.categories != null) {
                val categories = response.body()!!.categories!!.map { it.strCategory }
                Result.success(categories)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllAreas(): Result<List<String>> {
        return try {
            val response = mealApiService.getAllAreas()
            if (response.isSuccessful && response.body()?.meals != null) {
                val areas = response.body()!!.meals!!.map { it.strArea }
                Result.success(areas)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getFavoriteMeals(): Flow<List<Meal>> {
        val userId = firebaseAuth.currentUser?.uid ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        return favoriteMealDao.getAllFavorites(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun addToFavorites(meal: Meal): Result<Unit> {
        return try {
            val userId = firebaseAuth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            favoriteMealDao.insertFavorite(
                FavoriteMealEntity(
                    mealId = meal.id,
                    userId = userId,
                    name = meal.name,
                    category = meal.category,
                    area = meal.area,
                    thumbUrl = meal.thumbUrl,
                    addedAt = System.currentTimeMillis()
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromFavorites(mealId: String): Result<Unit> {
        return try {
            val userId = firebaseAuth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            favoriteMealDao.deleteFavorite(userId, mealId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isFavorite(mealId: String): Boolean {
        val userId = firebaseAuth.currentUser?.uid ?: return false
        return favoriteMealDao.isFavorite(userId, mealId)
    }
}
