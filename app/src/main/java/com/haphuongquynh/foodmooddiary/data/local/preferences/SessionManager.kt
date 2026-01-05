package com.haphuongquynh.foodmooddiary.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SessionManager handles user session persistence
 * Manages "Remember Me" functionality with 30-day expiration
 */
@Singleton
class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_REMEMBER_ME = booleanPreferencesKey("remember_me")
        private val KEY_SAVED_EMAIL = stringPreferencesKey("saved_email")
        private val KEY_SESSION_EXPIRY = longPreferencesKey("session_expiry")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        
        // 30 days in milliseconds
        private const val REMEMBER_ME_DURATION = 30L * 24 * 60 * 60 * 1000
    }

    /**
     * Save remember me session
     */
    suspend fun saveRememberMeSession(userId: String, email: String) {
        val expiryTime = System.currentTimeMillis() + REMEMBER_ME_DURATION
        dataStore.edit { preferences ->
            preferences[KEY_REMEMBER_ME] = true
            preferences[KEY_SAVED_EMAIL] = email
            preferences[KEY_USER_ID] = userId
            preferences[KEY_SESSION_EXPIRY] = expiryTime
        }
    }

    /**
     * Clear remember me session
     */
    suspend fun clearRememberMeSession() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_REMEMBER_ME)
            preferences.remove(KEY_SAVED_EMAIL)
            preferences.remove(KEY_USER_ID)
            preferences.remove(KEY_SESSION_EXPIRY)
        }
    }

    /**
     * Check if remember me session is valid
     */
    suspend fun isRememberMeValid(): Boolean {
        val preferences = dataStore.data.firstOrNull() ?: return false
        val rememberMe = preferences[KEY_REMEMBER_ME] ?: false
        val expiryTime = preferences[KEY_SESSION_EXPIRY] ?: 0L
        val currentTime = System.currentTimeMillis()
        
        return rememberMe && currentTime < expiryTime
    }

    /**
     * Get saved email if remember me is enabled
     */
    fun getSavedEmail(): Flow<String?> = dataStore.data.map { preferences ->
        val rememberMe = preferences[KEY_REMEMBER_ME] ?: false
        val expiryTime = preferences[KEY_SESSION_EXPIRY] ?: 0L
        val currentTime = System.currentTimeMillis()
        
        if (rememberMe && currentTime < expiryTime) {
            preferences[KEY_SAVED_EMAIL]
        } else {
            null
        }
    }

    /**
     * Get saved user ID if remember me is enabled
     */
    fun getSavedUserId(): Flow<String?> = dataStore.data.map { preferences ->
        val rememberMe = preferences[KEY_REMEMBER_ME] ?: false
        val expiryTime = preferences[KEY_SESSION_EXPIRY] ?: 0L
        val currentTime = System.currentTimeMillis()
        
        if (rememberMe && currentTime < expiryTime) {
            preferences[KEY_USER_ID]
        } else {
            null
        }
    }

    /**
     * Check if remember me is enabled
     */
    fun isRememberMeEnabled(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_REMEMBER_ME] ?: false
    }

    /**
     * Get remaining days for session
     */
    suspend fun getRemainingDays(): Int {
        val preferences = dataStore.data.firstOrNull() ?: return 0
        val expiryTime = preferences[KEY_SESSION_EXPIRY] ?: return 0
        val currentTime = System.currentTimeMillis()
        val remainingTime = expiryTime - currentTime
        
        return if (remainingTime > 0) {
            (remainingTime / (24 * 60 * 60 * 1000)).toInt()
        } else {
            0
        }
    }
}
