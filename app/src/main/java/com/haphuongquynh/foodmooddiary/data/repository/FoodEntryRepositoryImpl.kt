package com.haphuongquynh.foodmooddiary.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.haphuongquynh.foodmooddiary.data.local.dao.FoodEntryDao
import com.haphuongquynh.foodmooddiary.data.local.entity.FoodEntryEntity
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.repository.FoodEntryRepository
import com.haphuongquynh.foodmooddiary.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FoodEntryRepository
 * Offline-First: Local database as single source of truth, sync with Firebase
 */
@Singleton
class FoodEntryRepositoryImpl @Inject constructor(
    private val foodEntryDao: FoodEntryDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : FoodEntryRepository {

    private val currentUserId: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    override fun getAllEntries(): Flow<List<FoodEntry>> {
        return foodEntryDao.getAllEntries(currentUserId)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getEntriesInDateRange(startDate: Long, endDate: Long): Flow<List<FoodEntry>> {
        return foodEntryDao.getEntriesInDateRange(currentUserId, startDate, endDate)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getEntryById(entryId: String): Flow<FoodEntry?> {
        return foodEntryDao.getEntryById(entryId)
            .map { it?.toDomain() }
    }

    override suspend fun addEntry(entry: FoodEntry): Resource<FoodEntry> {
        return try {
            // Generate ID if not provided
            val entryWithId = if (entry.id.isBlank()) {
                entry.copy(id = UUID.randomUUID().toString())
            } else entry

            // Save to local database first (Offline-First)
            val entity = FoodEntryEntity.fromDomain(entryWithId)
            foodEntryDao.insertEntry(entity)

            // Try to sync with Firestore
            syncEntryToFirestore(entryWithId)

            Resource.success(entryWithId)
        } catch (e: Exception) {
            Resource.error("Failed to add entry: ${e.localizedMessage}", e)
        }
    }

    override suspend fun updateEntry(entry: FoodEntry): Resource<FoodEntry> {
        return try {
            val updatedEntry = entry.copy(
                updatedAt = System.currentTimeMillis(),
                isSynced = false
            )

            // Update local database
            val entity = FoodEntryEntity.fromDomain(updatedEntry)
            foodEntryDao.updateEntry(entity)

            // Try to sync with Firestore
            syncEntryToFirestore(updatedEntry)

            Resource.success(updatedEntry)
        } catch (e: Exception) {
            Resource.error("Failed to update entry: ${e.localizedMessage}", e)
        }
    }

    override suspend fun deleteEntry(entryId: String): Resource<Unit> {
        return try {
            // Delete from local database
            foodEntryDao.deleteEntryById(entryId)

            // Try to delete from Firestore
            try {
                firestore.collection("foodEntries")
                    .document(entryId)
                    .delete()
                    .await()
            } catch (e: Exception) {
                // Fail silently - will be handled during sync
                android.util.Log.e("FoodEntryRepo", "Firestore delete failed: ${e.message}")
            }

            Resource.success(Unit)
        } catch (e: Exception) {
            Resource.error("Failed to delete entry: ${e.localizedMessage}", e)
        }
    }

    override suspend fun syncEntries(): Resource<Unit> {
        return try {
            // Upload unsynced entries
            val unsyncedEntries = foodEntryDao.getUnsyncedEntries(currentUserId)
            unsyncedEntries.forEach { entity ->
                syncEntryToFirestore(entity.toDomain())
            }

            // TODO: Download remote entries newer than local
            // This would require tracking last sync timestamp

            Resource.success(Unit)
        } catch (e: Exception) {
            Resource.error("Sync failed: ${e.localizedMessage}", e)
        }
    }

    override suspend fun uploadPhoto(localPath: String): Resource<String> {
        return try {
            val file = File(localPath)
            if (!file.exists()) {
                return Resource.error("Photo file not found")
            }

            val fileName = "food_photos/${currentUserId}/${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child(fileName)

            val uri = Uri.fromFile(file)
            val uploadTask = storageRef.putFile(uri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()

            Resource.success(downloadUrl.toString())
        } catch (e: Exception) {
            Resource.error("Photo upload failed: ${e.localizedMessage}", e)
        }
    }

    /**
     * Sync entry to Firestore
     */
    private suspend fun syncEntryToFirestore(entry: FoodEntry) {
        try {
            val entryMap = hashMapOf(
                "id" to entry.id,
                "userId" to entry.userId,
                "foodName" to entry.foodName,
                "notes" to entry.notes,
                "photoUrl" to entry.photoUrl,
                "moodColor" to entry.moodColor,
                "location" to entry.location?.let {
                    hashMapOf(
                        "latitude" to it.latitude,
                        "longitude" to it.longitude,
                        "address" to it.address
                    )
                },
                "timestamp" to entry.timestamp,
                "createdAt" to entry.createdAt,
                "updatedAt" to entry.updatedAt
            )

            firestore.collection("foodEntries")
                .document(entry.id)
                .set(entryMap)
                .await()

            // Mark as synced in local DB
            foodEntryDao.markAsSynced(entry.id)
        } catch (e: Exception) {
            // Fail silently - will be synced later
            android.util.Log.e("FoodEntryRepo", "Firestore sync failed: ${e.message}")
        }
    }
}
