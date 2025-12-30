package com.haphuongquynh.foodmooddiary.domain.model

/**
 * Domain model for user profile
 */
data class UserProfile(
    val userId: String,
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    val heightCm: Float? = null,
    val weightKg: Float? = null,
    val activityLevel: String? = null,
    val dietaryPreferences: List<String>? = null
)
