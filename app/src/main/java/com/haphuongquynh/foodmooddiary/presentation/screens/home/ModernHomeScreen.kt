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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
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
                    containerColor = Color(0xFF9FD4A8),
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
                                BorderStroke(2.dp, Color(0xFF9FD4A8))
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

                // Quick Access Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickAccessButton(
                        icon = Icons.Default.BarChart,
                        label = "Statistics",
                        onClick = { navController.navigate(Screen.Statistics.route) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessButton(
                        icon = Icons.Default.Map,
                        label = "Map",
                        onClick = { navController.navigate(Screen.Map.route) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessButton(
                        icon = Icons.Default.Search,
                        label = "Discovery",
                        onClick = { navController.navigate(Screen.Discovery.route) },
                        modifier = Modifier.weight(1f)
                    )
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
        return
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(entries) { entry ->
            GridItemCard(entry = entry, navController = navController)
        }
    }
}

@Composable
private fun ModernGridItemCard(
    emoji: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                color = Color(0xFFD9D9D9),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {
        Text(
            text = emoji,
            fontSize = 32.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        )
    }
}

@Composable
private fun GridItemCard(entry: FoodEntry, navController: NavController) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { navController.navigate(Screen.EntryDetail.createRoute(entry.id)) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2E)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Photo or colored background
            if (entry.localPhotoPath != null || entry.photoUrl != null) {
                AsyncImage(
                    model = entry.localPhotoPath ?: entry.photoUrl,
                    contentDescription = entry.foodName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Color box if no photo
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(entry.moodColor))
                )
            }
            
            // Mood emoji badge at bottom right
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(36.dp),
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.5f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        getMoodEmoji(entry),
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ListView(entries: List<FoodEntry>, navController: NavController) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(entries) { entry ->
            ListItemCard(
                entry = entry,
                onClick = { navController.navigate(Screen.EntryDetail.createRoute(entry.id)) }
            )
        }
    }
}

