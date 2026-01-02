package com.haphuongquynh.foodmooddiary.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.haphuongquynh.foodmooddiary.domain.model.VietnameseMeal
import com.haphuongquynh.foodmooddiary.domain.repository.VietnameseMealRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore implementation for Vietnamese meals
 *
 * Collection structure: vietnameseMeals/{mealId}
 */
@Singleton
class VietnameseMealRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : VietnameseMealRepository {

    private val mealsCollection = firestore.collection("vietnameseMeals")

    override fun getAllMeals(): Flow<List<VietnameseMeal>> = callbackFlow {
        val listener = mealsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val meals = snapshot?.documents?.mapNotNull {
                    try {
                        it.toObject(VietnameseMeal::class.java)?.copy(id = it.id)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(meals)
            }

        awaitClose { listener.remove() }
    }

    override fun getMealsByCategory(category: String): Flow<List<VietnameseMeal>> = callbackFlow {
        val listener = mealsCollection
            .whereEqualTo("category", category)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val meals = snapshot?.documents?.mapNotNull {
                    try {
                        it.toObject(VietnameseMeal::class.java)?.copy(id = it.id)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(meals)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getMealById(id: String): VietnameseMeal? {
        return try {
            val doc = mealsCollection.document(id).get().await()
            doc.toObject(VietnameseMeal::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    override fun searchMeals(query: String): Flow<List<VietnameseMeal>> = callbackFlow {
        val listener = mealsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val meals = snapshot?.documents?.mapNotNull {
                    try {
                        it.toObject(VietnameseMeal::class.java)?.copy(id = it.id)
                    } catch (e: Exception) {
                        null
                    }
                }?.filter { meal ->
                    meal.name.contains(query, ignoreCase = true) ||
                    meal.category.contains(query, ignoreCase = true) ||
                    meal.tags.any { tag -> tag.contains(query, ignoreCase = true) } ||
                    meal.description.contains(query, ignoreCase = true)
                } ?: emptyList()

                trySend(meals)
            }

        awaitClose { listener.remove() }
    }
}
