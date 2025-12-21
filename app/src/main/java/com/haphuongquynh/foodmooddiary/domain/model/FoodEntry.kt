package com.haphuongquynh.foodmooddiary.domain.model

/**
 * Domain model representing a Food Entry with mood tracking
 * This is the core entity of the FoodMoodDiary app
 */
data class FoodEntry(
    val id: String = "",
    val userId: String,
    val foodName: String,
    val notes: String = "",
    val photoUrl: String? = null,
    val localPhotoPath: String? = null,
    val moodColor: Int, // ARGB color value
    val mood: String? = null, // Emoji representing mood
    val location: Location? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

/**
 * Location data for food entry
 */
data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)
