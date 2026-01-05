package com.haphuongquynh.foodmooddiary.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.haphuongquynh.foodmooddiary.domain.model.User
import com.haphuongquynh.foodmooddiary.domain.repository.AuthRepository
import com.haphuongquynh.foodmooddiary.domain.usecase.auth.GetCurrentUserUseCase
import com.haphuongquynh.foodmooddiary.domain.usecase.auth.LoginUseCase
import com.haphuongquynh.foodmooddiary.domain.usecase.auth.LogoutUseCase
import com.haphuongquynh.foodmooddiary.domain.usecase.auth.RegisterUseCase
import com.haphuongquynh.foodmooddiary.util.common.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for Authentication screens
 * Handles login, register, and logout operations
 * Uses StateFlow for reactive UI updates
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val authRepository: AuthRepository,
    private val storage: FirebaseStorage
) : ViewModel() {

    // UI State for Login/Register screens
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Current user state
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Profile update state
    private val _profileUpdateState = MutableStateFlow<ProfileUpdateState>(ProfileUpdateState.Idle)
    val profileUpdateState: StateFlow<ProfileUpdateState> = _profileUpdateState.asStateFlow()

    init {
        observeCurrentUser()
    }

    /**
     * Observe current user from repository
     */
    private fun observeCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                _currentUser.value = user
            }
        }
    }

    /**
     * Login with email and password
     */
    fun login(email: String, password: String, rememberMe: Boolean = false) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val result = loginUseCase(email, password, rememberMe)
            
            _authState.value = when (result) {
                is Resource.Success -> AuthState.Success(result.data)
                is Resource.Error -> AuthState.Error(result.message)
                is Resource.Loading -> AuthState.Loading
            }
        }
    }

    /**
     * Register new user
     */
    fun register(
        email: String,
        password: String,
        confirmPassword: String,
        displayName: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val result = registerUseCase(email, password, confirmPassword, displayName)
            
            _authState.value = when (result) {
                is Resource.Success -> AuthState.Success(result.data)
                is Resource.Error -> AuthState.Error(result.message)
                is Resource.Loading -> AuthState.Loading
            }
        }
    }

    /**
     * Sign in with Google
     */
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val result = authRepository.signInWithGoogle(idToken)
            
            _authState.value = when (result) {
                is Resource.Success -> AuthState.Success(result.data)
                is Resource.Error -> AuthState.Error(result.message)
                is Resource.Loading -> AuthState.Loading
            }
        }
    }

    /**
     * Send password reset email
     */
    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val result = authRepository.sendPasswordResetEmail(email)
            
            _authState.value = when (result) {
                is Resource.Success -> AuthState.PasswordResetSent
                is Resource.Error -> AuthState.Error(result.message)
                is Resource.Loading -> AuthState.Loading
            }
        }
    }

    /**
     * Confirm password reset with code from email
     */
    fun confirmPasswordReset(code: String, newPassword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val result = authRepository.confirmPasswordReset(code, newPassword)
            
            _authState.value = when (result) {
                is Resource.Success -> AuthState.PasswordResetComplete
                is Resource.Error -> AuthState.Error(result.message)
                is Resource.Loading -> AuthState.Loading
            }
        }
    }

    /**
     * Logout current user
     */
    fun logout() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val result = logoutUseCase()
            
            _authState.value = when (result) {
                is Resource.Success -> AuthState.LoggedOut
                is Resource.Error -> AuthState.Error(result.message)
                is Resource.Loading -> AuthState.Loading
            }
        }
    }

    /**
     * Reset auth state to idle
     */
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    /**
     * Update user's theme preference
     * @param theme Theme preference: "Light", "Dark", or "Auto"
     */
    fun updateThemePreference(theme: String) {
        viewModelScope.launch {
            authRepository.updateThemePreference(theme)
        }
    }

    /**
     * Update user's display name
     */
    fun updateDisplayName(displayName: String) {
        viewModelScope.launch {
            _profileUpdateState.value = ProfileUpdateState.Loading

            val result = authRepository.updateProfile(displayName = displayName, photoUrl = null)

            _profileUpdateState.value = when (result) {
                is Resource.Success -> ProfileUpdateState.Success
                is Resource.Error -> ProfileUpdateState.Error(result.message)
                is Resource.Loading -> ProfileUpdateState.Loading
            }
        }
    }

    /**
     * Upload profile image to Firebase Storage and update user profile
     */
    fun updateProfileImage(localPath: String) {
        viewModelScope.launch {
            _profileUpdateState.value = ProfileUpdateState.Loading

            try {
                val file = File(localPath)
                if (!file.exists()) {
                    _profileUpdateState.value = ProfileUpdateState.Error("Image file not found")
                    return@launch
                }

                val userId = _currentUser.value?.uid ?: run {
                    _profileUpdateState.value = ProfileUpdateState.Error("User not logged in")
                    return@launch
                }

                // Upload to Firebase Storage
                val fileName = "profile_photos/${userId}/${UUID.randomUUID()}.jpg"
                val storageRef = storage.reference.child(fileName)

                val uri = Uri.fromFile(file)
                val uploadTask = storageRef.putFile(uri).await()
                val downloadUrl = uploadTask.storage.downloadUrl.await().toString()

                // Update user profile with new photo URL
                val result = authRepository.updateProfile(displayName = null, photoUrl = downloadUrl)

                _profileUpdateState.value = when (result) {
                    is Resource.Success -> ProfileUpdateState.Success
                    is Resource.Error -> ProfileUpdateState.Error(result.message)
                    is Resource.Loading -> ProfileUpdateState.Loading
                }
            } catch (e: Exception) {
                _profileUpdateState.value = ProfileUpdateState.Error("Upload failed: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Reset profile update state
     */
    fun resetProfileUpdateState() {
        _profileUpdateState.value = ProfileUpdateState.Idle
    }
}

/**
 * Sealed class representing profile update states
 */
sealed class ProfileUpdateState {
    data object Idle : ProfileUpdateState()
    data object Loading : ProfileUpdateState()
    data object Success : ProfileUpdateState()
    data class Error(val message: String) : ProfileUpdateState()
}

/**
 * Sealed class representing different authentication states
 */
sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
    data object LoggedOut : AuthState()
    data object PasswordResetSent : AuthState()
    data object PasswordResetComplete : AuthState()
}
