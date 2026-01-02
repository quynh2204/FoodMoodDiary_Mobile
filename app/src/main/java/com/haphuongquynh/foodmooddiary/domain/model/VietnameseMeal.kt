package com.haphuongquynh.foodmooddiary.domain.model

/**
 * Vietnamese Meal data model
 * Stored in Firestore: vietnameseMeals/{mealId}
 */
data class VietnameseMeal(
    val id: String = "",
    val name: String = "",
    val category: String = "", // "Món nước", "Món khô", "Tráng miệng"
    val youtubeUrl: String = "",
    val imageUrl: String = "",
    val calories: Int = 0,
    val description: String = "",
    val tags: List<String> = emptyList() // e.g., ["breakfast", "popular", "noodles"]
)
