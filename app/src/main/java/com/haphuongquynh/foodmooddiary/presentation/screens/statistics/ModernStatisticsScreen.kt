package com.haphuongquynh.foodmooddiary.presentation.screens.statistics

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.StatisticsViewModel

/**
 * Modern Statistics Screen with Dark Theme
 * Overview tab with summary cards + Charts/Insights tabs
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernStatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val moodTrend by viewModel.moodTrend.collectAsStateWithLifecycle()
    val topFoods by viewModel.topFoods.collectAsStateWithLifecycle()
    val mealDistribution by viewModel.mealDistribution.collectAsStateWithLifecycle()
    val colorDistribution by viewModel.colorDistribution.collectAsStateWithLifecycle()
    val insights by viewModel.insights.collectAsStateWithLifecycle()
    val weeklySummary by viewModel.weeklySummary.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1C1C1E)
    ) {
        Scaffold(
            containerColor = Color(0xFF1C1C1E),
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "Statistics",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showDateRangePicker = true }) {
                            Icon(Icons.Default.DateRange, "Date Range", tint = Color.White)
                        }
                        IconButton(onClick = { /* Export CSV */ }) {
                            Icon(Icons.Default.FileDownload, "Export", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1C1C1E)
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Tab Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Overview", "Charts", "Insights").forEachIndexed { index, title ->
                        TabButton(
                            text = title,
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Tab Content
                when (selectedTab) {
                    0 -> OverviewTab(
                        weeklySummary = weeklySummary,
                        modifier = Modifier.fillMaxSize()
                    )
                    1 -> ModernChartsTab(
                        moodTrend = moodTrend,
                        topFoods = topFoods,
                        mealDistribution = mealDistribution,
                        colorDistribution = colorDistribution,
                        modifier = Modifier.fillMaxSize()
                    )
                    2 -> ModernInsightsTab(
                        insights = insights,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Date Range Picker Dialog
            if (showDateRangePicker) {
                ModernDateRangeDialog(
                    onDismiss = { showDateRangePicker = false },
                    onSelectRange = { range ->
                        viewModel.setDateRange(range)
                        showDateRangePicker = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) Color(0xFF3C3C3E) else Color(0xFF2C2C2E),
        border = if (selected) BorderStroke(2.dp, Color(0xFFFFD700)) else null
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun OverviewTab(
    weeklySummary: com.haphuongquynh.foodmooddiary.domain.model.WeeklySummary?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Period Title
        Text(
            "This Week",
            color = Color(0xFFFFD700),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        weeklySummary?.let { summary ->
            // Summary Cards Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = "📊",
                    value = summary.totalEntries.toString(),
                    label = "Entries",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "😊",
                    value = String.format("%.1f", summary.averageMoodScore),
                    label = "Avg Mood",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = "🔥",
                    value = "${summary.streak}",
                    label = "Days Streak",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "⭐",
                    value = summary.mostFrequentFood?.take(6) ?: "N/A",
                    label = "Top Food",
                    modifier = Modifier.weight(1f)
                )
            }

            // Quick Insights
            Text(
                "Quick Insights",
                color = Color(0xFFFFD700),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )

            InsightCard(
                emoji = "📈",
                text = "You've logged ${summary.totalEntries} meals this week"
            )
            
            InsightCard(
                emoji = "🎯",
                text = "Your streak is ${summary.streak} days! Keep it up!"
            )
            
            if (!summary.mostFrequentFood.isNullOrBlank()) {
                InsightCard(
                    emoji = "🍽️",
                    text = "Your favorite food this week: ${summary.mostFrequentFood}"
                )
            }
        } ?: run {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.BarChart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Text(
                        "No data yet",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                    Text(
                        "Start logging meals to see statistics",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF2C2C2E)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                icon,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                label,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun InsightCard(
    emoji: String,
    text: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF2C2C2E)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3C3C3E)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 20.sp)
            }
            Text(
                text,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ModernChartsTab(
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
        ModernChartSection(title = "Mood Trend") {
            MoodTrendChart(data = moodTrend)
        }

        // Top Foods Chart
        ModernChartSection(title = "Top Foods by Frequency") {
            TopFoodsChart(data = topFoods)
        }

        // Meal Distribution Chart
        ModernChartSection(title = "Meal Type Distribution") {
            MealDistributionChart(data = mealDistribution)
        }

        // Color Distribution Chart
        ModernChartSection(title = "Color Palette Distribution") {
            ColorDistributionChart(data = colorDistribution)
        }
    }
}

@Composable
private fun ModernChartSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            color = Color(0xFFFFD700),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Surface(
            color = Color(0xFF2C2C2E),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun ModernInsightsTab(
    insights: List<com.haphuongquynh.foodmooddiary.domain.model.Insight>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "AI Insights",
            color = Color(0xFFFFD700),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        if (insights.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Psychology,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Text(
                        "No insights yet",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                    Text(
                        "Log more meals to generate insights",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            insights.forEach { insight ->
                ModernInsightCard(insight = insight)
            }
        }
    }
}

@Composable
private fun ModernInsightCard(
    insight: com.haphuongquynh.foodmooddiary.domain.model.Insight
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF2C2C2E)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when (insight.type) {
                                com.haphuongquynh.foodmooddiary.domain.model.InsightType.MOOD_PATTERN -> Color(0xFF4CAF50)
                                com.haphuongquynh.foodmooddiary.domain.model.InsightType.FOOD_CORRELATION -> Color(0xFFFF9800)
                                com.haphuongquynh.foodmooddiary.domain.model.InsightType.TIME_PATTERN -> Color(0xFF2196F3)
                                com.haphuongquynh.foodmooddiary.domain.model.InsightType.STREAK -> Color(0xFFFF5722)
                                com.haphuongquynh.foodmooddiary.domain.model.InsightType.COLOR_PATTERN -> Color(0xFFE91E63)
                                com.haphuongquynh.foodmooddiary.domain.model.InsightType.RECOMMENDATION -> Color(0xFF9C27B0)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (insight.type) {
                            com.haphuongquynh.foodmooddiary.domain.model.InsightType.MOOD_PATTERN -> Icons.Default.Psychology
                            com.haphuongquynh.foodmooddiary.domain.model.InsightType.FOOD_CORRELATION -> Icons.Default.Restaurant
                            com.haphuongquynh.foodmooddiary.domain.model.InsightType.TIME_PATTERN -> Icons.Default.Schedule
                            com.haphuongquynh.foodmooddiary.domain.model.InsightType.STREAK -> Icons.Default.LocalFireDepartment
                            com.haphuongquynh.foodmooddiary.domain.model.InsightType.COLOR_PATTERN -> Icons.Default.Palette
                            com.haphuongquynh.foodmooddiary.domain.model.InsightType.RECOMMENDATION -> Icons.Default.Lightbulb
                        },
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }
                Text(
                    when (insight.type) {
                        com.haphuongquynh.foodmooddiary.domain.model.InsightType.MOOD_PATTERN -> "Mood Pattern"
                        com.haphuongquynh.foodmooddiary.domain.model.InsightType.FOOD_CORRELATION -> "Food & Mood"
                        com.haphuongquynh.foodmooddiary.domain.model.InsightType.TIME_PATTERN -> "Time Pattern"
                        com.haphuongquynh.foodmooddiary.domain.model.InsightType.STREAK -> "Streak"
                        com.haphuongquynh.foodmooddiary.domain.model.InsightType.COLOR_PATTERN -> "Color Pattern"
                        com.haphuongquynh.foodmooddiary.domain.model.InsightType.RECOMMENDATION -> "Recommendation"
                    },
                    color = Color(0xFFFFD700),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                insight.description,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ModernDateRangeDialog(
    onDismiss: () -> Unit,
    onSelectRange: (com.haphuongquynh.foodmooddiary.presentation.viewmodel.DateRange) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF2C2C2E),
        title = { 
            Text(
                "Select Date Range",
                color = Color.White,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                com.haphuongquynh.foodmooddiary.presentation.viewmodel.DateRange.entries.forEach { range ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectRange(range) },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF3C3C3E)
                    ) {
                        Text(
                            text = when (range) {
                                com.haphuongquynh.foodmooddiary.presentation.viewmodel.DateRange.LAST_7_DAYS -> "Last 7 Days"
                                com.haphuongquynh.foodmooddiary.presentation.viewmodel.DateRange.LAST_30_DAYS -> "Last 30 Days"
                                com.haphuongquynh.foodmooddiary.presentation.viewmodel.DateRange.LAST_90_DAYS -> "Last 90 Days"
                                com.haphuongquynh.foodmooddiary.presentation.viewmodel.DateRange.LAST_YEAR -> "Last Year"
                                com.haphuongquynh.foodmooddiary.presentation.viewmodel.DateRange.ALL_TIME -> "All Time"
                            },
                            modifier = Modifier.padding(16.dp),
                            color = Color.White
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFFFFD700))
            }
        }
    )
}
