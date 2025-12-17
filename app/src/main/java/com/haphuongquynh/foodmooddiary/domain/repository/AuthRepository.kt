package com.haphuongquynh.foodmooddiary.domain.repository

import com.haphuongquynh.foodmooddiary.domain.model.User
import com.haphuongquynh.foodmooddiary.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations
 * Follows Repository Pattern and Clean Architecture principles
 */
interface AuthRepository {
    
    /**
     * Get current authenticated user
     * @return Flow emitting current user or null if not authenticated
     */
    fun getCurrentUser(): Flow<User?>
    
    /**
     * Login with email and password
     * @param email User's email address
     * @param password User's password
     * @return Resource with User data or error
     */
    suspend fun login(email: String, password: String): Resource<User>
    
    /**
     * Register new user with email and password
     * @param email User's email address
     * @param password User's password
     * @param displayName User's display name
     * @return Resource with User data or error
     */
    suspend fun register(email: String, password: String, displayName: String): Resource<User>
    
    /**
     * Logout current user
     * Clears local data and signs out from Firebase
     */
    suspend fun logout(): Resource<Unit>
    
    /**
     * Check if user is authenticated
     * @return true if user is logged in, false otherwise
     */
    suspend fun isAuthenticated(): Boolean
    
    /**
     * Update user profile
     * @param displayName New display name
     * @param photoUrl New photo URL
     * @return Resource with updated User or error
     */
    suspend fun updateProfile(displayName: String?, photoUrl: String?): Resource<User>
}
