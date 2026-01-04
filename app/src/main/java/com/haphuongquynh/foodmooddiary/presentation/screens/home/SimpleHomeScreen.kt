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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.model.MoodType
import com.haphuongquynh.foodmooddiary.presentation.navigation.Screen
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.FoodEntryViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.HomeAIViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.MoodInsightState
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.ColorAnalysisState
import com.haphuongquynh.foodmooddiary.ui.theme.BlackPrimary
import com.haphuongquynh.foodmooddiary.ui.theme.BlackSecondary
import com.haphuongquynh.foodmooddiary.ui.theme.BlackTertiary
import com.haphuongquynh.foodmooddiary.ui.theme.GrayText
import com.haphuongquynh.foodmooddiary.ui.theme.PastelGreen
import com.haphuongquynh.foodmooddiary.ui.theme.PastelGreenLight
import com.haphuongquynh.foodmooddiary.ui.theme.WhiteText
import java.text.SimpleDateFormat
import java.util.*

// Accent colors for mood
private val MoodHappy = Color(0xFF4CAF50)
private val MoodSad = Color(0xFF2196F3)
private val MoodNeutral = Color(0xFFFF9800)
private val AccentPurple = Color(0xFF9C27B0)
private val AccentPink = Color(0xFFE91E63)
private val AccentCyan = Color(0xFF00BCD4)

/**
 * Simple Home Screen without TopBar/FAB (used inside MainScreen)
 */
