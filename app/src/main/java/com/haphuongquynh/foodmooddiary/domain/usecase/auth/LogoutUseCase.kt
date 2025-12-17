package com.haphuongquynh.foodmooddiary.domain.usecase.auth

import com.haphuongquynh.foodmooddiary.domain.repository.AuthRepository
import com.haphuongquynh.foodmooddiary.utils.Resource
import javax.inject.Inject

/**
 * Use case for user logout
 * Handles sign out and data cleanup
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Execute logout operation
     * @return Resource indicating success or error
     */
    suspend operator fun invoke(): Resource<Unit> {
        return authRepository.logout()
    }
}
