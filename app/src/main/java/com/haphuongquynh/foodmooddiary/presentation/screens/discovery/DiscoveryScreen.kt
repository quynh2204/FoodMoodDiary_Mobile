package com.haphuongquynh.foodmooddiary.presentation.screens.discovery

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.DiscoveryViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.DiscoveryViewModel.RecommendedMeal
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.DiscoveryViewModel.VietnamMeal
import com.haphuongquynh.foodmooddiary.ui.theme.*

/**
 * Discovery Screen - Khám phá món ăn Việt Nam
 * 2 tabs: "Gợi ý cho bạn" và "Khám phá món ăn"
 */
@Composable
fun DiscoveryScreen(
    viewModel: DiscoveryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val filteredMeals by viewModel.filteredVietnamMeals.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedMainCategory.collectAsStateWithLifecycle()
    val recommendations by viewModel.recommendations.collectAsStateWithLifecycle()
    val recommendationReason by viewModel.recommendationReason.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Gợi ý cho bạn", "Khám phá món ăn")
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackPrimary)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Khám phá",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = WhiteText
            )
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = BlackSecondary,
            contentColor = PastelGreen,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PastelGreen
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    selectedContentColor = PastelGreen,
                    unselectedContentColor = GrayText
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> RecommendationsTab(
                recommendations = recommendations,
                reasonSummary = recommendationReason,
                onMealClick = { meal -> openYouTube(context, meal.youtubeUrl) },
                onRefresh = { viewModel.generateRecommendations() }
            )
            1 -> ExploreTab(
                meals = filteredMeals,
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.setVietnamCategory(it) },
                onMealClick = { meal -> openYouTube(context, meal.youtubeUrl) }
            )
        }
    }
}

/**
 * Tab 1: Recommendations - Gợi ý dựa trên lịch sử ăn uống
 */
@Composable
fun RecommendationsTab(
    recommendations: List<RecommendedMeal>,
    reasonSummary: String,
    onMealClick: (VietnamMeal) -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Reason Card
        if (reasonSummary.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = BlackSecondary
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = null,
                        tint = PastelGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Gợi ý hôm nay",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhiteText
                        )
                        Text(
                            text = reasonSummary,
                            fontSize = 13.sp,
                            color = GrayText
                        )
                    }
                    TextButton(onClick = onRefresh) {
                        Text("Làm mới", color = PastelGreen, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recommendations Grid
        if (recommendations.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Restaurant,
                title = "Chưa có gợi ý",
                subtitle = "Hãy thêm nhật ký ăn uống để nhận gợi ý phù hợp"
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recommendations) { recommendedMeal ->
                    RecommendedFoodCard(
                        recommendedMeal = recommendedMeal,
                        onClick = { onMealClick(recommendedMeal.meal) }
                    )
                }
            }
        }
    }
}

/**
 * Tab 2: Explore - Khám phá tất cả món ăn
 */
@Composable
fun ExploreTab(
    meals: List<VietnamMeal>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onMealClick: (VietnamMeal) -> Unit
) {
    val categories = listOf("Tất cả", "Món nước", "Món khô", "Tráng miệng")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Category Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = {
                        Text(
                            text = category,
                            fontSize = 13.sp,
                            fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PastelGreen,
                        selectedLabelColor = BlackPrimary,
                        containerColor = BlackSecondary,
                        labelColor = WhiteText
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = CharcoalGray,
                        selectedBorderColor = PastelGreen,
                        enabled = true,
                        selected = selectedCategory == category
                    )
                )
            }
        }

        // Food Grid
        if (meals.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Restaurant,
                title = "Không có món ăn",
                subtitle = "Thử chọn danh mục khác"
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(meals) { meal ->
                    FoodCard(
                        meal = meal,
                        onClick = { onMealClick(meal) }
                    )
                }
            }
        }
    }
}

/**
 * Food Card - Hiển thị món ăn trong grid
 */
@Composable
fun FoodCard(
    meal: VietnamMeal,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = BlackSecondary
    ) {
        Box {
            // Food Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(meal.thumbUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = meal.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 100f
                        )
                    )
            )

            // Play Button
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Xem video",
                tint = WhiteText.copy(alpha = 0.9f),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
                    .background(
                        color = BlackPrimary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(8.dp)
            )

            // Food Name
            Text(
                text = meal.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = WhiteText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )
        }
    }
}

/**
 * Recommended Food Card - Card lớn hơn với lý do gợi ý
 */
@Composable
fun RecommendedFoodCard(
    recommendedMeal: RecommendedMeal,
    onClick: () -> Unit
) {
    val meal = recommendedMeal.meal

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = BlackSecondary
    ) {
        Box {
            // Food Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(meal.thumbUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = meal.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            ),
                            startY = 80f
                        )
                    )
            )

            // Reason Badge
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                color = PastelGreen.copy(alpha = 0.9f)
            ) {
                Text(
                    text = recommendedMeal.reason,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = BlackPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Play Button
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Xem video",
                tint = WhiteText.copy(alpha = 0.9f),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
                    .background(
                        color = BlackPrimary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(8.dp)
            )

            // Food Name
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = meal.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhiteText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = meal.type.displayName,
                    fontSize = 11.sp,
                    color = PastelGreen
                )
            }
        }
    }
}

/**
 * Empty State Component
 */
@Composable
fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PastelGreen.copy(alpha = 0.6f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = WhiteText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = GrayText,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Open YouTube video - Try YouTube app first, fallback to browser
 */
fun openYouTube(context: Context, youtubeUrl: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))

    // Try YouTube app first
    intent.setPackage("com.google.android.youtube")

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // YouTube app not installed, open in browser
        intent.setPackage(null)
        context.startActivity(intent)
    }
}