@Composable
fun SimpleHomeScreen(
    navController: NavController,
    onNavigateToTab: (String) -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: FoodEntryViewModel = hiltViewModel(),
    aiViewModel: HomeAIViewModel = hiltViewModel()
) {
    var selectedView by remember { mutableStateOf(0) } // 0=Grid, 1=List, 2=Calendar
    var quickSelectedMood by remember { mutableStateOf<String?>(null) }
    val entries by viewModel.entries.collectAsState()
    
    // AI States (for color analysis feature)
    val colorAnalysisState by aiViewModel.colorAnalysis.collectAsState()
    
    // Trigger AI analysis on screen load and when entries change
    LaunchedEffect(Unit) {
        aiViewModel.refreshAllAI()
    }
    
    // Calculate stats
    val todayEntries = entries.filter { isToday(it.timestamp) }
    val streak = calculateStreak(entries)
    val dominantMood = getDominantMood(todayEntries)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackPrimary)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Header Greeting + Streak
        GreetingHeader(
            streak = streak,
            onProfileClick = onProfileClick
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 2. Today's Summary
        TodaySummaryCard(
            mealsCount = todayEntries.size,
            dominantMood = dominantMood,
            onClick = { onNavigateToTab("statistics_tab") }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 3. Mood Check-in
        MoodCheckInSection(
            selectedMood = quickSelectedMood,
            onMoodSelected = { mood -> quickSelectedMood = mood },
            onAddEntry = {
                // Navigate with mood if selected
                val route = if (quickSelectedMood != null) {
                    "${Screen.AddEntry.route}?mood=$quickSelectedMood"
                } else {
                    Screen.AddEntry.route
                }
                navController.navigate(route)
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 4. Mood Statistics Widget (restored)
        MoodStatisticsCard(
            entries = entries
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 5. Recent Entries Gallery
        RecentEntriesGallery(
            entries = entries,
            onEntryClick = { entry ->
                navController.navigate(Screen.EntryDetail.createRoute(entry.id))
            }
        )
        
        Spacer(modifier = Modifier.height(100.dp)) // Space for bottom nav
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
private fun GridItemCard(entry: FoodEntry, navController: NavController) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { navController.navigate(Screen.EntryDetail.createRoute(entry.id)) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = BlackSecondary
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
    if (entries.isEmpty()) {
        EmptyState()
        return
    }
    
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
            containerColor = BlackSecondary
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
                    color = WhiteText,
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
                        color = PastelGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "• ${formatDate(entry.timestamp)}",
                        color = GrayText,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarView(entries: List<FoodEntry>, navController: NavController) {
    // Calendar state
    var currentMonth by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var currentYear by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    
    val calendar = remember(currentYear, currentMonth) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }
    
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday
    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
    
    // Group entries by day
    val entriesByDay = entries.groupBy { entry ->
        val cal = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
        if (cal.get(Calendar.YEAR) == currentYear && cal.get(Calendar.MONTH) == currentMonth) {
            cal.get(Calendar.DAY_OF_MONTH)
        } else null
    }.filterKeys { it != null }.mapKeys { it.key!! }
    
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
            IconButton(onClick = {
                if (currentMonth == 0) {
                    currentMonth = 11
                    currentYear--
                } else {
                    currentMonth--
                }
            }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = WhiteText)
            }
            Text(
                text = monthName,
                color = WhiteText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = {
                if (currentMonth == 11) {
                    currentMonth = 0
                    currentYear++
                } else {
                    currentMonth++
                }
            }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = WhiteText)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Day headers
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = GrayText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7
        
        for (row in 0 until rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - firstDayOfWeek + 1
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (dayNumber in 1..daysInMonth) {
                            val dayEntries = entriesByDay[dayNumber] ?: emptyList()
                            val hasEntries = dayEntries.isNotEmpty()
                            val isToday = Calendar.getInstance().let { today ->
                                today.get(Calendar.YEAR) == currentYear &&
                                today.get(Calendar.MONTH) == currentMonth &&
                                today.get(Calendar.DAY_OF_MONTH) == dayNumber
                            }
                            
                            Surface(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clickable(enabled = hasEntries) {
                                        // Navigate to first entry of that day
                                        dayEntries.firstOrNull()?.let { entry ->
                                            navController.navigate(Screen.EntryDetail.createRoute(entry.id))
                                        }
                                    },
                                shape = CircleShape,
                                color = when {
                                    isToday -> PastelGreen.copy(alpha = 0.3f)
                                    hasEntries -> BlackSecondary
                                    else -> Color.Transparent
                                },
                                border = when {
                                    isToday -> BorderStroke(2.dp, PastelGreen)
                                    hasEntries -> BorderStroke(1.dp, PastelGreen.copy(alpha = 0.5f))
                                    else -> null
                                }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = dayNumber.toString(),
                                        color = if (isToday) PastelGreen else WhiteText,
                                        fontSize = 14.sp,
                                        fontWeight = if (isToday || hasEntries) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (hasEntries) {
                                        Text(
                                            text = "${dayEntries.size}",
                                            color = PastelGreen,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(PastelGreen.copy(alpha = 0.3f))
                        .border(1.dp, PastelGreen, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Hôm nay", color = GrayText, fontSize = 11.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(BlackSecondary)
                        .border(1.dp, PastelGreen.copy(alpha = 0.5f), CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Có bài viết", color = GrayText, fontSize = 11.sp)
            }
        }
        
        // Monthly summary
        val monthEntries = entriesByDay.values.flatten()
        if (monthEntries.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = BlackSecondary
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${monthEntries.size}",
                            color = PastelGreen,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Bữa ăn", color = GrayText, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${entriesByDay.size}",
                            color = PastelGreen,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Ngày", color = GrayText, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val avgMeals = monthEntries.size.toFloat() / entriesByDay.size
                        Text(
                            text = String.format("%.1f", avgMeals),
                            color = PastelGreen,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("TB/ngày", color = GrayText, fontSize = 12.sp)
                    }
                }
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Restaurant,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Text(
                "No entries yet",
                color = WhiteText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                "Tap + to add your first meal",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun QuickAccessButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = BlackSecondary,
        border = BorderStroke(1.dp, PastelGreenLight)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = label, tint = PastelGreen)
            Text(label, color = WhiteText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

// Helper functions
private fun getMoodEmoji(entry: FoodEntry): String {
    val moodText = entry.mood ?: ""
    return when {
        moodText.contains("happy", ignoreCase = true) -> "😊"
        moodText.contains("sad", ignoreCase = true) -> "😢"
        moodText.contains("angry", ignoreCase = true) -> "😠"
        moodText.contains("calm", ignoreCase = true) -> "😌"
        moodText.contains("stress", ignoreCase = true) -> "😰"
        moodText.isNotEmpty() -> moodText // Return emoji if already emoji
        else -> "😐"
    }
}

private fun getMoodLabel(entry: FoodEntry): String {
    return entry.mood?.ifEmpty { "No mood" } ?: "No mood"
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// ==================== NEW COMPONENTS ====================

@Composable
private fun GreetingHeader(
    streak: Int,
    onProfileClick: () -> Unit = {}
) {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val (greeting, emoji) = when {
        currentHour < 12 -> "Good morning" to "🌅"
        currentHour < 17 -> "Good afternoon" to "☀️"
        currentHour < 21 -> "Good evening" to "🌆"
        else -> "Night owl mode" to "🌙"
    }
    
    val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$greeting $emoji",
                color = GrayText,
                fontSize = 14.sp
            )
            Text(
                text = "Happy $dayName!",
                color = WhiteText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Streak Badge
            if (streak > 0) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFF5722).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color(0xFFFF5722))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🔥", fontSize = 16.sp)
                        Text(
                            text = "$streak day${if (streak > 1) "s" else ""}",
                            color = Color(0xFFFF5722),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            // Profile Icon
            IconButton(onClick = onProfileClick) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = WhiteText,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun TodaySummaryCard(
    mealsCount: Int,
    dominantMood: String,
    onClick: () -> Unit = {}
) {
    val moodEmoji = when {
        dominantMood.contains("happy", ignoreCase = true) -> "😊"
        dominantMood.contains("sad", ignoreCase = true) -> "😢"
        dominantMood.contains("calm", ignoreCase = true) -> "😌"
        dominantMood.contains("stress", ignoreCase = true) -> "😰"
        else -> "😐"
    }
    
    val moodText = when {
        mealsCount == 0 -> "Start your day!"
        dominantMood.isNotEmpty() -> "Feeling $dominantMood"
        else -> "How are you?"
    }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = BlackSecondary,
        border = BorderStroke(1.dp, PastelGreenLight.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = PastelGreen.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(moodEmoji, fontSize = 24.sp)
                    }
                }
                Column {
                    Text(
                        text = "Today",
                        color = GrayText,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$mealsCount meal${if (mealsCount != 1) "s" else ""} logged",
                        color = WhiteText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PastelGreen.copy(alpha = 0.15f)
            ) {
                Text(
                    text = moodText,
                    color = PastelGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun MoodCheckInSection(
    selectedMood: String?,
    onMoodSelected: (String) -> Unit,
    onAddEntry: () -> Unit
) {
    val moods = listOf(
        "😢" to "Sad",
        "😔" to "Meh",
        "😐" to "Okay",
        "😊" to "Good",
        "🥰" to "Great"
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = BlackSecondary
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "How are you feeling? ✨",
                color = WhiteText,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Mood selection row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                moods.forEach { (emoji, label) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            onMoodSelected(label)
                        }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (selectedMood == label) 
                                PastelGreen.copy(alpha = 0.3f) 
                            else Color.Transparent,
                            border = if (selectedMood == label)
                                BorderStroke(2.dp, PastelGreen)
                            else null
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 28.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Text(
                            text = label,
                            color = if (selectedMood == label) PastelGreen else GrayText,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Add entry button
            Button(
                onClick = onAddEntry,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PastelGreen
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = BlackPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add what you ate",
                    color = BlackPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun QuickInsightCard(
    entries: List<FoodEntry>,
    onClick: () -> Unit = {}
) {
    val insight = generateInsight(entries)
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = AccentPurple.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, AccentPurple.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = AccentPurple.copy(alpha = 0.3f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("💡", fontSize = 20.sp)
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Insight",
                        color = AccentPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "View more",
                        tint = AccentPurple,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = insight,
                    color = WhiteText,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// ==================== MOOD-BASED ANALYSIS COMPONENTS (Local - No API) ====================

@Composable
private fun AIQuickInsightCard(
    insightState: MoodInsightState,
    onRetry: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = AccentPurple.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, AccentPurple.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = AccentPurple.copy(alpha = 0.3f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    when (insightState) {
                        is MoodInsightState.Loading -> CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = AccentPurple,
                            strokeWidth = 2.dp
                        )
                        is MoodInsightState.Success -> Text(
                            insightState.analysis.dominantMood?.emoji ?: "🧠",
                            fontSize = 20.sp
                        )
                        else -> Text("🧠", fontSize = 20.sp)
                    }
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Mood Insight",
                            color = AccentPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AccentPurple.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "Local",
                                color = AccentPurple,
                                fontSize = 8.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "View more",
                        tint = AccentPurple,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                when (insightState) {
                    is MoodInsightState.Loading -> Text(
                        text = "Đang phân tích...",
                        color = GrayText,
                        fontSize = 14.sp
                    )
                    is MoodInsightState.Success -> Text(
                        text = insightState.analysis.insight,
                        color = WhiteText,
                        fontSize = 14.sp
                    )
                    is MoodInsightState.Error -> Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nhấn để thử lại",
                            color = GrayText,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onRetry() }
                        )
                        IconButton(onClick = onRetry, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Refresh, "Retry", tint = AccentPurple)
                        }
                    }
                    else -> Text(
                        text = "Đang tải...",
                        color = GrayText,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MoodStatisticsCard(entries: List<FoodEntry>) {
    // Debug log
    android.util.Log.d("MoodStats", "Total entries received: ${entries.size}")
    entries.take(5).forEach { entry ->
        android.util.Log.d("MoodStats", "Entry: id=${entry.id}, mood=${entry.mood}, moodColor=${entry.moodColor}")
    }
    
    // Get mood distribution from recent entries
    val moodCounts = entries
        .sortedByDescending { it.timestamp }
        .take(50)
        .mapNotNull { entry ->
            val result = MoodType.fromEmoji(entry.mood ?: "") ?: MoodType.fromColorInt(entry.moodColor)
            android.util.Log.d("MoodStats", "Entry ${entry.id}: mood=${entry.mood}, color=${entry.moodColor}, result=$result")
            result
        }
        .groupingBy { it }
        .eachCount()
    
    android.util.Log.d("MoodStats", "MoodCounts: $moodCounts")
    val totalMoods = moodCounts.values.sum()
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = BlackSecondary,
        border = BorderStroke(1.dp, PastelGreen.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PastelGreen.copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🎭", fontSize = 18.sp)
                        }
                    }
                    Column {
                        Text(
                            text = "Thống kê cảm xúc",
                            color = PastelGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (totalMoods > 0) "$totalMoods lần ghi nhận" else "Chưa có dữ liệu",
                            color = GrayText,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mood circles row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MoodType.entries.forEach { mood ->
                    val count = moodCounts[mood] ?: 0
                    MoodStatItem(mood = mood, count = count, total = totalMoods)
                }
            }
            
            // Summary insight
            if (totalMoods > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                
                val dominantMood = moodCounts.maxByOrNull { it.value }?.key
                val dominantCount = moodCounts[dominantMood] ?: 0
                val percentage = if (totalMoods > 0) (dominantCount * 100) / totalMoods else 0
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = PastelGreen.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(dominantMood?.emoji ?: "📊", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cảm xúc chủ đạo: ${dominantMood?.labelVi ?: "N/A"} ($percentage%)",
                            color = WhiteText.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodStatItem(mood: MoodType, count: Int, total: Int) {
    val percentage = if (total > 0) (count * 100) / total else 0
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = mood.color.copy(alpha = if (count > 0) 0.8f else 0.2f),
            modifier = Modifier.size(44.dp),
            border = if (count > 0) BorderStroke(2.dp, mood.color) else null
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(mood.emoji, fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = mood.labelVi,
            color = if (count > 0) WhiteText else GrayText,
            fontSize = 9.sp,
            fontWeight = if (count > 0) FontWeight.Medium else FontWeight.Normal
        )
        Text(
            text = "$count",
            color = if (count > 0) mood.color else GrayText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ComingSoonCard(
    title: String,
    subtitle: String,
    emoji: String,
    accentColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = BlackSecondary,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(emoji, fontSize = 18.sp)
                    }
                }
                Column {
                    Text(
                        text = title,
                        color = accentColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = GrayText,
                        fontSize = 11.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Coming Soon Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = accentColor.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🚀", fontSize = 16.sp)
                    Text(
                        text = "Coming Soon!",
                        color = accentColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Tính năng đang được phát triển",
                color = GrayText,
                fontSize = 12.sp
            )
        }
    }
}

// Keep for future use but not called
@Composable
private fun AIColorPaletteCard(
    colorState: ColorAnalysisState,
    entries: List<FoodEntry>,
    onRetry: () -> Unit
) {
    // Get mood distribution from entries
    val moodColors = entries
        .sortedByDescending { it.timestamp }
        .take(20)
        .mapNotNull { entry ->
            MoodType.fromEmoji(entry.mood ?: "") ?: MoodType.fromColorInt(entry.moodColor)
        }
        .groupingBy { it }
        .eachCount()
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = BlackSecondary,
        border = BorderStroke(1.dp, PastelGreen.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PastelGreen.copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🎭", fontSize = 18.sp)
                        }
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Phân tích cảm xúc",
                                color = PastelGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = PastelGreen.copy(alpha = 0.3f)
                            ) {
                                Text(
                                    text = "Local",
                                    color = PastelGreen,
                                    fontSize = 8.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Cảm xúc khi ăn của bạn",
                            color = GrayText,
                            fontSize = 11.sp
                        )
                    }
                }
                
                if (colorState is ColorAnalysisState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = PastelGreen,
                        strokeWidth = 2.dp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Mood distribution circles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MoodType.entries.forEach { mood ->
                    val count = moodColors[mood] ?: 0
                    MoodCircle(mood = mood, count = count)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Mood Insight
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = PastelGreen.copy(alpha = 0.1f)
            ) {
                when (colorState) {
                    is ColorAnalysisState.Loading -> Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎭", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Đang phân tích cảm xúc...",
                            color = GrayText,
                            fontSize = 12.sp
                        )
                    }
                    is ColorAnalysisState.Success -> Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = colorState.analysis.insight,
                            color = WhiteText.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                        if (colorState.analysis.suggestion.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.Top) {
                                Text("💡", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = colorState.analysis.suggestion,
                                    color = PastelGreen.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                    is ColorAnalysisState.Error -> Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .clickable { onRetry() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚠️", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Nhấn để thử lại",
                            color = GrayText,
                            fontSize = 12.sp
                        )
                    }
                    else -> Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💡", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (moodColors.isEmpty()) "Thêm bữa ăn với cảm xúc để xem phân tích!" else "Đang tải...",
                            color = GrayText,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodCircle(mood: MoodType, count: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = mood.color.copy(alpha = if (count > 0) 0.8f else 0.2f),
            modifier = Modifier.size(40.dp),
            border = if (count > 0) BorderStroke(2.dp, mood.color) else null
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(mood.emoji, fontSize = 18.sp)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$count",
            color = if (count > 0) WhiteText else GrayText,
            fontSize = 10.sp,
            fontWeight = if (count > 0) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ==================== COLOR PALETTE ANALYSIS ====================

@Composable
private fun ColorPaletteAnalysisCard(entries: List<FoodEntry>) {
    // Extract colors from recent entries
    val recentColors = entries
        .sortedByDescending { it.timestamp }
        .take(10)
        .mapNotNull { entry ->
            entry.moodColor?.takeIf { it != 0 }?.let { Color(it) }
        }
        .distinct()
        .take(6)
    
    if (recentColors.isEmpty()) return
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = BlackSecondary,
        border = BorderStroke(1.dp, PastelGreen.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = PastelGreen.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🎨", fontSize = 18.sp)
                    }
                }
                Column {
                    Text(
                        text = "AI Palette Analysis",
                        color = PastelGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Colors extracted from your meals",
                        color = GrayText,
                        fontSize = 11.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Color palette circles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                recentColors.forEach { color ->
                    PaletteColorCircle(color = color)
                }
                // Fill remaining space if less than 6 colors
                repeat(6 - recentColors.size) {
                    EmptyPaletteCircle()
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Mood-color insight
            val colorInsight = generateColorInsight(recentColors)
            Text(
                text = colorInsight,
                color = WhiteText.copy(alpha = 0.8f),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun PaletteColorCircle(color: Color) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
    )
}

@Composable
private fun EmptyPaletteCircle() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(GrayText.copy(alpha = 0.2f))
            .border(1.dp, GrayText.copy(alpha = 0.3f), CircleShape)
    )
}

private fun generateColorInsight(colors: List<Color>): String {
    if (colors.isEmpty()) return "📸 Add photos to your meals for AI color analysis!"
    
    // Analyze colors - categorize by hue
    var redOrange = 0  // 0-60
    var yellowGreen = 0  // 60-150
    var cyan = 0  // 150-210
    var blue = 0  // 210-270
    var purple = 0  // 270-330
    var pink = 0  // 330-360
    
    colors.forEach { color ->
        val hue = colorToHue(color)
        when {
            hue in 0f..60f || hue in 330f..360f -> { if (hue > 330) pink++ else redOrange++ }
            hue in 60f..150f -> yellowGreen++
            hue in 150f..210f -> cyan++
            hue in 210f..270f -> blue++
            hue in 270f..330f -> purple++
        }
    }
    
    val warmColors = redOrange + pink
    val coolColors = cyan + blue + purple
    val earthyColors = yellowGreen
    
    // Calculate saturation average
    val avgSaturation = colors.map { colorToSaturation(it) }.average()
    
    return when {
        earthyColors > colors.size / 2 -> "🥗 Green/yellow tones detected! Your meals are rich in vegetables and natural ingredients."
        redOrange >= 3 -> "🍅 Lots of red/orange foods! These contain lycopene & beta-carotene - great for immunity!"
        warmColors > coolColors * 2 -> "🔥 Warm palette! Tomatoes, peppers, carrots - these foods boost energy & appetite."
        coolColors > warmColors * 2 -> "🫐 Cool tones dominate! Blueberries, leafy greens - excellent for brain health."
        avgSaturation > 0.6 -> "🌈 Vibrant colors! High-saturation foods often indicate fresh, nutrient-rich ingredients."
        avgSaturation < 0.3 -> "🍚 Neutral tones detected. Try adding colorful veggies for more nutrients!"
        colors.size >= 5 -> "✨ Great color variety! Diverse colored foods = diverse nutrients = better health!"
        else -> "⚖️ Balanced palette! Keep eating the rainbow for optimal nutrition."
    }
}

private fun colorToSaturation(color: Color): Float {
    val r = color.red
    val g = color.green
    val b = color.blue
    
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    
    return if (max == 0f) 0f else (max - min) / max
}

private fun colorToHue(color: Color): Float {
    val r = color.red
    val g = color.green
    val b = color.blue
    
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min
    
    if (delta == 0f) return 0f
    
    val hue = when (max) {
        r -> 60f * (((g - b) / delta) % 6)
        g -> 60f * (((b - r) / delta) + 2)
        else -> 60f * (((r - g) / delta) + 4)
    }
    
    return if (hue < 0) hue + 360f else hue
}

// ==================== RECENT ENTRIES GALLERY ====================

@Composable
private fun RecentEntriesGallery(
    entries: List<FoodEntry>,
    onEntryClick: (FoodEntry) -> Unit
) {
    var isGridView by remember { mutableStateOf(true) }
    val recentEntries = entries
        .sortedByDescending { it.timestamp }
        .take(12) // Show more entries in list view
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Header with view toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📸", fontSize = 18.sp)
                Text(
                    text = "Recent Uploads",
                    color = WhiteText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // View toggle buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${entries.size} total",
                    color = GrayText,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Grid view button
                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { isGridView = true },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isGridView) PastelGreen.copy(alpha = 0.3f) else Color.Transparent
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.GridView,
                            contentDescription = "Grid View",
                            tint = if (isGridView) PastelGreen else GrayText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                // List view button
                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { isGridView = false },
                    shape = RoundedCornerShape(8.dp),
                    color = if (!isGridView) PastelGreen.copy(alpha = 0.3f) else Color.Transparent
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.ViewList,
                            contentDescription = "List View",
                            tint = if (!isGridView) PastelGreen else GrayText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (recentEntries.isEmpty()) {
            // Empty state
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BlackSecondary
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🍽️", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No meals uploaded yet",
                        color = GrayText,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Start capturing your food journey!",
                        color = GrayText.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        } else if (isGridView) {
            // Grid view (2 columns)
            val displayEntries = recentEntries.take(6)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                displayEntries.chunked(2).forEach { rowEntries ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowEntries.forEach { entry ->
                            RecentEntryCard(
                                entry = entry,
                                onClick = { onEntryClick(entry) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Fill empty space if odd number
                        if (rowEntries.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            // List view
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recentEntries.forEach { entry ->
                    RecentEntryListItem(
                        entry = entry,
                        onClick = { onEntryClick(entry) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentEntryListItem(
    entry: FoodEntry,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = BlackSecondary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail
            Surface(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(8.dp),
                color = BlackTertiary
            ) {
                if (!entry.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = entry.imageUrl,
                        contentDescription = entry.foodName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🍽️", fontSize = 24.sp)
                    }
                }
            }
            
            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = entry.foodName ?: "Untitled",
                        color = WhiteText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    entry.mood?.let { mood ->
                        if (mood.isNotEmpty()) {
                            Text(mood, fontSize = 16.sp)
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = dateFormat.format(Date(entry.timestamp)),
                        color = GrayText,
                        fontSize = 12.sp
                    )
                    Text("•", color = GrayText, fontSize = 12.sp)
                    Text(
                        text = timeFormat.format(Date(entry.timestamp)),
                        color = GrayText,
                        fontSize = 12.sp
                    )
                }
                if (entry.notes.isNotEmpty()) {
                    Text(
                        text = entry.notes,
                        color = GrayText,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Mood color indicator
            entry.moodColor?.takeIf { it != 0 }?.let { colorInt ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(colorInt))
                )
            }
            
            // Arrow
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = GrayText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun RecentEntryCard(
    entry: FoodEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = BlackSecondary
    ) {
        Box {
            // Image
            if (!entry.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = entry.imageUrl,
                    contentDescription = entry.foodName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                ),
                                startY = 100f
                            )
                        )
                )
            } else {
                // Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BlackTertiary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🍽️", fontSize = 32.sp)
                }
            }
            
            // Mood color indicator
            entry.moodColor?.takeIf { it != 0 }?.let { colorInt ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(colorInt))
                        .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                )
            }
            
            // Info overlay at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = entry.foodName ?: "Untitled",
                    color = WhiteText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = dateFormat.format(Date(entry.timestamp)),
                        color = GrayText,
                        fontSize = 10.sp
                    )
                    Text("•", color = GrayText, fontSize = 10.sp)
                    Text(
                        text = timeFormat.format(Date(entry.timestamp)),
                        color = GrayText,
                        fontSize = 10.sp
                    )
                    entry.mood?.let { mood ->
                        if (mood.isNotEmpty()) {
                            Text("•", color = GrayText, fontSize = 10.sp)
                            Text(
                                text = mood,
                                color = PastelGreen,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==================== HELPER FUNCTIONS ====================

private fun isToday(timestamp: Long): Boolean {
    val today = Calendar.getInstance()
    val date = Calendar.getInstance().apply { timeInMillis = timestamp }
    return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
}

private fun calculateStreak(entries: List<FoodEntry>): Int {
    if (entries.isEmpty()) return 0
    
    val entriesByDay = entries.groupBy { entry ->
        val cal = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
    }
    
    var streak = 0
    val checkDate = Calendar.getInstance()
    
    while (true) {
        val key = "${checkDate.get(Calendar.YEAR)}-${checkDate.get(Calendar.DAY_OF_YEAR)}"
        if (entriesByDay.containsKey(key)) {
            streak++
            checkDate.add(Calendar.DAY_OF_YEAR, -1)
        } else {
            break
        }
    }
    
    return streak
}

private fun getDominantMood(entries: List<FoodEntry>): String {
    if (entries.isEmpty()) return ""
    return entries
        .mapNotNull { it.mood }
        .filter { it.isNotEmpty() }
        .groupingBy { it }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key ?: ""
}

private fun generateInsight(entries: List<FoodEntry>): String {
    if (entries.isEmpty()) {
        return "Start logging meals to get personalized AI insights! 🚀"
    }
    
    // Get this week's entries
    val weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
    val weekEntries = entries.filter { it.timestamp >= weekAgo }
    
    // Today's entries
    val todayEntries = entries.filter { isToday(it.timestamp) }
    
    // Mood analysis
    val happyCount = entries.count {
        it.mood?.contains("happy", ignoreCase = true) == true ||
        it.mood?.contains("good", ignoreCase = true) == true ||
        it.mood?.contains("great", ignoreCase = true) == true
    }
    val sadCount = entries.count {
        it.mood?.contains("sad", ignoreCase = true) == true ||
        it.mood?.contains("meh", ignoreCase = true) == true
    }
    val totalWithMood = entries.count { !it.mood.isNullOrEmpty() }
    
    // Meal patterns
    val avgMealsPerDay = if (weekEntries.isNotEmpty()) {
        val days = weekEntries.groupBy { 
            val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            cal.get(Calendar.DAY_OF_YEAR)
        }.size
        weekEntries.size.toFloat() / days.coerceAtLeast(1)
    } else 0f
    
    // Time-based insights
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    
    return when {
        todayEntries.isEmpty() && currentHour >= 12 -> "🍽️ Haven't logged lunch yet? Capture your meal!"
        todayEntries.isEmpty() && currentHour >= 19 -> "🌙 Don't forget to log your dinner!"
        totalWithMood == 0 -> "🎯 Add moods to unlock AI mood-food correlation analysis!"
        happyCount > totalWithMood * 0.7 -> "🌟 Amazing! 70%+ happy moods! Your food choices are boosting your mood."
        sadCount > totalWithMood * 0.5 -> "💚 Try colorful fruits & veggies - they're linked to better moods!"
        avgMealsPerDay < 2 -> "📊 Logging more meals helps AI give better insights. Aim for 3/day!"
        avgMealsPerDay > 4 -> "🔍 ${avgMealsPerDay.toInt()} meals/day detected. Tracking snacks too? Great detail!"
        entries.size >= 20 && happyCount > sadCount -> "📈 Pattern detected: Your healthy meals correlate with better moods!"
        entries.size < 5 -> "📱 Keep logging! AI needs 5+ entries for personalized insights."
        else -> "⚡ ${weekEntries.size} meals this week! Consistent tracking = better health insights."
    }
}