@Composable
private fun ListItemCard(entry: FoodEntry, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2E)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image or colored box
            if (entry.localPhotoPath != null || entry.photoUrl != null) {
                AsyncImage(
                    model = entry.localPhotoPath ?: entry.photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(entry.moodColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        getMoodEmoji(entry),
                        fontSize = 32.sp
                    )
                }
            }
            
            // Text content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = entry.foodName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = getMoodEmoji(entry), fontSize = 18.sp)
                    Text(
                        text = getMoodLabel(entry),
                        color = Color(0xFFFFB800),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "• ${formatDate(entry.timestamp)}",
                        color = Color(0xFF8E8E93),
                        fontSize = 12.sp
                    )
                }
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
                        getMoodEmoji(entry),
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        getMoodLabel(entry),
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
    var selectedDate by remember { mutableStateOf("12/12/2025") }
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Month/Year Header with navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "<",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { /* previous month */ }
                    .padding(8.dp)
            )
            Text(
                text = "December 2025",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = ">",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { /* next month */ }
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Calendar Grid
        MonthCalendarGrid(
            entries = entries,
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LegendItem(Color(0xFF6BCF7F), "calm")
            LegendItem(Color(0xFFFFC857), "happy")
            LegendItem(Color(0xFF8E8E93), "sad")
            LegendItem(Color(0xFFFF6B9D), "stress")
            LegendItem(Color.White, "blank")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Selected date text
        Text(
            text = "Bạn đã chọn ngày $selectedDate",
            color = Color(0xFFFFC857),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Entry cards for selected date
        entries.take(2).forEach { entry ->
            CalendarEntryCard(entry)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun MonthCalendarGrid(
    entries: List<FoodEntry>,
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val daysOfWeek = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    
    // Sample data for December 2025 (starts on Monday)
    val calendarDays = listOf(
        // Week 1
        CalendarDayData(1, Color(0xFF6BCF7F)),
        CalendarDayData(2, Color(0xFFFFC857)),
        CalendarDayData(3, Color(0xFFFFC857)),
        CalendarDayData(4, Color(0xFF8E8E93)),
        CalendarDayData(5, Color(0xFF6BCF7F)),
        CalendarDayData(6, Color(0xFF8E8E93)),
        CalendarDayData(7, Color(0xFFFFC857)),
        // Week 2
        CalendarDayData(8, Color(0xFF6BCF7F)),
        CalendarDayData(9, Color(0xFFFFC857)),
        CalendarDayData(10, Color(0xFFFFC857)),
        CalendarDayData(11, Color(0xFF8E8E93)),
        CalendarDayData(12, Color(0xFF6BCF7F)),
        CalendarDayData(13, Color(0xFF8E8E93)),
        CalendarDayData(14, Color(0xFFFFC857)),
        // Week 3
        CalendarDayData(15, Color(0xFF6BCF7F)),
        CalendarDayData(16, Color(0xFFFFC857)),
        CalendarDayData(17, Color(0xFFFFC857)),
        CalendarDayData(18, Color(0xFF8E8E93)),
        CalendarDayData(19, Color(0xFF6BCF7F)),
        CalendarDayData(20, Color(0xFF8E8E93)),
        CalendarDayData(21, Color(0xFFFFC857)),
        // Week 4
        CalendarDayData(22, Color(0xFF6BCF7F)),
        CalendarDayData(23, Color(0xFFFFC857)),
        CalendarDayData(24, Color(0xFFFFC857)),
        CalendarDayData(25, Color(0xFF8E8E93)),
        CalendarDayData(26, Color(0xFF6BCF7F)),
        CalendarDayData(27, Color(0xFF8E8E93)),
        CalendarDayData(28, Color(0xFFFFC857)),
        // Week 5
        CalendarDayData(29, Color(0xFF6BCF7F)),
        CalendarDayData(30, Color(0xFFFFC857)),
        CalendarDayData(31, Color(0xFFFFC857)),
        CalendarDayData(0, Color.Transparent), // Empty
        CalendarDayData(0, Color(0xFF6BCF7F)),
        CalendarDayData(0, Color.Transparent), // Empty
        CalendarDayData(0, Color.Transparent)  // Empty
    )
    
    Column {
        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Calendar days grid (5 weeks)
        calendarDays.chunked(7).forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                week.forEach { dayData ->
                    CalendarDayCell(
                        dayData = dayData,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (dayData.day > 0) {
                                onDateSelected("${dayData.day}/12/2025")
                            }
                        }
                    )
                }
            }
        }
    }
}

data class CalendarDayData(
    val day: Int,
    val moodColor: Color
)

@Composable
private fun CalendarDayCell(
    dayData: CalendarDayData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                color = if (dayData.day > 0) dayData.moodColor else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = dayData.day > 0) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (dayData.day > 0) {
            Text(
                text = dayData.day.toString(),
                color = if (dayData.moodColor == Color(0xFFFFC857)) Color.Black else Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun CalendarEntryCard(entry: FoodEntry) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF2C2C2E)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Date header
            Text(
                text = "12 Dec 2025",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Entry 1
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🍰", fontSize = 20.sp)
                Text(
                    text = "Bánh kem Giáng Sinh",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(text = "-", color = Color(0xFF8E8E93), fontSize = 14.sp)
                Text(text = "😊", fontSize = 20.sp)
                Text(
                    text = "Happy",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Entry 2
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🍵", fontSize = 20.sp)
                Text(
                    text = "Trà sữa",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(text = "-", color = Color(0xFF8E8E93), fontSize = 14.sp)
                Text(text = "😌", fontSize = 20.sp)
                Text(
                    text = "Calm",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
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
                    "${getMoodEmoji(entry)} ${getMoodLabel(entry)}",
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

private fun getMoodEmoji(entry: FoodEntry): String {
    // Use stored mood emoji if available, otherwise analyze color
    return entry.mood ?: run {
        val red = android.graphics.Color.red(entry.moodColor)
        val green = android.graphics.Color.green(entry.moodColor)
        val blue = android.graphics.Color.blue(entry.moodColor)
        
        // Analyze color to determine mood
        when {
            // Red tones - Stress/Angry
            red > 200 && green < 100 && blue < 100 -> "😫"
            // Orange tones - Happy/Energetic  
            red > 200 && green > 150 && blue < 100 -> "😊"
            // Yellow tones - Happy/Joyful
            red > 200 && green > 200 && blue < 150 -> "😄"
            // Green tones - Calm/Peaceful
            red < 150 && green > 150 && blue < 150 -> "😌"
            // Blue tones - Sad/Melancholy
            red < 100 && green < 150 && blue > 150 -> "😔"
            // Purple tones - Excited/Party
            red > 150 && green < 150 && blue > 150 -> "🎉"
            // Default
            else -> "😊"
        }
    }
}

@Composable
private fun QuickAccessButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF2C2C2E)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun getMoodLabel(entry: FoodEntry): String {
    val red = android.graphics.Color.red(entry.moodColor)
    val green = android.graphics.Color.green(entry.moodColor)
    val blue = android.graphics.Color.blue(entry.moodColor)
    
    // Analyze color to determine mood label
    return when {
        // Red tones - Stress/Angry
        red > 200 && green < 100 && blue < 100 -> "Stress"
        // Orange tones - Happy/Energetic  
        red > 200 && green > 150 && blue < 100 -> "Happy"
        // Yellow tones - Happy/Joyful
        red > 200 && green > 200 && blue < 150 -> "Energetic"
        // Green tones - Calm/Peaceful
        red < 150 && green > 150 && blue < 150 -> "Calm"
        // Blue tones - Sad/Melancholy
        red < 100 && green < 150 && blue > 150 -> "Sad"
        // Purple tones - Excited/Party
        red > 150 && green < 150 && blue > 150 -> "Excited"
        // Default
        else -> "Happy"
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy - h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
