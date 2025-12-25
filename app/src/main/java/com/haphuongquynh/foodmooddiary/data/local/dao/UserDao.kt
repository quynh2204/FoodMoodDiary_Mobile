package com.haphuongquynh.foodmooddiary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.haphuongquynh.foodmooddiary.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User operations
 * Room will generate implementation at compile time
 */
@Dao
interface UserDao {
    
    /**
     * Insert or replace user
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    /**
     * Update user
     */
    @Update
    suspend fun updateUser(user: UserEntity)
    
    /**
     * Delete user
     */
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    /**
     * Get user by UID
     * @return Flow emitting user or null if not found
     */
    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    fun getUserByUid(uid: String): Flow<UserEntity?>
    
    /**
     * Get current user (assuming single user app)
     * @return Flow emitting first user or null
     */
    @Query("SELECT * FROM users ORDER BY lastLoginAt DESC LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>
    
    /**
     * Get user by UID (suspend version for direct access)
     * @return UserEntity or null if not found
     */
    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    suspend fun getUserById(uid: String): UserEntity?
    
    /**
     * Delete all users (for logout)
     */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
    
    /**
     * Check if user exists
     */
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE uid = :uid)")
    suspend fun userExists(uid: String): Boolean
}
