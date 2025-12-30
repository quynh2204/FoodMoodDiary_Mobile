package com.haphuongquynh.foodmooddiary.domain.usecase

import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.repository.FoodEntryRepository
import com.haphuongquynh.foodmooddiary.testing.TestDataGenerator
import com.haphuongquynh.foodmooddiary.util.common.Resource
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for AddFoodEntryUseCase
 */
class AddFoodEntryUseCaseTest {

    private lateinit var repository: FoodEntryRepository
    private lateinit var useCase: com.haphuongquynh.foodmooddiary.domain.usecase.entry.AddEntryUseCase

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = com.haphuongquynh.foodmooddiary.domain.usecase.entry.AddEntryUseCase(repository)
    }
    
    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `addEntry should call repository addEntry`() = runTest {
        // Given
        val entry = TestDataGenerator.generateFoodEntry()
        coEvery { repository.addEntry(entry) } returns Resource.success(entry)

        // When
        val result = useCase(entry)

        // Then
        coVerify { repository.addEntry(entry) }
        assertTrue(result is Resource.Success)
    }

    @Test
    fun `addEntry should return failure when repository throws exception`() = runTest {
        // Given
        val entry = TestDataGenerator.generateFoodEntry()
        coEvery { repository.addEntry(entry) } returns Resource.error("Database error")

        // When
        val result = useCase(entry)

        // Then
        assertTrue(result is Resource.Error)
    }
}
