package com.haphuongquynh.foodmooddiary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for sensor settings
 */
@HiltViewModel
class SensorViewModel @Inject constructor() : ViewModel() {

    private val _shakeEnabled = MutableStateFlow(true)
    val shakeEnabled: StateFlow<Boolean> = _shakeEnabled.asStateFlow()

    private val _adaptiveThemeEnabled = MutableStateFlow(true)
    val adaptiveThemeEnabled: StateFlow<Boolean> = _adaptiveThemeEnabled.asStateFlow()
    
    private val _currentTheme = MutableStateFlow(ThemeMode.SYSTEM)
    val currentTheme: StateFlow<ThemeMode> = _currentTheme.asStateFlow()

    fun setShakeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _shakeEnabled.value = enabled
        }
    }

    fun setAdaptiveThemeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _adaptiveThemeEnabled.value = enabled
        }
    }
    
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            _currentTheme.value = mode
        }
    }
}

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM,
    ADAPTIVE // Based on light sensor
}
