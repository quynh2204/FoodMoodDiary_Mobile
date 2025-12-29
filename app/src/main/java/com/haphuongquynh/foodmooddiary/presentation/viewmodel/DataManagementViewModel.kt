package com.haphuongquynh.foodmooddiary.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haphuongquynh.foodmooddiary.domain.repository.FoodEntryRepository
import com.haphuongquynh.foodmooddiary.util.export.ExportHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * ViewModel for Data Management Screen
 * Handles export and data clearing operations
 */
@HiltViewModel
class DataManagementViewModel @Inject constructor(
    private val foodEntryRepository: FoodEntryRepository
) : ViewModel() {

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    /**
     * Export all entries to CSV format
     */
    fun exportToCSV(context: Context) {
        viewModelScope.launch {
            try {
                _exportState.value = ExportState.Loading
                
                // Fetch all entries
                val entries = foodEntryRepository.getAllEntries().first()
                
                if (entries.isEmpty()) {
                    _exportState.value = ExportState.Error("No entries to export")
                    return@launch
                }
                
                // Export to CSV
                val file = ExportHelper.exportToCsv(context, entries)
                
                // Share the file
                ExportHelper.shareFile(context, file, "text/csv")
                
                _exportState.value = ExportState.Success(file, "CSV")
            } catch (e: Exception) {
                _exportState.value = ExportState.Error("CSV export failed: ${e.message}")
            }
        }
    }

    /**
     * Export all entries to JSON format
     */
    fun exportToJSON(context: Context) {
        viewModelScope.launch {
            try {
                _exportState.value = ExportState.Loading
                
                // Fetch all entries
                val entries = foodEntryRepository.getAllEntries().first()
                
                if (entries.isEmpty()) {
                    _exportState.value = ExportState.Error("No entries to export")
                    return@launch
                }
                
                // Export to JSON
                val file = ExportHelper.exportToJson(context, entries)
                
                // Share the file
                ExportHelper.shareFile(context, file, "application/json")
                
                _exportState.value = ExportState.Success(file, "JSON")
            } catch (e: Exception) {
                _exportState.value = ExportState.Error("JSON export failed: ${e.message}")
            }
        }
    }

    /**
     * Export all entries to PDF format
     * Note: PDF export requires Android PDF library or external tool
     * For now, we'll export as formatted text
     */
    fun exportToPDF(context: Context) {
        viewModelScope.launch {
            try {
                _exportState.value = ExportState.Loading
                
                // Fetch all entries
                val entries = foodEntryRepository.getAllEntries().first()
                
                if (entries.isEmpty()) {
                    _exportState.value = ExportState.Error("No entries to export")
                    return@launch
                }
                
                // Share as text (PDF generation would require additional library)
                ExportHelper.shareEntriesAsText(context, entries)
                
                _exportState.value = ExportState.Success(null, "PDF/Text")
            } catch (e: Exception) {
                _exportState.value = ExportState.Error("PDF export failed: ${e.message}")
            }
        }
    }

    /**
     * Clear all user data
     * Deletes all entries from local database
     */
    fun clearAllData() {
        viewModelScope.launch {
            try {
                _exportState.value = ExportState.Loading
                
                // Get all entries first
                val entries = foodEntryRepository.getAllEntries().first()
                
                // Delete each entry
                entries.forEach { entry ->
                    foodEntryRepository.deleteEntry(entry.id)
                }
                
                _exportState.value = ExportState.DataCleared
            } catch (e: Exception) {
                _exportState.value = ExportState.Error("Failed to clear data: ${e.message}")
            }
        }
    }

    /**
     * Reset export state
     */
    fun resetState() {
        _exportState.value = ExportState.Idle
    }
}

/**
 * UI State for export operations
 */
sealed class ExportState {
    data object Idle : ExportState()
    data object Loading : ExportState()
    data class Success(val file: File?, val format: String) : ExportState()
    data class Error(val message: String) : ExportState()
    data object DataCleared : ExportState()
}
