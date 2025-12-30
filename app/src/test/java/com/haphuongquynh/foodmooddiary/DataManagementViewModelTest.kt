package com.haphuongquynh.foodmooddiary.viewmodel

import android.content.Context
import app.cash.turbine.test
import com.haphuongquynh.foodmooddiary.domain.repository.FoodEntryRepository
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.DataManagementViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.ExportState
import com.haphuongquynh.foodmooddiary.testing.TestDataGenerator
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for DataManagementViewModel
 * Tests export operations and data clearing functionality
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DataManagementViewModelTest {

    private lateinit var viewModel: DataManagementViewModel
    private lateinit var foodEntryRepository: FoodEntryRepository
    private lateinit var mockContext: Context
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        foodEntryRepository = mockk(relaxed = true)
        mockContext = mockk(relaxed = true)
        viewModel = DataManagementViewModel(foodEntryRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `exportToCSV should emit Loading then Success state`() = runTest {
        // Given
        val testEntries = TestDataGenerator.generateFoodEntries(5)
        coEvery { foodEntryRepository.getAllEntries() } returns flowOf(testEntries)

        // When
        viewModel.exportState.test {
            viewModel.exportToCSV(mockContext)
            
            // Then
            assertEquals(ExportState.Idle, awaitItem())
            assertEquals(ExportState.Loading, awaitItem())
            
            val successState = awaitItem()
            assertTrue(successState is ExportState.Success)
            assertEquals("CSV", (successState as ExportState.Success).format)
        }
    }

    @Test
    fun `exportToCSV with empty entries should emit Error state`() = runTest {
        // Given
        coEvery { foodEntryRepository.getAllEntries() } returns flowOf(emptyList())

        // When
        viewModel.exportState.test {
            viewModel.exportToCSV(mockContext)
            
            // Then
            assertEquals(ExportState.Idle, awaitItem())
            assertEquals(ExportState.Loading, awaitItem())
            
            val errorState = awaitItem()
            assertTrue(errorState is ExportState.Error)
            assertEquals("No entries to export", (errorState as ExportState.Error).message)
        }
    }

    @Test
    fun `exportToJSON should emit Loading then Success state`() = runTest {
        // Given
        val testEntries = TestDataGenerator.generateFoodEntries(3)
        coEvery { foodEntryRepository.getAllEntries() } returns flowOf(testEntries)

        // When
        viewModel.exportState.test {
            viewModel.exportToJSON(mockContext)
            
            // Then
            assertEquals(ExportState.Idle, awaitItem())
            assertEquals(ExportState.Loading, awaitItem())
            
            val successState = awaitItem()
            assertTrue(successState is ExportState.Success)
            assertEquals("JSON", (successState as ExportState.Success).format)
        }
    }

    @Test
    fun `exportToPDF should emit Loading then Success state`() = runTest {
        // Given
        val testEntries = TestDataGenerator.generateFoodEntries(2)
        coEvery { foodEntryRepository.getAllEntries() } returns flowOf(testEntries)

        // When
        viewModel.exportState.test {
            viewModel.exportToPDF(mockContext)
            
            // Then
            assertEquals(ExportState.Idle, awaitItem())
            assertEquals(ExportState.Loading, awaitItem())
            
            val successState = awaitItem()
            assertTrue(successState is ExportState.Success)
            assertEquals("PDF/Text", (successState as ExportState.Success).format)
        }
    }

    @Test
    fun `clearAllData should delete all entries and emit DataCleared state`() = runTest {
        // Given
        val testEntries = TestDataGenerator.generateFoodEntries(10)
        coEvery { foodEntryRepository.getAllEntries() } returns flowOf(testEntries)
        coEvery { foodEntryRepository.deleteEntry(any()) } returns mockk(relaxed = true)

        // When
        viewModel.exportState.test {
            viewModel.clearAllData()
            
            // Then
            assertEquals(ExportState.Idle, awaitItem())
            assertEquals(ExportState.Loading, awaitItem())
            assertEquals(ExportState.DataCleared, awaitItem())
            
            // Verify all entries were deleted
            coVerify(exactly = testEntries.size) { 
                foodEntryRepository.deleteEntry(any()) 
            }
        }
    }

    @Test
    fun `clearAllData with empty database should still emit DataCleared`() = runTest {
        // Given
        coEvery { foodEntryRepository.getAllEntries() } returns flowOf(emptyList())

        // When
        viewModel.exportState.test {
            viewModel.clearAllData()
            
            // Then
            assertEquals(ExportState.Idle, awaitItem())
            assertEquals(ExportState.Loading, awaitItem())
            assertEquals(ExportState.DataCleared, awaitItem())
            
            // Verify no deletions occurred
            coVerify(exactly = 0) { foodEntryRepository.deleteEntry(any()) }
        }
    }

    @Test
    fun `resetState should set state back to Idle`() = runTest {
        // Given
        val testEntries = TestDataGenerator.generateFoodEntries(1)
        coEvery { foodEntryRepository.getAllEntries() } returns flowOf(testEntries)

        // When
        viewModel.exportState.test {
            viewModel.exportToCSV(mockContext)
            
            skipItems(2) // Skip Idle and Loading
            val successState = awaitItem()
            assertTrue(successState is ExportState.Success)
            
            // Reset state
            viewModel.resetState()
            
            // Then
            assertEquals(ExportState.Idle, awaitItem())
        }
    }

    @Test
    fun `export should handle repository exceptions gracefully`() = runTest {
        // Given
        coEvery { foodEntryRepository.getAllEntries() } throws Exception("Database error")

        // When
        viewModel.exportState.test {
            viewModel.exportToCSV(mockContext)
            
            // Then
            assertEquals(ExportState.Idle, awaitItem())
            assertEquals(ExportState.Loading, awaitItem())
            
            val errorState = awaitItem()
            assertTrue(errorState is ExportState.Error)
            assertTrue((errorState as ExportState.Error).message.contains("CSV export failed"))
        }
    }
}
