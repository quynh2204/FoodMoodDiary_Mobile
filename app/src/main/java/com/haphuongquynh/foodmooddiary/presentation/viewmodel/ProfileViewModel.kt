package com.haphuongquynh.foodmooddiary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.haphuongquynh.foodmooddiary.util.notification.WorkManagerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for Profile Screen
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    val workManagerHelper: WorkManagerHelper
) : ViewModel()
