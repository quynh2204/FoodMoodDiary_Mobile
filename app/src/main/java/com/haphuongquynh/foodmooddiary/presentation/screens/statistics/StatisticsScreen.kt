package com.haphuongquynh.foodmooddiary.presentation.screens.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.StatisticsViewModel

/**
 * Statistics Screen
 * Week 3 Day 19-20: Charts + Insights with MPAndroidChart
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val moodTrend by viewModel.moodTrend.collectAsStateWithLifecycle()
    val topFoods by viewModel.topFoods.collectAsStateWithLifecycle()
    val mealDistribution by viewModel.mealDistribution.collectAsStateWithLifecycle()
    val colorDistribution by viewModel.colorDistribution.collectAsStateWithLifecycle()
    val insights by viewModel.insights.collectAsStateWithLifecycle()
    val weeklySummary by viewModel.weeklySummary.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Charts", "Insights")

    var showDateRangePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Date Range Picker
                    IconButton(onClick = { showDateRangePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Date Range")
                    }
                    
                    // Export Data
                    IconButton(onClick = { /* TODO: Export to CSV */ }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Weekly Summary Card
            weeklySummary?.let { summary ->
                WeeklySummaryCard(
                    summary = summary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) },
                        icon = {
                            Icon(
                                imageVector = if (index == 0) Icons.Default.BarChart else Icons.Default.Psychology,
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> ChartsTab(
                    moodTrend = moodTrend,
                    topFoods = topFoods,
                    mealDistribution = mealDistribution,
                    colorDistribution = colorDistribution,
                    modifier = Modifier.fillMaxSize()
                )
                1 -> InsightsTab(
                    insights = insights,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Date Range Picker Dialog
        if (showDateRangePicker) {
            DateRangePickerDialog(
                onDismiss = { showDateRangePicker = false },
                onSelectRange = { range ->
                    viewModel.setDateRange(range)
                    showDateRangePicker = false
                }
            )
        }
    }
}

@Composable
fun ChartsTab(
    moodTrend: List<com.haphuongquynh.foodmooddiary.domain.model.MoodTrendPoint>,
    topFoods: List<com.haphuongquynh.foodmooddiary.domain.model.FoodFrequency>,
    mealDistribution: List<com.haphuongquynh.foodmooddiary.domain.model.MealDistribution>,
    colorDistribution: List<com.haphuongquynh.foodmooddiary.domain.model.ColorDistribution>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Mood Trend Chart
        ChartSection(title = "Mood Trend") {
            MoodTrendChart(data = moodTrend)
        }

        // Top Foods Chart
        ChartSection(title = "Top Foods by Frequency") {
            TopFoodsChart(data = topFoods)
        }

        // Meal Distribution Chart
        ChartSection(title = "Meal Type Distribution") {
            MealDistributionChart(data = mealDistribution)
        }

        // Color Distribution Chart
        ChartSection(title = "Color Palette Distribution") {
            ColorDistributionChart(data = colorDistribution)
        }
    }
}

@Composable
fun ChartSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 1.dp
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun WeeklySummaryCard(
    summary: com.haphuongquynh.foodmooddiary.domain.model.WeeklySummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "This Week",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    label = "Entries",
                    value = summary.totalEntries.toString(),
                    icon = Icons.Default.Restaurant
                )
                SummaryItem(
                    label = "Avg Mood",
                    value = String.format("%.1f", summary.averageMoodScore),
                    icon = Icons.Default.SentimentSatisfied
                )
                SummaryItem(
                    label = "Streak",
                    value = "${summary.streak} days",
                    icon = Icons.Default.LocalFireDepartment
                )
            }

            if (!summary.mostFrequentFood.isNullOrBlank()) {
                Divider()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "Top food: ${summary.mostFrequentFood}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    onDismiss: () -> Unit,
    onSelectRange: (com.haphuongquynh.foodmooddiary.presentation.viewmodel.DateRange) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Date Range") },
        text = {
            Column {
                com.haphuongquynh.foodmooddiary.presentation.viewmodel.DateRange.entries.forEach { range ->
                    TextButton(
                        onClick = { onSelectRange(range) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = when (range) {
                                com.haphuongquynh.foodmooddiary.presentation.viewmodel.DateRange.LAST_7_DAYS -> "Last 7 Days"
                                com.haphuongquynh.foodmooddiary.presentation.viewmodel.DateRange.LAST_30_DAYS -> "Last 30 Days"
                                com.haphuongquynh.foodmooddiary.presentation.viewmodel.DateRange.LAST_90_DAYS -> "Last 90 Days"
                                com.haphuongquynh.foodmooddiary.presentation.viewmodel.DateRange.LAST_YEAR -> "Last Year"
                                com.haphuongquynh.foodmooddiary.presentation.viewmodel.DateRange.ALL_TIME -> "All Time"
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
