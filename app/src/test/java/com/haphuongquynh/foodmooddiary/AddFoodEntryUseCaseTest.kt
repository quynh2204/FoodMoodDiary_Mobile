package com.haphuongquynh.foodmooddiary.domain.usecase

import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.repository.FoodEntryRepository
import com.haphuongquynh.foodmooddiary.testing.TestDataGenerator
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for AddFoodEntryUseCase
 */
class AddFoodEntryUseCaseTest {

    @Mock
    private lateinit var repository: FoodEntryRepository

    private lateinit var useCase: com.haphuongquynh.foodmooddiary.domain.usecase.entry.AddFoodEntryUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = com.haphuongquynh.foodmooddiary.domain.usecase.entry.AddFoodEntryUseCase(repository)
    }

    @Test
    fun `addEntry should call repository addEntry`() = runTest {
        // Given
        val entry = TestDataGenerator.generateFoodEntry()

        // When
        val result = useCase(entry)

        // Then
        verify(repository).addEntry(entry)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `addEntry should return failure when repository throws exception`() = runTest {
        // Given
        val entry = TestDataGenerator.generateFoodEntry()
        `when`(repository.addEntry(entry)).thenThrow(RuntimeException("Database error"))

        // When
        val result = useCase(entry)

        // Then
        assertTrue(result.isFailure)
    }
}
