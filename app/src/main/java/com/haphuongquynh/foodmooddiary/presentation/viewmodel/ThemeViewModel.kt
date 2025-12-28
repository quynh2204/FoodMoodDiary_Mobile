package com.haphuongquynh.foodmooddiary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haphuongquynh.foodmooddiary.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Theme ViewModel - Quản lý theme preference
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _themeMode = MutableStateFlow("Dark") // "Light", "Dark", "Auto"
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    init {
        loadThemePreference()
    }

    private fun loadThemePreference() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                user?.let {
                    _themeMode.value = it.themePreference
                }
            }
        }
    }

    fun updateThemeMode(mode: String) {
        viewModelScope.launch {
            _themeMode.value = mode
            // Update in repository
            authRepository.updateThemePreference(mode)
        }
    }

    fun toggleTheme() {
        val newMode = if (_themeMode.value == "Dark") "Light" else "Dark"
        updateThemeMode(newMode)
    }
}
