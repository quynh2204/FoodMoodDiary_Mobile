package com.haphuongquynh.foodmooddiary.domain.usecase.auth

import com.haphuongquynh.foodmooddiary.domain.model.User
import com.haphuongquynh.foodmooddiary.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get current authenticated user
 * Returns a Flow that emits user changes
 */
class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Get current user as Flow
     * @return Flow emitting current User or null if not authenticated
     */
    operator fun invoke(): Flow<User?> {
        return authRepository.getCurrentUser()
    }
}
