package com.haphuongquynh.foodmooddiary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.haphuongquynh.foodmooddiary.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for UserProfile operations
 */
@Dao
interface UserProfileDao {
    
    @Query("SELECT * FROM user_profile WHERE userId = :userId LIMIT 1")
    fun getUserProfile(userId: String = "current"): Flow<UserProfileEntity?>
    
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)
    
    @Query("DELETE FROM user_profile WHERE userId = :userId")
    suspend fun deleteProfile(userId: String)
}
