package com.haphuongquynh.foodmooddiary.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.haphuongquynh.foodmooddiary.presentation.navigation.Screen
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.FoodEntryViewModel
import com.haphuongquynh.foodmooddiary.ui.theme.*

@Composable
fun HomeWithNavigationScreen(
    navController: NavController,
    viewModel: FoodEntryViewModel = hiltViewModel()
) {
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Trang chủ",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(24.dp)
        )
        
        // Quick Navigation Cards
        Text(
            text = "Khám phá",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickNavCard(
                icon = Icons.Default.BarChart,
                title = "Thống kê",
                subtitle = "Xem nhật ký",
                color = PastelGreen,
                onClick = { navController.navigate(Screen.Statistics.route) },
                modifier = Modifier.weight(1f)
            )
            
            QuickNavCard(
                icon = Icons.Default.Map,
                title = "Bản đồ",
                subtitle = "Địa điểm",
                color = GoldPrimary,
                onClick = { navController.navigate(Screen.Map.route) },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickNavCard(
                icon = Icons.Default.Restaurant,
                title = "Món ăn",
                subtitle = "Khám phá",
                color = Color(0xFF90CAF9),
                onClick = { navController.navigate(Screen.Discovery.route) },
                modifier = Modifier.weight(1f)
            )
            
            QuickNavCard(
                icon = Icons.Default.History,
                title = "Lịch sử",
                subtitle = "${entries.size} bài",
                color = ErrorRed,
                onClick = { /* Show history */ },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Recent Entries Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gần đây",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            TextButton(onClick = { /* View all */ }) {
                Text(
                    text = "Xem tất cả",
                    color = PastelGreen,
                    fontSize = 14.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Recent entries list (take 5)
        entries.take(5).forEach { entry ->
            RecentEntryItem(
                entry = entry,
                onClick = {
                    navController.navigate(Screen.EntryDetail.createRoute(entry.id))
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
        
        if (entries.isEmpty()) {
            EmptyStateMessage(
                modifier = Modifier.padding(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun QuickNavCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = BlackSecondary),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhiteText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun RecentEntryItem(
    entry: com.haphuongquynh.foodmooddiary.domain.model.FoodEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = BlackSecondary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mood Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PastelGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (entry.mood) {
                        "Vui vẻ" -> "😊"
                        "Bình thường" -> "😐"
                        "Buồn" -> "😢"
                        "Tuyệt vời" -> "🤩"
                        else -> "😊"
                    },
                    fontSize = 28.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.foodName ?: "Món ăn",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WhiteText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = entry.category.ifEmpty { "Món ăn" },
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun EmptyStateMessage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Restaurant,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Chưa có nhật ký nào",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Chụp ảnh món ăn để bắt đầu",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}
