package com.haphuongquynh.foodmooddiary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.haphuongquynh.foodmooddiary.data.local.dao.FavoriteMealDao
import com.haphuongquynh.foodmooddiary.data.local.dao.UserDao
import com.haphuongquynh.foodmooddiary.data.local.entity.FavoriteMealEntity
import com.haphuongquynh.foodmooddiary.data.local.entity.UserEntity

/**
 * Room Database for FoodMoodDiary app
 * Version 5: Updated schema after backend merge
 */
@Database(
    entities = [
        UserEntity::class,
        com.haphuongquynh.foodmooddiary.data.local.entity.FoodEntryEntity::class,
        FavoriteMealEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class FoodMoodDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun foodEntryDao(): com.haphuongquynh.foodmooddiary.data.local.dao.FoodEntryDao
    abstract fun favoriteMealDao(): FavoriteMealDao
    
    companion object {
        const val DATABASE_NAME = "food_mood_diary.db"
    }
}
