package com.haphuongquynh.foodmooddiary.presentation.screens.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.FoodEntryViewModel
import com.haphuongquynh.foodmooddiary.util.export.ExportHelper

/**
 * Entry Detail Screen Wrapper
 * Loads entry by ID and displays ModernEntryDetailScreen
 */
@Composable
fun EntryDetailScreen(
    entryId: String,
    navController: NavController,
    viewModel: FoodEntryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val entries by viewModel.entries.collectAsState()
    val entry = entries.firstOrNull { it.id == entryId }

    if (entry != null) {
        ModernEntryDetailScreen(
            entry = entry,
            onNavigateBack = { navController.navigateUp() },
            onShare = {
                // Share single entry as text
                ExportHelper.shareEntriesAsText(context, listOf(entry))
            }
        )
    } else {
        // Loading or not found
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
