package com.haphuongquynh.foodmooddiary.presentation.screens.discovery

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.haphuongquynh.foodmooddiary.domain.model.Meal
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.DiscoveryViewModel
import com.haphuongquynh.foodmooddiary.ui.theme.*

/**
 * Discovery Screen - Explore meal plans from TheMealDB API
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryScreen(
    onNavigateBack: (() -> Unit)? = null,
    viewModel: DiscoveryViewModel = hiltViewModel()
) {
    val currentMeal by viewModel.currentMeal.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedTab by remember { mutableStateOf(1) } // Default to "Khám phá món ăn"
    var selectedCategory by remember { mutableStateOf("Tất cả") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Text(
            text = "Khám phá món ăn",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = BlackPrimary,
            contentColor = PastelGreen
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { 
                    Text(
                        "Gợi ý cho bạn",
                        color = if (selectedTab == 0) PastelGreen else Color.Gray,
                        fontSize = 14.sp
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { 
                    Text(
                        "Khám phá món ăn",
                        color = if (selectedTab == 1) PastelGreen else Color.Gray,
                        fontSize = 14.sp
                    )
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { 
                    Text(
                        "Món đã lưu",
                        color = if (selectedTab == 2) PastelGreen else Color.Gray,
                        fontSize = 14.sp
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PastelGreen)
                }
            }
            error != null -> {
                ErrorMessage(
                    message = error ?: "Unknown error",
                    onRetry = {
                        viewModel.clearError()
                        viewModel.loadRandomMeal()
                    }
                )
            }
            selectedTab == 0 -> {
                // Suggestions for you
                SuggestionsTab()
            }
            selectedTab == 1 -> {
                // Discover Meals
                DiscoverMealsTab(
                    selectedCategory = selectedCategory,
                    onCategoryChange = { category ->
                        selectedCategory = category
                        when(category) {
                            "Tất cả" -> viewModel.loadInitialMeals()
                            "Bò" -> viewModel.filterByCategory("Beef")
                            "Gà" -> viewModel.filterByCategory("Chicken")
                            "Hải sản" -> viewModel.filterByCategory("Seafood")
                        }
                    },
                    searchResults = searchResults,
                    favorites = favorites,
                    onFavoriteClick = { viewModel.toggleFavorite(it) },
                    onMealClick = { meal ->
                        meal.youtubeUrl?.let { url ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    },
                    onRefresh = { viewModel.loadRandomMeal() }
                )
            }
            selectedTab == 2 -> {
                // Saved Meals
                SavedMealsTab(favorites = favorites)
            }
        }
    }
}

@Composable
private fun SuggestionsTab() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Lightbulb,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = PastelGreen
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Gợi ý dựa trên sở thích của bạn",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Thêm môn ăn vào nhật ký để nhận gợi ý",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun DiscoverMealsTab(
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    searchResults: List<Meal>,
    favorites: List<Meal>,
    onFavoriteClick: (Meal) -> Unit,
    onMealClick: (Meal) -> Unit,
    onRefresh: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Title
        item {
            Text(
                text = "Khám phá món ăn mới",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        // Category Filters
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(listOf("Tất cả", "Bò", "Gà", "Hải sản")) { category ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = { onCategoryChange(category) },
                        label = { 
                            Text(
                                category,
                                fontSize = 13.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = BlackSecondary,
                            labelColor = Color.Gray,
                            selectedContainerColor = PastelGreen,
                            selectedLabelColor = Color.White
                        ),
                        border = null
                    )
                }
            }
        }

        // Meal Plans
        val mealsToShow = if (searchResults.isNotEmpty()) searchResults else favorites.take(10)
        
        items(mealsToShow) { meal ->
            MealDiscoveryCard(
                meal = meal,
                onClick = { onMealClick(meal) },
                onFavoriteClick = { onFavoriteClick(meal) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Load more or empty state
        item {
            if (mealsToShow.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Đang tải món ăn...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRefresh,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PastelGreen
                            )
                        ) {
                            Icon(Icons.Default.Refresh, "Refresh")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ẩn khám phá")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MealDiscoveryCard(
    meal: Meal,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = BlackSecondary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Image
            Box {
                AsyncImage(
                    model = meal.thumbUrl,
                    contentDescription = meal.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Overlay badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    color = PastelGreen.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = meal.area,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            // Content
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = meal.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Meal description
                Text(
                    text = "${meal.category} • ${meal.area}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Tags
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = Color(0xFF3C3C3E),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = PastelGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${meal.ingredients.size} ngậy liệu",
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }
                    
                    if (meal.isFavorite) {
                        Surface(
                            color = Color(0xFF3C3C3E),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Favorite",
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedMealsTab(favorites: List<Meal>) {
    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Chưa có món ăn được lưu",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Lưu món ăn yêu thích để xem sau",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(favorites) { meal ->
                MealDiscoveryCard(
                    meal = meal,
                    onClick = { },
                    onFavoriteClick = { }
                )
            }
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFFF5252)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Oops!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(8.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PastelGreen
                )
            ) {
                Icon(Icons.Default.Refresh, "Retry")
                Spacer(Modifier.width(8.dp))
                Text("Thử lại")
            }
        }
    }
}
