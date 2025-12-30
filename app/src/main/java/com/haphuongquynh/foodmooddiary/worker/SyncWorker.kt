package com.haphuongquynh.foodmooddiary.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.haphuongquynh.foodmooddiary.data.local.dao.FoodEntryDao
import com.haphuongquynh.foodmooddiary.util.notification.NotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

/**
 * SyncWorker - Day 23
 * Periodically syncs local data with Firebase
 * Offline-first strategy: upload unsynced entries
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val foodEntryDao: FoodEntryDao,
    private val firebaseAuth: FirebaseAuth,
    private val notificationService: NotificationService
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "data_sync_work"
    }

    override suspend fun doWork(): Result {
        val currentUser = firebaseAuth.currentUser ?: return Result.failure()
        
        return try {
            // Show sync notification
            notificationService.showSyncNotification(isSyncing = true)
            
            // Get unsynced entries
            val unsyncedEntries = foodEntryDao.getUnsyncedEntries(currentUser.uid)
            
            var successCount = 0
            var errorCount = 0
            
            // Upload each unsynced entry
            unsyncedEntries.forEach { entity ->
                try {
                    // Upload photo if exists and not uploaded
                    var photoUrl = entity.photoUrl
                    if (entity.localPhotoPath != null && entity.photoUrl == null) {
                        // Upload to Firebase Storage
                        val storageRef = FirebaseStorage.getInstance().reference
                        val photoRef = storageRef.child("food_photos/${currentUser.uid}/${entity.id}.jpg")
                        val file = java.io.File(entity.localPhotoPath)
                        if (file.exists()) {
                            photoUrl = photoRef.putFile(android.net.Uri.fromFile(file))
                                .await()
                                .storage
                                .downloadUrl
                                .await()
                                .toString()
                        }
                    }
                    
                    // Update Firestore
                    val entryMap = hashMapOf(
                        "userId" to entity.userId,
                        "foodName" to entity.foodName,
                        "notes" to entity.notes,
                        "photoUrl" to photoUrl,
                        "moodColor" to entity.moodColor,
                        "location" to entity.location,
                        "timestamp" to entity.timestamp,
                        "createdAt" to entity.createdAt,
                        "updatedAt" to System.currentTimeMillis()
                    )
                    
                    // Upload to Firestore (using Firestore directly)
                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        .collection("food_entries")
                        .document(entity.id)
                        .set(entryMap)
                        .await()
                    
                    // Mark as synced
                    foodEntryDao.updateEntry(
                        entity.copy(
                            isSynced = true,
                            photoUrl = photoUrl
                        )
                    )
                    
                    successCount++
                } catch (e: Exception) {
                    errorCount++
                    e.printStackTrace()
                }
            }
            
            // Show completion notification
            notificationService.showSyncNotification(
                isSyncing = false,
                successCount = successCount,
                errorCount = errorCount
            )
            
            if (errorCount == 0) Result.success() else Result.retry()
        } catch (e: Exception) {
            notificationService.showSyncNotification(isSyncing = false, errorCount = 1)
            Result.failure()
        }
    }
}
