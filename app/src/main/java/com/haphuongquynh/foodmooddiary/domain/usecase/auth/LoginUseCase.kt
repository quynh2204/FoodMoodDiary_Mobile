package com.haphuongquynh.foodmooddiary.domain.usecase.auth

import com.haphuongquynh.foodmooddiary.domain.model.User
import com.haphuongquynh.foodmooddiary.domain.repository.AuthRepository
import com.haphuongquynh.foodmooddiary.util.common.Resource
import javax.inject.Inject

/**
 * Use case for user login
 * Encapsulates business logic for authentication
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Execute login operation
     * @param email User's email address
     * @param password User's password
     * @return Resource with User data or error
     */
    suspend operator fun invoke(email: String, password: String): Resource<User> {
        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Resource.error("Invalid email format")
        }
        
        // Validate password length
        if (password.length < 6) {
            return Resource.error("Password must be at least 6 characters")
        }
        
        return authRepository.login(email, password)
    }
}
