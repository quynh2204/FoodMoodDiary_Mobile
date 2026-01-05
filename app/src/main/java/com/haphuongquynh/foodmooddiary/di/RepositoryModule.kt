package com.haphuongquynh.foodmooddiary.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.haphuongquynh.foodmooddiary.data.local.dao.UserDao
import com.haphuongquynh.foodmooddiary.data.local.preferences.SessionManager
import com.haphuongquynh.foodmooddiary.data.repository.AuthRepositoryImpl
import com.haphuongquynh.foodmooddiary.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing Repository dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        userDao: UserDao,
        sessionManager: SessionManager
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, firestore, userDao, sessionManager)
    }

    @Provides
    @Singleton
    fun provideFoodEntryRepository(
        foodEntryDao: com.haphuongquynh.foodmooddiary.data.local.dao.FoodEntryDao,
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: com.google.firebase.storage.FirebaseStorage
    ): com.haphuongquynh.foodmooddiary.domain.repository.FoodEntryRepository {
        return com.haphuongquynh.foodmooddiary.data.repository.FoodEntryRepositoryImpl(
            foodEntryDao, firebaseAuth, firestore, storage
        )
    }

    @Provides
    @Singleton
    fun provideStatisticsRepository(
        foodEntryDao: com.haphuongquynh.foodmooddiary.data.local.dao.FoodEntryDao,
        firebaseAuth: FirebaseAuth
    ): com.haphuongquynh.foodmooddiary.domain.repository.StatisticsRepository {
        return com.haphuongquynh.foodmooddiary.data.repository.StatisticsRepositoryImpl(
            foodEntryDao, firebaseAuth
        )
    }

    @Provides
    @Singleton
    fun provideSavedVietnamMealRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): com.haphuongquynh.foodmooddiary.domain.repository.SavedVietnamMealRepository {
        return com.haphuongquynh.foodmooddiary.data.repository.SavedVietnamMealRepositoryImpl(
            firebaseAuth, firestore
        )
    }

    @Provides
    @Singleton
    fun provideVietnameseMealRepository(
        firestore: FirebaseFirestore
    ): com.haphuongquynh.foodmooddiary.domain.repository.VietnameseMealRepository {
        return com.haphuongquynh.foodmooddiary.data.repository.VietnameseMealRepositoryImpl(
            firestore
        )
    }
}
