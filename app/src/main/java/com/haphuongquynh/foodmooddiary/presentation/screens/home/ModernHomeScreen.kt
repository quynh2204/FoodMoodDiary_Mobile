package com.haphuongquynh.foodmooddiary.presentation.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.presentation.navigation.Screen
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.FoodEntryViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Modern Home Screen with Calendar, Grid, and List views
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernHomeScreen(
    navController: NavController,
    viewModel: FoodEntryViewModel = hiltViewModel()
) {
    var selectedView by remember { mutableStateOf(0) } // 0=Grid, 1=List, 2=Calendar
    val entries by viewModel.entries.collectAsState()
    
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
                            "FoodMoodDiary",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ) 
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                            Icon(Icons.Default.Settings, "Settings", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1C1C1E)
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddEntry.route) },
                    containerColor = Color(0xFFFFD700),
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Default.Add, "Add Entry")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // View Mode Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    listOf("Grid", "List", "Calendar").forEachIndexed { index, label ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .clickable { selectedView = index },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedView == index) Color(0xFF3C3C3E)
                                   else Color(0xFF2C2C2E),
                            border = if (selectedView == index) 
                                BorderStroke(2.dp, Color(0xFFFFD700))
                                else null
                        ) {
                            Text(
                                label,
                                modifier = Modifier.padding(12.dp),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = if (selectedView == index) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Content based on selected view
                when (selectedView) {
                    0 -> GridView(entries, navController)
                    1 -> ListView(entries, navController)
                    2 -> CalendarView(entries, navController)
                }
            }
        }
    }
}

@Composable
private fun GridView(entries: List<FoodEntry>, navController: NavController) {
    if (entries.isEmpty()) {
        EmptyState()
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(entries) { entry ->
                GridItemCard(entry, navController)
            }
        }
    }
}

@Composable
private fun GridItemCard(entry: FoodEntry, navController: NavController) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { /* navigate to detail */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2E)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Mood emoji badge
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(36.dp),
                shape = CircleShape,
                color = Color(0xFF3C3C3E)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        getMoodEmoji(entry.moodColor),
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ListView(entries: List<FoodEntry>, navController: NavController) {
    if (entries.isEmpty()) {
        EmptyState()
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(entries) { entry ->
                ListItemCard(entry, navController)
            }
        }
    }
}

@Composable
private fun ListItemCard(entry: FoodEntry, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* navigate to detail */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2E)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // No image - just text
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    entry.foodName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        getMoodEmoji(entry.moodColor),
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        getMoodLabel(entry.moodColor),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "• ${formatDate(entry.timestamp)}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarView(entries: List<FoodEntry>, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Month/Year Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* previous month */ }) {
                Icon(Icons.Default.ChevronLeft, "Previous", tint = Color.White)
            }
            Text(
                "December 2025",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { /* next month */ }) {
                Icon(Icons.Default.ChevronRight, "Next", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar Grid
        MonthCalendarGrid(entries)

        Spacer(modifier = Modifier.height(16.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(Color(0xFF4CAF50), "calm")
            LegendItem(Color(0xFFFFD700), "happy")
            LegendItem(Color.Gray, "sad")
            LegendItem(Color(0xFFFF5252), "stress")
            LegendItem(Color.White, "blank")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected date entries
        Text(
            "Bạn đã chọn ngày 12/12/2025",
            color = Color(0xFFFFD700),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(entries.take(3)) { entry ->
                DayEntryCard(entry)
            }
        }
    }
}

@Composable
private fun MonthCalendarGrid(entries: List<FoodEntry>) {
    val daysOfWeek = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    
    Column {
        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    day,
                    modifier = Modifier.weight(1f),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar days (5 weeks)
        repeat(5) { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(7) { day ->
                    CalendarDay(
                        color = when ((week * 7 + day) % 4) {
                            0 -> Color(0xFF4CAF50)
                            1 -> Color(0xFFFFD700)
                            2 -> Color.Gray
                            else -> Color(0xFFFF5252)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(color: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(color, RoundedCornerShape(4.dp))
    )
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            label,
            color = Color.White,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun DayEntryCard(entry: FoodEntry) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF2C2C2E)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "🍰",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    entry.foodName,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${getMoodEmoji(entry.moodColor)} ${getMoodLabel(entry.moodColor)}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.RestaurantMenu,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No entries yet",
                color = Color.Gray,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Tap + to add your first meal",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

private fun getMoodEmoji(color: Int): String {
    return when {
        color == android.graphics.Color.parseColor("#4CAF50") -> "😊"
        color == android.graphics.Color.parseColor("#FFD700") -> "😌"
        else -> "😊"
    }
}

private fun getMoodLabel(color: Int): String {
    return when {
        color == android.graphics.Color.parseColor("#4CAF50") -> "Happy"
        color == android.graphics.Color.parseColor("#FFD700") -> "Calm"
        else -> "Happy"
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy - h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
