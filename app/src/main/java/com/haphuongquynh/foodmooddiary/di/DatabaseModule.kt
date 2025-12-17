package com.haphuongquynh.foodmooddiary.di

import android.content.Context
import androidx.room.Room
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
            .fallbackToDestructiveMigration() // TODO: Remove in production, add migrations
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
    fun provideFavoriteMealDao(database: FoodMoodDatabase): com.haphuongquynh.foodmooddiary.data.local.dao.FavoriteMealDao {
        return database.favoriteMealDao()
    }
}
