package com.haphuongquynh.foodmooddiary.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.model.Location

/**
 * Room Entity for FoodEntry
 * Stores food entries locally for offline-first functionality
 */
@Entity(tableName = "food_entries")
data class FoodEntryEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val foodName: String,
    val notes: String,
    val photoUrl: String?,
    val localPhotoPath: String?,
    val moodColor: Int,
    val mood: String?,
    val mealType: String?, // Breakfast, Lunch, Dinner, Snack
    @Embedded(prefix = "loc_")
    val location: LocationEntity?,
    val timestamp: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean
) {
    fun toDomain(): FoodEntry {
        return FoodEntry(
            id = id,
            userId = userId,
            foodName = foodName,
            notes = notes,
            photoUrl = photoUrl,
            localPhotoPath = localPhotoPath,
            moodColor = moodColor,
            mood = mood,
            mealType = mealType,
            location = location?.toDomain(),
            timestamp = timestamp,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isSynced = isSynced
        )
    }
    
    companion object {
        fun fromDomain(entry: FoodEntry): FoodEntryEntity {
            return FoodEntryEntity(
                id = entry.id,
                userId = entry.userId,
                foodName = entry.foodName,
                notes = entry.notes,
                photoUrl = entry.photoUrl,
                localPhotoPath = entry.localPhotoPath,
                moodColor = entry.moodColor,
                mood = entry.mood,
                mealType = entry.mealType,
                location = entry.location?.let { LocationEntity.fromDomain(it) },
                timestamp = entry.timestamp,
                createdAt = entry.createdAt,
                updatedAt = entry.updatedAt,
                isSynced = entry.isSynced
            )
        }
    }
}

/**
 * Embedded location data in FoodEntry
 */
data class LocationEntity(
    val latitude: Double,
    val longitude: Double,
    val address: String?
) {
    fun toDomain(): Location {
        return Location(latitude, longitude, address)
    }
    
    companion object {
        fun fromDomain(location: Location): LocationEntity {
            return LocationEntity(
                latitude = location.latitude,
                longitude = location.longitude,
                address = location.address
            )
        }
    }
}
