package com.haphuongquynh.foodmooddiary.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility for exporting and sharing data
 */
object ExportHelper {

    /**
     * Export food entries to JSON file
     */
    suspend fun exportToJson(
        context: Context,
        entries: List<FoodEntry>,
        fileName: String = "food_mood_diary_export_${System.currentTimeMillis()}.json"
    ): File = withContext(Dispatchers.IO) {
        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }
        
        val file = File(exportDir, fileName)
        val gson = Gson()
        val json = gson.toJson(entries)
        
        file.writeText(json)
        file
    }

    /**
     * Export food entries to CSV file
     */
    suspend fun exportToCsv(
        context: Context,
        entries: List<FoodEntry>,
        fileName: String = "food_mood_diary_export_${System.currentTimeMillis()}.csv"
    ): File = withContext(Dispatchers.IO) {
        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }
        
        val file = File(exportDir, fileName)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        
        file.bufferedWriter().use { writer ->
            // Header
            writer.write("ID,Food Name,Notes,Color,Mood Score,Latitude,Longitude,Timestamp\n")
            
            // Data
            entries.forEach { entry ->
                val row = listOf(
                    entry.id,
                    entry.foodName.replace(",", ";"),
                    entry.notes?.replace(",", ";") ?: "",
                    entry.moodColor.toString(),
                    calculateMoodScore(entry.moodColor).toString(),
                    entry.location?.latitude?.toString() ?: "",
                    entry.location?.longitude?.toString() ?: "",
                    dateFormat.format(Date(entry.timestamp))
                ).joinToString(",")
                
                writer.write("$row\n")
            }
        }
        
        file
    }

    /**
     * Share exported file
     */
    fun shareFile(context: Context, file: File, mimeType: String = "*/*") {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "FoodMoodDiary Export")
            putExtra(Intent.EXTRA_TEXT, "My food and mood diary entries")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooser = Intent.createChooser(shareIntent, "Share via")
        context.startActivity(chooser)
    }

    /**
     * Share multiple entries as text
     */
    fun shareEntriesAsText(context: Context, entries: List<FoodEntry>) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        
        val text = buildString {
            appendLine("📝 FoodMoodDiary Export")
            appendLine("Total entries: ${entries.size}")
            appendLine()
            
            entries.take(10).forEach { entry ->
                appendLine("🍽️ ${entry.foodName}")
                if (entry.notes != null) {
                    appendLine("   ${entry.notes}")
                }
                appendLine("   📅 ${dateFormat.format(Date(entry.timestamp))}")
                appendLine()
            }
            
            if (entries.size > 10) {
                appendLine("... and ${entries.size - 10} more entries")
            }
        }
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "My FoodMoodDiary")
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    /**
     * Calculate mood score from color (0-10)
     */
    private fun calculateMoodScore(color: Int): Float {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(color, hsv)
        
        val hue = hsv[0]
        val saturation = hsv[1]
        val value = hsv[2]
        
        // Map hue to mood score
        val hueScore = when {
            hue < 60 -> 8f + (hue / 60f) * 2f // Red to Yellow: 8-10
            hue < 120 -> 6f + ((hue - 60f) / 60f) * 2f // Yellow to Green: 6-8
            hue < 180 -> 5f + ((hue - 120f) / 60f) * 1f // Green to Cyan: 5-6
            hue < 240 -> 3f + ((hue - 180f) / 60f) * 2f // Cyan to Blue: 3-5
            hue < 300 -> 2f + ((hue - 240f) / 60f) * 1f // Blue to Magenta: 2-3
            else -> 4f + ((hue - 300f) / 60f) * 4f // Magenta to Red: 4-8
        }
        
        // Adjust for saturation and value
        return (hueScore * saturation * value).coerceIn(0f, 10f)
    }
}
