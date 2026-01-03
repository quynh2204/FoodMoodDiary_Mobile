package com.haphuongquynh.foodmooddiary.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.haphuongquynh.foodmooddiary.data.local.FoodMoodDatabase
import com.haphuongquynh.foodmooddiary.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing Database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // Migration from version 5 to 6 (added mealType column)
    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add mealType column if not exists
            database.execSQL("ALTER TABLE food_entries ADD COLUMN mealType TEXT")
        }
    }

    @Provides
    @Singleton
    fun provideFoodMoodDatabase(
        @ApplicationContext context: Context
    ): FoodMoodDatabase {
        return Room.databaseBuilder(
            context,
            FoodMoodDatabase::class.java,
            FoodMoodDatabase.DATABASE_NAME
        )
            .addMigrations(MIGRATION_5_6)
            // Keep fallback for older versions that we don't have migrations for
            .fallbackToDestructiveMigrationFrom(1, 2, 3, 4)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: FoodMoodDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideFoodEntryDao(database: FoodMoodDatabase): com.haphuongquynh.foodmooddiary.data.local.dao.FoodEntryDao {
        return database.foodEntryDao()
    }
    
    @Provides
    @Singleton
    fun provideUserProfileDao(database: FoodMoodDatabase): com.haphuongquynh.foodmooddiary.data.local.dao.UserProfileDao {
        return database.userProfileDao()
    }
}
