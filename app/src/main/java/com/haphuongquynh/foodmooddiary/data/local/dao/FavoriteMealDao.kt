package com.haphuongquynh.foodmooddiary.data.local.dao

import androidx.room.*
import com.haphuongquynh.foodmooddiary.data.local.entity.FavoriteMealEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for favorite meals
 */
@Dao
interface FavoriteMealDao {

    @Query("SELECT * FROM favorite_meals WHERE userId = :userId ORDER BY addedAt DESC")
    fun getAllFavorites(userId: String): Flow<List<FavoriteMealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(meal: FavoriteMealEntity)

    @Query("DELETE FROM favorite_meals WHERE userId = :userId AND mealId = :mealId")
    suspend fun deleteFavorite(userId: String, mealId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_meals WHERE userId = :userId AND mealId = :mealId)")
    suspend fun isFavorite(userId: String, mealId: String): Boolean

    @Query("DELETE FROM favorite_meals WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}
