package com.haphuongquynh.foodmooddiary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.haphuongquynh.foodmooddiary.data.local.dao.UserDao
import com.haphuongquynh.foodmooddiary.data.local.dao.UserProfileDao
import com.haphuongquynh.foodmooddiary.data.local.entity.UserEntity
import com.haphuongquynh.foodmooddiary.data.local.entity.UserProfileEntity

/**
 * Room Database for FoodMoodDiary app
 * Version 6: Removed FavoriteMealEntity (moved to Firebase) and Added mealType column to food_entries
 */
@Database(
    entities = [
        UserEntity::class,
        com.haphuongquynh.foodmooddiary.data.local.entity.FoodEntryEntity::class,
        UserProfileEntity::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FoodMoodDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun foodEntryDao(): com.haphuongquynh.foodmooddiary.data.local.dao.FoodEntryDao
    abstract fun userProfileDao(): UserProfileDao
    
    companion object {
        const val DATABASE_NAME = "food_mood_diary.db"
    }
}
