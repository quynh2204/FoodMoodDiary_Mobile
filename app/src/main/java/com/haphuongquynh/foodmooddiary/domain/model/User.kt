package com.haphuongquynh.foodmooddiary.domain.model

/**
 * Domain model representing a User
 * Clean Architecture: Domain layer should not depend on Android framework or external libraries
 */
data class User(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    val notificationsEnabled: Boolean = true,
    val themePreference: String = "Auto" // "Light", "Dark", "Auto"
)
