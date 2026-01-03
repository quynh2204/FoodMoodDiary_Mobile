package com.haphuongquynh.foodmooddiary.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.haphuongquynh.foodmooddiary.domain.repository.SavedVietnamMealRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore implementation for saving Vietnamese meal favorites
 *
 * Structure: users/{userId}/savedVietnamMeals/{mealId}
 */
@Singleton
class SavedVietnamMealRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : SavedVietnamMealRepository {

    private val currentUserId: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    private fun getSavedMealsCollection() =
        firestore.collection("users")
            .document(currentUserId)
            .collection("savedVietnamMeals")

    override fun getSavedMealIds(): Flow<Set<String>> = callbackFlow {
        if (currentUserId.isEmpty()) {
            trySend(emptySet())
            awaitClose()
            return@callbackFlow
        }

        val listener = getSavedMealsCollection()
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptySet())
                    return@addSnapshotListener
                }

                val mealIds = snapshot?.documents?.map { it.id }?.toSet() ?: emptySet()
                trySend(mealIds)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun saveMeal(mealId: String): Result<Unit> {
        return try {
            if (currentUserId.isEmpty()) {
                return Result.failure(Exception("User not logged in"))
            }

            getSavedMealsCollection()
                .document(mealId)
                .set(mapOf(
                    "savedAt" to System.currentTimeMillis()
                ))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeMeal(mealId: String): Result<Unit> {
        return try {
            if (currentUserId.isEmpty()) {
                return Result.failure(Exception("User not logged in"))
            }

            getSavedMealsCollection()
                .document(mealId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleSave(mealId: String): Result<Boolean> {
        return try {
            if (currentUserId.isEmpty()) {
                return Result.failure(Exception("User not logged in"))
            }

            val docRef = getSavedMealsCollection().document(mealId)
            val exists = docRef.get().await().exists()

            if (exists) {
                docRef.delete().await()
                Result.success(false) // Now unsaved
            } else {
                docRef.set(mapOf("savedAt" to System.currentTimeMillis())).await()
                Result.success(true) // Now saved
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isSaved(mealId: String): Boolean {
        return try {
            if (currentUserId.isEmpty()) return false
            getSavedMealsCollection().document(mealId).get().await().exists()
        } catch (e: Exception) {
            false
        }
    }
}
