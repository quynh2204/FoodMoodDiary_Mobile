package com.haphuongquynh.foodmooddiary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.haphuongquynh.foodmooddiary.domain.model.User

/**
 * Room Entity representing User in local database
 * Follows Clean Architecture: Data layer entity separate from domain model
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val createdAt: Long,
    val lastLoginAt: Long
) {
    /**
     * Convert Entity to Domain Model
     */
    fun toDomain(): User {
        return User(
            uid = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoUrl,
            createdAt = createdAt,
            lastLoginAt = lastLoginAt
        )
    }
    
    companion object {
        /**
         * Convert Domain Model to Entity
         */
        fun fromDomain(user: User): UserEntity {
            return UserEntity(
                uid = user.uid,
                email = user.email,
                displayName = user.displayName,
                photoUrl = user.photoUrl,
                createdAt = user.createdAt,
                lastLoginAt = user.lastLoginAt
            )
        }
    }
}
