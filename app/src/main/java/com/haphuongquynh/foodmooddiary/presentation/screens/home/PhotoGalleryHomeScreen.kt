package com.haphuongquynh.foodmooddiary.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.presentation.navigation.Screen
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.FoodEntryViewModel
import com.haphuongquynh.foodmooddiary.ui.theme.*

enum class ViewMode {
    GRID, LIST
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryHomeScreen(
    navController: NavController,
    viewModel: FoodEntryViewModel = hiltViewModel()
) {
    val entries by viewModel.entries.collectAsState()
    var viewMode by remember { mutableStateOf(ViewMode.GRID) }

    Scaffold(
        containerColor = BlackPrimary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Trang chủ",
                        color = WhiteText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Toggle Grid/List View
                    IconButton(onClick = {
                        viewMode = if (viewMode == ViewMode.GRID) ViewMode.LIST else ViewMode.GRID
                    }) {
                        Icon(
                            imageVector = if (viewMode == ViewMode.GRID) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Toggle View",
                            tint = PastelGreen
                        )
                    }
                    
                    // Profile Icon
                    IconButton(onClick = {
                        navController.navigate(Screen.Profile.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = PastelGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkGray
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BlackPrimary),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Navigation Cards Section
            item {
                Text(
                    text = "Điều hướng",
                    color = WhiteText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp)
                ) {
                    item {
                        NavCard(
                            title = "Thống kê",
                            subtitle = "Xem phân tích",
                            icon = Icons.Default.BarChart,
                            color = PastelGreen,
                            modifier = Modifier.width(160.dp),
                            onClick = { navController.navigate(Screen.Statistics.route) }
                        )
                    }
                    item {
                        NavCard(
                            title = "Bản đồ",
                            subtitle = "Địa điểm",
                            icon = Icons.Default.Map,
                            color = GoldPrimary,
                            modifier = Modifier.width(160.dp),
                            onClick = { navController.navigate(Screen.Map.route) }
                        )
                    }
                    item {
                        NavCard(
                            title = "Khám phá",
                            subtitle = "Món ăn mới",
                            icon = Icons.Default.Search,
                            color = Color(0xFF4A9FFF),
                            modifier = Modifier.width(160.dp),
                            onClick = { navController.navigate(Screen.Discovery.route) }
                        )
                    }
                }
            }
            
            // Photos Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ảnh của bạn",
                        color = WhiteText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${entries.size} ảnh",
                        color = GrayText,
                        fontSize = 14.sp
                    )
                }
            }
            
            // Photos Grid/List
            when (viewMode) {
                ViewMode.GRID -> {
                    items(entries.chunked(3)) { rowEntries ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            rowEntries.forEach { entry ->
                                Box(modifier = Modifier.weight(1f)) {
                                    GridPhotoItem(entry = entry, navController = navController)
                                }
                            }
                            // Fill remaining slots
                            repeat(3 - rowEntries.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                ViewMode.LIST -> {
                    items(entries) { entry ->
                        ListPhotoItem(entry = entry, navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun NavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = title,
                    color = WhiteText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = GrayText,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun PhotoGridView(
    entries: List<FoodEntry>,
    navController: NavController
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(entries) { entry ->
            GridPhotoItem(entry = entry, navController = navController)
        }
    }
}

@Composable
fun GridPhotoItem(
    entry: FoodEntry,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(DarkGray)
            .clickable {
                navController.navigate("${Screen.EntryDetail.route}/${entry.id}")
            }
    ) {
        // Photo
        if (!entry.photoUrl.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(entry.photoUrl),
                contentDescription = entry.foodName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else if (!entry.localPhotoPath.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(entry.localPhotoPath),
                contentDescription = entry.foodName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Mood indicator overlay at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Color.Black.copy(alpha = 0.5f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = getMoodEmojiByString(entry.mood ?: "Bình thường"),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun PhotoListView(
    entries: List<FoodEntry>,
    navController: NavController
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(entries) { entry ->
            ListPhotoItem(entry = entry, navController = navController)
        }
    }
}

@Composable
fun ListPhotoItem(
    entry: FoodEntry,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable {
                navController.navigate("${Screen.EntryDetail.route}/${entry.id}")
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Photo
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BlackPrimary)
            ) {
                if (!entry.photoUrl.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(entry.photoUrl),
                        contentDescription = entry.foodName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (!entry.localPhotoPath.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(entry.localPhotoPath),
                        contentDescription = entry.foodName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = entry.foodName,
                        color = WhiteText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    
                    if (entry.category.isNotEmpty()) {
                        Text(
                            text = entry.category,
                            color = GrayText,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mood
                    Text(
                        text = getMoodEmojiByString(entry.mood ?: "Bình thường"),
                        fontSize = 18.sp
                    )
                    
                    if (!entry.mood.isNullOrEmpty()) {
                        Text(
                            text = entry.mood,
                            color = PastelGreen,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Calories if available
                    if (entry.calories > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Calories",
                                tint = ErrorRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${entry.calories}",
                                color = GrayText,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun getMoodEmojiByString(mood: String): String {
    return when (mood) {
        "Vui vẻ" -> "😊"
        "Bình thường" -> "😐"
        "Buồn" -> "😢"
        "Tuyệt vời" -> "🤩"
        "Hạnh phúc" -> "😄"
        "Căng thẳng" -> "😰"
        "Tức giận" -> "😠"
        else -> "😐"
    }
}
