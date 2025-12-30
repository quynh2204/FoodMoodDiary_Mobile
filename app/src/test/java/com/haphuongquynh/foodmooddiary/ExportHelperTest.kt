package com.haphuongquynh.foodmooddiary.util

import android.content.Context
import com.haphuongquynh.foodmooddiary.testing.TestDataGenerator
import com.haphuongquynh.foodmooddiary.util.export.ExportHelper
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Unit tests for ExportHelper
 * Tests CSV and JSON export functionality
 */
class ExportHelperTest {

    private lateinit var mockContext: Context
    private lateinit var mockCacheDir: File

    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
        mockCacheDir = createTempDir()
        every { mockContext.cacheDir } returns mockCacheDir
    }

    @Test
    fun `exportToJson should create valid JSON file`() = runTest {
        // Given
        val entries = TestDataGenerator.generateFoodEntries(3)

        // When
        val file = ExportHelper.exportToJson(mockContext, entries)

        // Then
        assertTrue(file.exists())
        assertTrue(file.name.endsWith(".json"))
        assertTrue(file.length() > 0)
        
        val content = file.readText()
        assertTrue(content.contains("\"foodName\""))
        assertTrue(content.contains("\"moodColor\""))
        assertTrue(content.contains("Food 1"))
    }

    @Test
    fun `exportToJson with empty list should create empty JSON array`() = runTest {
        // Given
        val entries = emptyList<com.haphuongquynh.foodmooddiary.domain.model.FoodEntry>()

        // When
        val file = ExportHelper.exportToJson(mockContext, entries)

        // Then
        assertTrue(file.exists())
        val content = file.readText()
        assertEquals("[]", content)
    }

    @Test
    fun `exportToCsv should create valid CSV file with header`() = runTest {
        // Given
        val entries = TestDataGenerator.generateFoodEntries(2)

        // When
        val file = ExportHelper.exportToCsv(mockContext, entries)

        // Then
        assertTrue(file.exists())
        assertTrue(file.name.endsWith(".csv"))
        
        val lines = file.readLines()
        assertTrue(lines.isNotEmpty())
        
        // Check header
        val header = lines.first()
        assertTrue(header.contains("ID"))
        assertTrue(header.contains("Food Name"))
        assertTrue(header.contains("Notes"))
        assertTrue(header.contains("Mood Score"))
        assertTrue(header.contains("Timestamp"))
        
        // Check data rows
        assertEquals(3, lines.size) // Header + 2 entries
        assertTrue(lines[1].contains("Food 1"))
        assertTrue(lines[2].contains("Food 2"))
    }

    @Test
    fun `exportToCsv should handle special characters in food names`() = runTest {
        // Given
        val entry = TestDataGenerator.generateFoodEntry(
            foodName = "Pizza, Pasta & More",
            notes = "Delicious, amazing!"
        )

        // When
        val file = ExportHelper.exportToCsv(mockContext, listOf(entry))

        // Then
        val lines = file.readLines()
        assertEquals(2, lines.size) // Header + 1 entry
        
        // Commas in food name should be replaced with semicolons
        val dataLine = lines[1]
        assertTrue(dataLine.contains("Pizza; Pasta & More"))
        assertTrue(dataLine.contains("Delicious; amazing!"))
    }

    @Test
    fun `exportToCsv with empty list should only have header`() = runTest {
        // Given
        val entries = emptyList<com.haphuongquynh.foodmooddiary.domain.model.FoodEntry>()

        // When
        val file = ExportHelper.exportToCsv(mockContext, entries)

        // Then
        assertTrue(file.exists())
        val lines = file.readLines()
        assertEquals(1, lines.size) // Only header
    }

    @Test
    fun `exportToCsv should include location data when available`() = runTest {
        // Given
        val entry = TestDataGenerator.generateFoodEntry(
            latitude = 37.7749,
            longitude = -122.4194
        )

        // When
        val file = ExportHelper.exportToCsv(mockContext, listOf(entry))

        // Then
        val lines = file.readLines()
        val dataLine = lines[1]
        assertTrue(dataLine.contains("37.7749"))
        assertTrue(dataLine.contains("-122.4194"))
    }

    @Test
    fun `exported files should be created in exports subdirectory`() = runTest {
        // Given
        val entries = TestDataGenerator.generateFoodEntries(1)

        // When
        val csvFile = ExportHelper.exportToCsv(mockContext, entries)
        val jsonFile = ExportHelper.exportToJson(mockContext, entries)

        // Then
        assertTrue(csvFile.parentFile?.name == "exports")
        assertTrue(jsonFile.parentFile?.name == "exports")
    }

    @Test
    fun `mood score calculation should produce valid range`() = runTest {
        // Given - entries with different mood colors
        val redEntry = TestDataGenerator.generateFoodEntry(
            moodColor = android.graphics.Color.RED
        )
        val greenEntry = TestDataGenerator.generateFoodEntry(
            moodColor = android.graphics.Color.GREEN
        )
        val blueEntry = TestDataGenerator.generateFoodEntry(
            moodColor = android.graphics.Color.BLUE
        )

        // When
        val file = ExportHelper.exportToCsv(
            mockContext, 
            listOf(redEntry, greenEntry, blueEntry)
        )

        // Then
        val lines = file.readLines()
        
        // Extract mood scores from CSV (column 4)
        val moodScores = lines.drop(1).map { line ->
            line.split(",")[4].toFloatOrNull() ?: 0f
        }
        
        // All mood scores should be between 0 and 10
        assertTrue(moodScores.all { it in 0f..10f })
    }

    @Test
    fun `JSON export should preserve all entry fields`() = runTest {
        // Given
        val entry = TestDataGenerator.generateFoodEntry(
            id = "test-id-123",
            userId = "user-456",
            foodName = "Test Food",
            notes = "Test notes",
            photoUrl = "https://example.com/photo.jpg"
        )

        // When
        val file = ExportHelper.exportToJson(mockContext, listOf(entry))

        // Then
        val content = file.readText()
        assertTrue(content.contains("\"id\":\"test-id-123\""))
        assertTrue(content.contains("\"userId\":\"user-456\""))
        assertTrue(content.contains("\"foodName\":\"Test Food\""))
        assertTrue(content.contains("\"notes\":\"Test notes\""))
        assertTrue(content.contains("\"photoUrl\":\"https://example.com/photo.jpg\""))
    }
}
