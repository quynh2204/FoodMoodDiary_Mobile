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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.presentation.navigation.Screen
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.FoodEntryViewModel
import com.haphuongquynh.foodmooddiary.ui.theme.BlackPrimary
import com.haphuongquynh.foodmooddiary.ui.theme.BlackSecondary
import com.haphuongquynh.foodmooddiary.ui.theme.BlackTertiary
import com.haphuongquynh.foodmooddiary.ui.theme.GrayText
import com.haphuongquynh.foodmooddiary.ui.theme.PastelGreen
import com.haphuongquynh.foodmooddiary.ui.theme.PastelGreenLight
import com.haphuongquynh.foodmooddiary.ui.theme.WhiteText
import java.text.SimpleDateFormat
import java.util.*

/**
 * Simple Home Screen without TopBar/FAB (used inside MainScreen)
 */
@Composable
fun SimpleHomeScreen(
    navController: NavController,
    viewModel: FoodEntryViewModel = hiltViewModel()
) {
    var selectedView by remember { mutableStateOf(0) } // 0=Grid, 1=List, 2=Calendar
    val entries by viewModel.entries.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackPrimary)
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
                    color = if (selectedView == index) BlackSecondary else BlackTertiary,
                    border = if (selectedView == index)
                        BorderStroke(2.dp, PastelGreenLight)
                        else BorderStroke(1.dp, BlackSecondary)
                ) {
                    Text(
                        label,
                        modifier = Modifier.padding(12.dp),
                        color = WhiteText,
                        fontSize = 14.sp,
                        fontWeight = if (selectedView == index) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Quick Access - ĐÃ SỬA PHẦN NÀY ĐỂ GỌI AI
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessButton(
                icon = Icons.Default.BarChart,
                label = "Statistics",
                onClick = { navController.navigate(Screen.Statistics.route) },
                modifier = Modifier.weight(1f)
            )
            
            // 👇 ĐÂY LÀ NÚT AI MỚI CỦA BẠN
            QuickAccessButton(
                icon = Icons.Default.Face, // Đổi icon thành mặt người
                label = "Trợ lý AI",       // Đổi tên
                onClick = { navController.navigate(Screen.Map.route) }, // Dẫn tới Chat (đã tráo ruột)
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
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Month/Year Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "<",
                color = WhiteText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { /* previous month */ }
                    .padding(8.dp)
            )
            Text(
                text = "December 2025",
                color = WhiteText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = ">",
                color = WhiteText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { /* next month */ }
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Calendar Grid (simplified)
        Text(
            text = "Calendar view - showing ${entries.size} entries",
            color = PastelGreen,
            fontSize = 14.sp,
            modifier = Modifier.padding(16.dp)
        )
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