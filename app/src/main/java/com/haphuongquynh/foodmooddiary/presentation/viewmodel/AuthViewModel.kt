package com.haphuongquynh.foodmooddiary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val authRepository: AuthRepository
) : ViewModel() {

    // UI State for Login/Register screens
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Current user state
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

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
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val result = loginUseCase(email, password)
            
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
    fun signInWithGoogle() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            // TODO: Implement Google Sign-In
            // This is a placeholder for Google Sign-In implementation
            _authState.value = AuthState.Error("Google Sign-In not implemented yet")
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
}
