package com.haphuongquynh.foodmooddiary.domain.usecase.auth

import com.haphuongquynh.foodmooddiary.domain.model.User
import com.haphuongquynh.foodmooddiary.domain.repository.AuthRepository
import com.haphuongquynh.foodmooddiary.utils.Resource
import javax.inject.Inject

/**
 * Use case for user registration
 * Handles validation and business rules for new user sign up
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Execute registration operation
     * @param email User's email address
     * @param password User's password
     * @param confirmPassword Password confirmation
     * @param displayName User's display name
     * @return Resource with User data or error
     */
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String,
        displayName: String
    ): Resource<User> {
        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Resource.error("Invalid email format")
        }
        
        // Validate display name
        if (displayName.isBlank() || displayName.length < 2) {
            return Resource.error("Display name must be at least 2 characters")
        }
        
        // Validate password length
        if (password.length < 6) {
            return Resource.error("Password must be at least 6 characters")
        }
        
        // Validate password strength (at least one number)
        if (!password.any { it.isDigit() }) {
            return Resource.error("Password must contain at least one number")
        }
        
        // Validate password confirmation
        if (password != confirmPassword) {
            return Resource.error("Passwords do not match")
        }
        
        return authRepository.register(email, password, displayName)
    }
}
