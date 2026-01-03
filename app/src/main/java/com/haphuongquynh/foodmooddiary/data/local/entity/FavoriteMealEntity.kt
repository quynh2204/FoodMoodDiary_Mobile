package com.haphuongquynh.foodmooddiary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.haphuongquynh.foodmooddiary.domain.model.Meal

/**
 * Room entity for favorite meals
 */
@Entity(tableName = "favorite_meals")
data class FavoriteMealEntity(
    @PrimaryKey
    val mealId: String,
    val userId: String,
    val name: String,
    val category: String,
    val area: String,
    val thumbUrl: String,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): Meal {
        return Meal(
            id = mealId,
            name = name,
            category = category,
            area = area,
            instructions = "",
            thumbUrl = thumbUrl,
            tags = emptyList(),
            youtubeUrl = null,
            ingredients = emptyList(),
            isFavorite = true
        )
    }
}
