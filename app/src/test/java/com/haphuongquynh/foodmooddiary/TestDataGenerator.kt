package com.haphuongquynh.foodmooddiary.testing

import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.model.Location
import java.util.*

/**
 * Test data generators for unit testing
 */
object TestDataGenerator {

    fun generateFoodEntry(
        id: String = UUID.randomUUID().toString(),
        userId: String = "test_user",
        foodName: String = "Test Food",
        notes: String? = "Test notes",
        moodColor: Int = android.graphics.Color.GREEN,
        timestamp: Long = System.currentTimeMillis(),
        latitude: Double = 37.7749,
        longitude: Double = -122.4194,
        photoUri: String? = null
    ): FoodEntry {
        return FoodEntry(
            id = id,
            userId = userId,
            foodName = foodName,
            notes = notes,
            moodColor = moodColor,
            timestamp = timestamp,
            location = Location(latitude, longitude),
            photoUri = photoUri
        )
    }

    fun generateFoodEntries(count: Int): List<FoodEntry> {
        return (1..count).map { index ->
            generateFoodEntry(
                foodName = "Food $index",
                notes = "Notes for food $index",
                timestamp = System.currentTimeMillis() - (index * 3600000L) // 1 hour apart
            )
        }
    }

    fun generateFoodEntriesWithColors(colors: List<Int>): List<FoodEntry> {
        return colors.mapIndexed { index, color ->
            generateFoodEntry(
                foodName = "Food ${index + 1}",
                moodColor = color,
                timestamp = System.currentTimeMillis() - (index * 3600000L)
            )
        }
    }

    fun generateFoodEntriesInDateRange(
        startDate: Long,
        endDate: Long,
        count: Int
    ): List<FoodEntry> {
        val interval = (endDate - startDate) / count
        return (0 until count).map { index ->
            generateFoodEntry(
                timestamp = startDate + (index * interval)
            )
        }
    }
}
