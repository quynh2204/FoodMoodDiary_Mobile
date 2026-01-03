package com.haphuongquynh.foodmooddiary.domain.model

/**
 * Meal model from TheMealDB API
 */
data class Meal(
    val id: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val thumbUrl: String,
    val tags: List<String>,
    val youtubeUrl: String?,
    val ingredients: List<Pair<String, String>>, // Ingredient to Measure
    val isFavorite: Boolean = false
)
