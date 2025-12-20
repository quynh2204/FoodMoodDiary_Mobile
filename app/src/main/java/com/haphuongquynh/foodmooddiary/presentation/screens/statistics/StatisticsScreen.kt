package com.haphuongquynh.foodmooddiary.presentation.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Charts", "Insights")

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
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back", 
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1C1C1E)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color(0xFF2C2C2E),
                contentColor = Color.White
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { 
                            Text(
                                title, 
                                color = if (selectedTabIndex == index) Color.White else Color(0xFF8E8E93),
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            ) 
                        }
                    )
                }
            }

            // Content based on selected tab
            when (selectedTabIndex) {
                0 -> OverviewTab()
                1 -> ChartsTab()
                2 -> InsightsTab()
            }
        }
    }
}

// Tab 1: Overview - Monthly mood calendar + summary
@Composable
fun OverviewTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Title
        Text(
            text = "Mood Calendar (Monthly)",
            color = Color(0xFFFFC857),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        // Calendar Grid (7x5)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(5) { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(7) { day ->
                        val colors = listOf(
                            Color(0xFF6BCF7F), Color(0xFFFFC857), Color(0xFFFFC857),
                            Color(0xFF8E8E93), Color(0xFF6BCF7F), Color(0xFF8E8E93),
                            Color(0xFFFFC857), Color(0xFFFF6B9D), Color(0xFFFFC857),
                            Color(0xFF6BCF7F), Color(0xFFFFC857), Color(0xFFFFC857),
                            Color(0xFFFF6B9D), Color(0xFF8E8E93), Color(0xFFFFC857),
                            Color(0xFF6BCF7F), Color(0xFF6BCF7F), Color(0xFFFFC857),
                            Color(0xFFFF6B9D), Color(0xFFFFC857), Color(0xFFFFC857),
                            Color(0xFF6BCF7F), Color(0xFF6BCF7F), Color(0xFFFFC857),
                            Color(0xFFFF6B9D), Color(0xFF8E8E93), Color(0xFF8E8E93),
                            Color(0xFFFFFFFF), Color(0xFFFFFFFF), Color(0xFFFFFFFF),
                            Color(0xFFFFFFFF)
                        )
                        val colorIndex = week * 7 + day
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(
                                    color = colors.getOrElse(colorIndex) { Color(0xFF3C3C3E) },
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Today Summary
        Text(
            text = "Today Summary",
            color = Color(0xFFFFC857),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "😊", fontSize = 24.sp)
                Text(
                    text = "Today: Happy",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            
            Text(
                text = "• 2 entries today",
                color = Color.White,
                fontSize = 14.sp
            )
            
            Text(
                text = "• Most common mood this week: Happy",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

// Tab 2: Charts - Line, Bar, Pie charts
@Composable
fun ChartsTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Mood Overtime Chart
        ChartSection(
            title = "Mood Overtime (Week)",
            chartType = "Line Chart"
        )
        
        // Food vs Mood Frequency
        ChartSection(
            title = "Food vs Mood Frequency",
            chartType = "Bar Chart"
        )
        
        // Color Palette Distribution
        ChartSection(
            title = "Color Palette Distribution",
            chartType = "Pie Chart"
        )
    }
}

@Composable
fun ChartSection(title: String, chartType: String) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            color = Color(0xFFFFC857),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    color = Color(0xFF2C2C2E),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = chartType,
                color = Color(0xFF8E8E93),
                fontSize = 16.sp
            )
        }
    }
}

// Tab 3: Insights - AI generated insights
@Composable
fun InsightsTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Insights generated from your meals",
            color = Color(0xFFFFC857),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        
        // Insight items
        val insights = listOf(
            "💡 Bạn thường ăn đồ ngọt khi buồn." to "\"70% mood SAD entries liên quan đến bánh, trà sữa hoặc món tráng miệng.\"",
            "💡 Mood Happy xuất hiện nhiều nhất vào buổi trưa." to "\"Giữa 11AM-1PM là khoảng thời gian bạn ghi nhận nhiều cảm xúc tích cực nhất.\"",
            "💡 Món có màu đỏ thường đi kèm mood Happy hoặc Energy." to "\"Ảnh có tone đỏ xuất hiện rất nhiều trong các entry vui vẻ.\"",
            "💡 Bạn ăn tối hơi muộn so với mức trung bình." to "\"Hơn 60% bữa Dinner được ghi sau 8:30 PM.\"",
            "💡 Color palette của bạn thiên về tone ấm." to "\"Điều này liên quan tới mood Happy và Energy cao hơn.\""
        )
        
        insights.forEach { (title, description) ->
            InsightItem(title = title, description = description)
        }
    }
}

@Composable
fun InsightItem(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2E)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = description,
                color = Color(0xFFAAAAAA),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}
