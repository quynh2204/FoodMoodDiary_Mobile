package com.haphuongquynh.foodmooddiary.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.haphuongquynh.foodmooddiary.data.local.dao.UserDao
import com.haphuongquynh.foodmooddiary.data.local.entity.UserEntity
import com.haphuongquynh.foodmooddiary.domain.model.User
import com.haphuongquynh.foodmooddiary.domain.repository.AuthRepository
import com.haphuongquynh.foodmooddiary.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository
 * Handles Firebase Authentication and local database operations
 * Follows Offline-First strategy: cache data locally and sync with Firebase
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
) : AuthRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Sync Firebase Auth user to Room DB immediately on startup
        scope.launch {
            syncFirebaseUserToRoom()
        }
    }

    private suspend fun syncFirebaseUserToRoom() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            val user = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "User",
                photoUrl = firebaseUser.photoUrl?.toString(),
                createdAt = firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis()
            )
            userDao.insertUser(UserEntity.fromDomain(user))
            android.util.Log.d("AuthRepo", "Synced Firebase user to Room: ${user.email}")
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return userDao.getCurrentUser().map { it?.toDomain() }
    }

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            // Sign in with Firebase
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Resource.error("Login failed: No user data")

            // Create User object
            val user = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: email,
                displayName = firebaseUser.displayName ?: email.substringBefore("@"),
                photoUrl = firebaseUser.photoUrl?.toString(),
                createdAt = firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis()
            )

            // Save to local database (Offline-First)
            userDao.insertUser(UserEntity.fromDomain(user))

            // Sync with Firestore
            syncUserToFirestore(user)

            Resource.success(user)
        } catch (e: Exception) {
            Resource.error("Login failed: ${e.localizedMessage}", e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): Resource<User> {
        return try {
            // Create account with Firebase
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Resource.error("Registration failed")

            // Update profile with display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // Create User object
            val user = User(
                uid = firebaseUser.uid,
                email = email,
                displayName = displayName,
                photoUrl = null,
                createdAt = System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis()
            )

            // Save to local database
            userDao.insertUser(UserEntity.fromDomain(user))

            // Sync with Firestore
            syncUserToFirestore(user)

            Resource.success(user)
        } catch (e: Exception) {
            Resource.error("Registration failed: ${e.localizedMessage}", e)
        }
    }

    override suspend fun logout(): Resource<Unit> {
        return try {
            // Sign out from Firebase
            firebaseAuth.signOut()

            // Clear local database
            userDao.deleteAllUsers()

            Resource.success(Unit)
        } catch (e: Exception) {
            Resource.error("Logout failed: ${e.localizedMessage}", e)
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun updateProfile(displayName: String?, photoUrl: String?): Resource<User> {
        return try {
            val firebaseUser = firebaseAuth.currentUser 
                ?: return Resource.error("No authenticated user")

            // Update Firebase profile
            val profileUpdates = UserProfileChangeRequest.Builder().apply {
                displayName?.let { setDisplayName(it) }
                photoUrl?.let { setPhotoUri(android.net.Uri.parse(it)) }
            }.build()
            firebaseUser.updateProfile(profileUpdates).await()

            // Create updated User object
            val user = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = displayName ?: firebaseUser.displayName ?: "",
                photoUrl = photoUrl ?: firebaseUser.photoUrl?.toString(),
                createdAt = firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis()
            )

            // Update local database
            userDao.insertUser(UserEntity.fromDomain(user))

            // Sync with Firestore
            syncUserToFirestore(user)

            Resource.success(user)
        } catch (e: Exception) {
            Resource.error("Profile update failed: ${e.localizedMessage}", e)
        }
    }

    override suspend fun updateThemePreference(themePreference: String): Resource<User> {
        return try {
            val firebaseUser = firebaseAuth.currentUser 
                ?: return Resource.error("No authenticated user")

            // Get current user from database
            val currentUserEntity = userDao.getUserById(firebaseUser.uid)
                ?: return Resource.error("User not found in local database")

            // Update theme preference
            val updatedUser = currentUserEntity.toDomain().copy(
                themePreference = themePreference
            )

            // Save to local database
            userDao.insertUser(UserEntity.fromDomain(updatedUser))

            // Sync with Firestore
            syncUserToFirestore(updatedUser)

            Resource.success(updatedUser)
        } catch (e: Exception) {
            Resource.error("Theme update failed: ${e.localizedMessage}", e)
        }
    }

    /**
     * Sync user data to Firestore for backup and cross-device access
     */
    private suspend fun syncUserToFirestore(user: User) {
        try {
            val userMap = hashMapOf(
                "uid" to user.uid,
                "email" to user.email,
                "displayName" to user.displayName,
                "photoUrl" to user.photoUrl,
                "createdAt" to user.createdAt,
                "lastLoginAt" to user.lastLoginAt,
                "themePreference" to user.themePreference
            )
            firestore.collection("users")
                .document(user.uid)
                .set(userMap)
                .await()
        } catch (e: Exception) {
            // Fail silently - local data is preserved
            android.util.Log.e("AuthRepository", "Firestore sync failed: ${e.message}")
        }
    }
}