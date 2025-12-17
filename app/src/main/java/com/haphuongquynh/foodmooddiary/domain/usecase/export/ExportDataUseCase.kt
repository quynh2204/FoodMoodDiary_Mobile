package com.haphuongquynh.foodmooddiary.domain.usecase.export

import android.content.Context
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.repository.FoodEntryRepository
import com.haphuongquynh.foodmooddiary.util.ExportHelper
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject

/**
 * Use case for exporting food entries
 */
class ExportDataUseCase @Inject constructor(
    private val repository: FoodEntryRepository
) {
    
    suspend fun exportToJson(context: Context): Result<File> {
        return try {
            val entries = repository.getAllEntries().first()
            val file = ExportHelper.exportToJson(context, entries)
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun exportToCsv(context: Context): Result<File> {
        return try {
            val entries = repository.getAllEntries().first()
            val file = ExportHelper.exportToCsv(context, entries)
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun shareEntriesAsText(context: Context) {
        try {
            val entries = repository.getAllEntries().first()
            ExportHelper.shareEntriesAsText(context, entries)
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    suspend fun shareFileExport(context: Context, format: ExportFormat): Result<Unit> {
        return try {
            val entries = repository.getAllEntries().first()
            val file = when (format) {
                ExportFormat.JSON -> ExportHelper.exportToJson(context, entries)
                ExportFormat.CSV -> ExportHelper.exportToCsv(context, entries)
            }
            
            val mimeType = when (format) {
                ExportFormat.JSON -> "application/json"
                ExportFormat.CSV -> "text/csv"
            }
            
            ExportHelper.shareFile(context, file, mimeType)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

enum class ExportFormat {
    JSON,
    CSV
}
