package com.haphuongquynh.foodmooddiary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for user profile
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val userId: String,
    val displayName: String,
    val email: String,
    val photoUrl: String?,
    val age: Int?,
    val gender: String?,
    val heightCm: Float?,
    val weightKg: Float?,
    val activityLevel: String?,
    val dietaryPreferences: List<String>?
)
