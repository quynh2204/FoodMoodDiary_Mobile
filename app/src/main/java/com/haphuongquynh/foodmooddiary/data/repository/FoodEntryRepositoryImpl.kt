package com.haphuongquynh.foodmooddiary.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.haphuongquynh.foodmooddiary.data.local.dao.FoodEntryDao
import com.haphuongquynh.foodmooddiary.data.local.entity.FoodEntryEntity
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.model.Location
import com.haphuongquynh.foodmooddiary.domain.model.MoodType
import com.haphuongquynh.foodmooddiary.domain.repository.FoodEntryRepository
import com.haphuongquynh.foodmooddiary.util.common.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FoodEntryRepository
 * Offline-First: Local database as single source of truth, sync with Firebase
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Singleton
class FoodEntryRepositoryImpl @Inject constructor(
    private val foodEntryDao: FoodEntryDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : FoodEntryRepository {

    companion object {
        private const val TAG = "FoodEntryRepo"
    }

    private val currentUserId: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    /**
     * Flow that emits the current user ID whenever auth state changes
     */
    private val authStateFlow: Flow<String> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val userId = auth.currentUser?.uid ?: ""
            Log.d(TAG, "Auth state changed, userId: $userId")
            trySend(userId)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }

    override fun getAllEntries(): Flow<List<FoodEntry>> {
        // Combine auth state with database query to automatically re-query when user changes
        return authStateFlow.flatMapLatest { userId ->
            Log.d(TAG, "Getting all entries for userId: $userId")
            if (userId.isEmpty()) {
                // Return empty list if no user is logged in
                flowOf(emptyList())
            } else {
                foodEntryDao.getAllEntries(userId)
                    .map { entities -> 
                        Log.d(TAG, "Found ${entities.size} entries for userId: $userId")
                        entities.map { it.toDomain() } 
                    }
            }
        }
    }

    override fun getEntriesInDateRange(startDate: Long, endDate: Long): Flow<List<FoodEntry>> {
        return authStateFlow.flatMapLatest { userId ->
            if (userId.isEmpty()) {
                flowOf(emptyList())
            } else {
                foodEntryDao.getEntriesInDateRange(userId, startDate, endDate)
                    .map { entities -> entities.map { it.toDomain() } }
            }
        }
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

            Log.d(TAG, "Adding entry: id=${entryWithId.id}, userId=${entryWithId.userId}, foodName=${entryWithId.foodName}")

            // Upload photo to Firebase Storage if local path exists
            var finalEntry = entryWithId
            if (!entryWithId.localPhotoPath.isNullOrEmpty()) {
                Log.d(TAG, "Uploading photo from: ${entryWithId.localPhotoPath}")
                val uploadResult = uploadPhoto(entryWithId.localPhotoPath)
                if (uploadResult is Resource.Success) {
                    finalEntry = entryWithId.copy(photoUrl = uploadResult.data)
                    Log.d(TAG, "Photo uploaded successfully: ${uploadResult.data}")
                } else {
                    Log.e(TAG, "Photo upload failed, continuing without cloud photo")
                }
            }

            // Save to local database first (Offline-First)
            val entity = FoodEntryEntity.fromDomain(finalEntry)
            foodEntryDao.insertEntry(entity)
            
            Log.d(TAG, "Entry saved to local DB successfully")

            // Try to sync with Firestore
            syncEntryToFirestore(finalEntry)

            Resource.success(finalEntry)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add entry: ${e.message}", e)
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
            val userId = currentUserId
            if (userId.isEmpty()) {
                return Resource.error("User not authenticated")
            }
            
            Log.d(TAG, "Starting sync for userId: $userId")
            
            // 1. Download entries from Firestore that belong to this user
            try {
                val remoteEntries = firestore.collection("foodEntries")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                
                Log.d(TAG, "Found ${remoteEntries.size()} remote entries for user")
                
                remoteEntries.documents.forEach { doc ->
                    try {
                        // Log raw Firestore data
                        Log.d(TAG, "=== Raw Firestore doc ${doc.id} ===")
                        Log.d(TAG, "  foodName: ${doc.getString("foodName")}")
                        Log.d(TAG, "  mood: ${doc.getString("mood")}")
                        Log.d(TAG, "  moodColor: ${doc.getLong("moodColor")}")
                        Log.d(TAG, "  photoUrl: ${doc.getString("photoUrl")}")
                        Log.d(TAG, "  localPhotoPath: ${doc.getString("localPhotoPath")}")
                        
                        val entry = documentToFoodEntry(doc, userId)
                        if (entry != null) {
                            val entity = FoodEntryEntity.fromDomain(entry.copy(isSynced = true))
                            foodEntryDao.insertEntry(entity)
                            Log.d(TAG, "Saved entry: ${entry.foodName}, mood=${entry.mood}, photoUrl=${entry.photoUrl}")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse entry ${doc.id}: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to download from Firestore: ${e.message}")
            }
            
            // 2. Upload unsynced local entries to Firestore
            val unsyncedEntries = foodEntryDao.getUnsyncedEntries(userId)
            Log.d(TAG, "Uploading ${unsyncedEntries.size} unsynced entries")
            unsyncedEntries.forEach { entity ->
                syncEntryToFirestore(entity.toDomain())
            }

            Resource.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed: ${e.message}", e)
            Resource.error("Sync failed: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Upload missing photos for entries that have localPhotoPath but no photoUrl
     */
    override suspend fun uploadMissingPhotos(): Int {
        var uploadedCount = 0
        try {
            if (currentUserId.isEmpty()) {
                Log.w(TAG, "Cannot upload missing photos: No user logged in")
                return 0
            }
            
            // Get all entries from local DB that have localPhotoPath but no photoUrl
            val allEntries = foodEntryDao.getAllEntriesOnce(currentUserId)
            Log.d(TAG, "Checking ${allEntries.size} entries for missing photos")
            
            for (entity in allEntries) {
                val localPath = entity.localPhotoPath
                val photoUrl = entity.photoUrl
                
                // Skip if already has photoUrl or no local path
                if (!photoUrl.isNullOrEmpty() || localPath.isNullOrEmpty()) {
                    continue
                }
                
                val file = File(localPath)
                if (!file.exists()) {
                    Log.d(TAG, "Local file not found for entry ${entity.id}: $localPath")
                    continue
                }
                
                Log.d(TAG, "Uploading photo for entry ${entity.id}: $localPath")
                val uploadResult = uploadPhoto(localPath)
                
                if (uploadResult is Resource.Success) {
                    val newPhotoUrl = uploadResult.data!!
                    // Update local DB
                    val updatedEntity = entity.copy(photoUrl = newPhotoUrl)
                    foodEntryDao.updateEntry(updatedEntity)
                    
                    // Update Firestore
                    syncEntryToFirestore(updatedEntity.toDomain())
                    
                    uploadedCount++
                    Log.d(TAG, "Successfully uploaded photo for entry ${entity.id}")
                } else {
                    Log.e(TAG, "Failed to upload photo for entry ${entity.id}")
                }
            }
            
            Log.d(TAG, "Upload missing photos completed: $uploadedCount photos uploaded")
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading missing photos: ${e.message}", e)
        }
        return uploadedCount
    }

    /**
     * Convert Firestore document to FoodEntry
     */
    private fun documentToFoodEntry(doc: com.google.firebase.firestore.DocumentSnapshot, fallbackUserId: String): FoodEntry? {
        return try {
            val locationMap = doc.get("location") as? Map<*, *>
            val location = if (locationMap != null) {
                Location(
                    latitude = (locationMap["latitude"] as? Number)?.toDouble() ?: 0.0,
                    longitude = (locationMap["longitude"] as? Number)?.toDouble() ?: 0.0,
                    address = locationMap["address"] as? String
                )
            } else null
            
            val rawMoodColor = (doc.getLong("moodColor") ?: 0).toInt()
            val rawMood = doc.getString("mood")
            
            // Try to determine mood from existing data
            var finalMood = rawMood
            var finalMoodColor = rawMoodColor
            
            // If no mood emoji but has moodColor, try to infer mood
            if (finalMood.isNullOrEmpty() && rawMoodColor != 0) {
                val inferredMood = MoodType.fromColorInt(rawMoodColor)
                if (inferredMood != null) {
                    finalMood = inferredMood.emoji
                    finalMoodColor = inferredMood.colorInt
                    Log.d(TAG, "Inferred mood ${inferredMood.emoji} from color $rawMoodColor")
                }
            }
            
            // If still no mood, assign default based on any color hint
            if (finalMood.isNullOrEmpty() && rawMoodColor != 0) {
                // Default to HAPPY if we have some color but can't match
                finalMood = MoodType.HAPPY.emoji
                finalMoodColor = MoodType.HAPPY.colorInt
                Log.d(TAG, "Defaulting to HAPPY for entry with unmapped color $rawMoodColor")
            }
            
            Log.d(TAG, "Parsed entry: id=${doc.id}, rawMood=$rawMood, rawColor=$rawMoodColor, finalMood=$finalMood, finalColor=$finalMoodColor")
            
            FoodEntry(
                id = doc.getString("id") ?: doc.id,
                userId = doc.getString("userId") ?: fallbackUserId,
                foodName = doc.getString("foodName") ?: "Unknown",
                notes = doc.getString("notes") ?: "",
                photoUrl = doc.getString("photoUrl"),
                localPhotoPath = null,
                moodColor = finalMoodColor,
                mood = finalMood,
                mealType = doc.getString("mealType"),
                location = location,
                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis(),
                isSynced = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing document: ${e.message}")
            null
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
                "localPhotoPath" to entry.localPhotoPath,
                "moodColor" to entry.moodColor,
                "mood" to entry.mood,
                "mealType" to entry.mealType,
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
            
            Log.d(TAG, "Syncing to Firestore: ${entry.foodName}, mood=${entry.mood}, photoUrl=${entry.photoUrl}")

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
