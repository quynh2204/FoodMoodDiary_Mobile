package com.haphuongquynh.foodmooddiary.presentation.screens.discovery

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
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
 * 3 tabs: "Gợi ý cho bạn", "Khám phá món ăn", "Món đã lưu"
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
    val savedMealIds by viewModel.savedVietnamMeals.collectAsStateWithLifecycle()
    val allMeals by viewModel.vietnamMeals.collectAsStateWithLifecycle()

    // Derive saved meals list
    val savedMeals = allMeals.filter { savedMealIds.contains(it.id) }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Gợi ý cho bạn", "Khám phá", "Đã lưu")
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
            contentColor = WhiteText,
            divider = {
                HorizontalDivider(color = CharcoalGray, thickness = 1.dp)
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) PastelGreen else GrayText
                        )
                    }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> RecommendationsTab(
                recommendations = recommendations,
                reasonSummary = recommendationReason,
                savedMealIds = savedMealIds,
                onMealClick = { meal -> openYouTube(context, meal.youtubeUrl) },
                onSaveClick = { meal -> viewModel.toggleSaveVietnamMeal(meal.id) },
                onRefresh = { viewModel.generateRecommendations() }
            )
            1 -> ExploreTab(
                meals = filteredMeals,
                selectedCategory = selectedCategory,
                savedMealIds = savedMealIds,
                onCategorySelected = { viewModel.setVietnamCategory(it) },
                onMealClick = { meal -> openYouTube(context, meal.youtubeUrl) },
                onSaveClick = { meal -> viewModel.toggleSaveVietnamMeal(meal.id) }
            )
            2 -> SavedMealsTab(
                savedMeals = savedMeals,
                onMealClick = { meal -> openYouTube(context, meal.youtubeUrl) },
                onRemoveClick = { meal -> viewModel.toggleSaveVietnamMeal(meal.id) }
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
    savedMealIds: Set<String>,
    onMealClick: (VietnamMeal) -> Unit,
    onSaveClick: (VietnamMeal) -> Unit,
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
                        isSaved = savedMealIds.contains(recommendedMeal.meal.id),
                        onClick = { onMealClick(recommendedMeal.meal) },
                        onSaveClick = { onSaveClick(recommendedMeal.meal) }
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
    savedMealIds: Set<String>,
    onCategorySelected: (String) -> Unit,
    onMealClick: (VietnamMeal) -> Unit,
    onSaveClick: (VietnamMeal) -> Unit
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
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (selectedCategory == category) PastelGreen else CharcoalGray
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
                        isSaved = savedMealIds.contains(meal.id),
                        onClick = { onMealClick(meal) },
                        onSaveClick = { onSaveClick(meal) }
                    )
                }
            }
        }
    }
}

/**
 * Tab 3: Saved Meals - Món đã lưu
 */
@Composable
fun SavedMealsTab(
    savedMeals: List<VietnamMeal>,
    onMealClick: (VietnamMeal) -> Unit,
    onRemoveClick: (VietnamMeal) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (savedMeals.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Bookmark,
                title = "Chưa có món đã lưu",
                subtitle = "Nhấn vào biểu tượng bookmark để lưu món ăn yêu thích"
            )
        } else {
            // Header with count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${savedMeals.size} món đã lưu",
                    fontSize = 14.sp,
                    color = GrayText
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(savedMeals) { meal ->
                    FoodCard(
                        meal = meal,
                        isSaved = true,
                        onClick = { onMealClick(meal) },
                        onSaveClick = { onRemoveClick(meal) }
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
    isSaved: Boolean = false,
    onClick: () -> Unit,
    onSaveClick: () -> Unit = {}
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

            // Save Button (top right)
            IconButton(
                onClick = onSaveClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(36.dp)
                    .background(
                        color = BlackPrimary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (isSaved) "Bỏ lưu" else "Lưu",
                    tint = if (isSaved) PastelGreen else WhiteText,
                    modifier = Modifier.size(20.dp)
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
    isSaved: Boolean = false,
    onClick: () -> Unit,
    onSaveClick: () -> Unit = {}
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

            // Top Row: Save Button + Reason Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Save Button (top left)
                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = BlackPrimary.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isSaved) "Bỏ lưu" else "Lưu",
                        tint = if (isSaved) PastelGreen else WhiteText,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Reason Badge (top right)
                Surface(
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
