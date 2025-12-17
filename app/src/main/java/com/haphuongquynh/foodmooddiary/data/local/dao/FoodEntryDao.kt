package com.haphuongquynh.foodmooddiary.data.local.dao

import androidx.room.*
import com.haphuongquynh.foodmooddiary.data.local.entity.FoodEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for FoodEntry operations
 */
@Dao
interface FoodEntryDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: FoodEntryEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<FoodEntryEntity>)
    
    @Update
    suspend fun updateEntry(entry: FoodEntryEntity)
    
    @Delete
    suspend fun deleteEntry(entry: FoodEntryEntity)
    
    @Query("DELETE FROM food_entries WHERE id = :entryId")
    suspend fun deleteEntryById(entryId: String)
    
    @Query("SELECT * FROM food_entries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllEntries(userId: String): Flow<List<FoodEntryEntity>>
    
    @Query("SELECT * FROM food_entries WHERE id = :entryId LIMIT 1")
    fun getEntryById(entryId: String): Flow<FoodEntryEntity?>
    
    @Query("""
        SELECT * FROM food_entries 
        WHERE userId = :userId 
        AND timestamp BETWEEN :startDate AND :endDate 
        ORDER BY timestamp DESC
    """)
    fun getEntriesInDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<FoodEntryEntity>>
    
    @Query("SELECT * FROM food_entries WHERE userId = :userId AND isSynced = 0")
    suspend fun getUnsyncedEntries(userId: String): List<FoodEntryEntity>
    
    @Query("UPDATE food_entries SET isSynced = 1 WHERE id = :entryId")
    suspend fun markAsSynced(entryId: String)
    
    @Query("DELETE FROM food_entries WHERE userId = :userId")
    suspend fun deleteAllEntriesForUser(userId: String)
    
    @Query("SELECT COUNT(*) FROM food_entries WHERE userId = :userId")
    suspend fun getEntryCount(userId: String): Int
    
    // Content Provider support
    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC")
    fun getAllEntriesCursor(): android.database.Cursor
    
    @Query("SELECT * FROM food_entries WHERE id = :entryId LIMIT 1")
    fun getEntryByIdCursor(entryId: String): android.database.Cursor
}
