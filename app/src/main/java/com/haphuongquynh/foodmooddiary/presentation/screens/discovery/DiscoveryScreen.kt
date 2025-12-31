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
 * Discovery Screen
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
    // (giữ var này để không phá cấu trúc cũ)
    var selectedCategory by remember { mutableStateOf("Tất cả") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackPrimary)
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
                // Suggestions for you (GIỮ NGUYÊN)
                SuggestionsTab()
            }

            selectedTab == 1 -> {
                // Discover Meals (ĐÃ ĐỔI THEO YÊU CẦU)
                DiscoverVietnamMealsTab(
                    onOpenYoutube = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                )
            }

            selectedTab == 2 -> {
                // Saved Meals (GIỮ NGUYÊN)
                SavedMealsTab(favorites = favorites)
            }
        }
    }
}

/* =========================================================
   TAB 1: KHÁM PHÁ MÓN ĂN (ĐỔI THÀNH 20 MÓN THUẦN VIỆT)
   ========================================================= */

private data class VietnamMealItem(
    val name: String,
    val type: VietnamMealType,
    val subType: VietnamCakeSubType? = null,
    val thumbUrl: String,
    val youtubeUrl: String,
)

private enum class VietnamMealType { MON_NUOC, MON_KHO, MON_BANH }

private enum class VietnamCakeSubType { BANH_DAN_GIAN, BANH_VIET_NAM }

@Composable
private fun DiscoverVietnamMealsTab(
    onOpenYoutube: (String) -> Unit
) {
    var selectedMain by remember { mutableStateOf("Tất cả") } // Tất cả / Món nước / Món khô / Món bánh
    var selectedCakeSub by remember { mutableStateOf("Tất cả") } // Tất cả / Bánh dân gian / Bánh Việt Nam

    val vietnamMeals = remember {
        listOf(
            // MÓN NƯỚC
            VietnamMealItem(
                name = "Hủ tiếu Nam Vang",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=c%C3%A1ch+l%C3%A0m+h%E1%BB%A7+ti%E1%BA%BFu+nam+vang"
            ),
            VietnamMealItem(
                name = "Bánh canh cua",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1604908176997-125f25cc500f?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=b%C3%A1nh+canh+cua+s%C3%A0i+g%C3%B2n+ngon"
            ),
            VietnamMealItem(
                name = "Phở bò",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1555126634-323283e090fa?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=c%C3%A1ch+l%C3%A0m+ph%E1%BB%9F+b%C3%B2"
            ),
            VietnamMealItem(
                name = "Bún bò Huế",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1617093727343-374698b1b08d?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=b%C3%BAn+b%C3%B2+hu%E1%BA%BF+c%C3%A1ch+l%C3%A0m"
            ),
            VietnamMealItem(
                name = "Bún riêu",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1604908554027-1b0bff98982f?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=c%C3%A1ch+l%C3%A0m+b%C3%BAn+ri%C3%AAu"
            ),
            VietnamMealItem(
                name = "Mì Quảng",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1526318896980-cf78c088247c?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=m%C3%AC+qu%E1%BA%A3ng+c%C3%A1ch+l%C3%A0m"
            ),
            VietnamMealItem(
                name = "Bún chả",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1550367363-29a61d1a67a4?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=b%C3%BAn+ch%E1%BA%A3+h%C3%A0+n%E1%BB%99i+ngon"
            ),
            VietnamMealItem(
                name = "Bún thịt nướng",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1541542684-4b26f1c8b3b9?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=b%C3%BAn+th%E1%BB%8Bt+n%C6%B0%E1%BB%9Bng+c%C3%A1ch+l%C3%A0m"
            ),

            // MÓN KHÔ
            VietnamMealItem(
                name = "Cơm sườn (Sài Gòn)",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=top+qu%C3%A1n+c%C6%A1m+s%C6%B0%E1%BB%9Dn+s%C3%A0i+g%C3%B2n"
            ),
            VietnamMealItem(
                name = "Cơm tấm bì chả",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1559847844-5315695dadae?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=c%C3%A1ch+l%C3%A0m+c%C6%A1m+t%E1%BA%A5m+s%C6%B0%E1%BB%9Dn+b%C3%AC+ch%E1%BA%A3"
            ),
            VietnamMealItem(
                name = "Bánh mì thịt",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=top+b%C3%A1nh+m%C3%AC+vi%E1%BB%87t+nam+ngon"
            ),
            VietnamMealItem(
                name = "Gỏi cuốn",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=c%C3%A1ch+l%C3%A0m+g%E1%BB%8Fi+cu%E1%BB%91n"
            ),
            VietnamMealItem(
                name = "Bò lúc lắc",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1604908176997-125f25cc500f?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=b%C3%B2+l%C3%BAc+l%E1%BA%AFc+c%C3%A1ch+l%C3%A0m"
            ),
            VietnamMealItem(
                name = "Cá kho tộ",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1559847844-5315695dadae?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=c%C3%A1+kho+t%E1%BB%99+c%C3%A1ch+l%C3%A0m"
            ),
            VietnamMealItem(
                name = "Thịt kho trứng",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=th%E1%BB%8Bt+kho+tr%E1%BB%A9ng+c%C3%A1ch+l%C3%A0m"
            ),
            VietnamMealItem(
                name = "Bún đậu mắm tôm",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1550367363-29a61d1a67a4?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=b%C3%BAn+%C4%91%E1%BA%ADu+m%E1%BA%AFm+t%C3%B4m+ngon"
            ),

            // MÓN BÁNH - BÁNH DÂN GIAN
            VietnamMealItem(
                name = "Bánh da lợn",
                type = VietnamMealType.MON_BANH,
                subType = VietnamCakeSubType.BANH_DAN_GIAN,
                thumbUrl = "https://images.unsplash.com/photo-1541781286675-1f2a65d4a78d?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=c%C3%A1ch+l%C3%A0m+b%C3%A1nh+da+l%E1%BB%A3n"
            ),
            VietnamMealItem(
                name = "Bánh trôi – bánh chay",
                type = VietnamMealType.MON_BANH,
                subType = VietnamCakeSubType.BANH_DAN_GIAN,
                thumbUrl = "https://images.unsplash.com/photo-1551024601-bec78aea704b?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=c%C3%A1ch+l%C3%A0m+b%C3%A1nh+tr%C3%B4i+b%C3%A1nh+chay"
            ),

            // MÓN BÁNH - BÁNH VIỆT NAM
            VietnamMealItem(
                name = "Bánh xèo",
                type = VietnamMealType.MON_BANH,
                subType = VietnamCakeSubType.BANH_VIET_NAM,
                thumbUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=b%C3%A1nh+x%C3%A8o+mi%E1%BB%81n+t%C3%A2y+c%C3%A1ch+l%C3%A0m"
            ),
            VietnamMealItem(
                name = "Bánh bèo",
                type = VietnamMealType.MON_BANH,
                subType = VietnamCakeSubType.BANH_VIET_NAM,
                thumbUrl = "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=b%C3%A1nh+b%C3%A8o+hu%E1%BA%BF+c%C3%A1ch+l%C3%A0m"
            )
        )
    }

    val filteredMeals = remember(selectedMain, selectedCakeSub) {
        when (selectedMain) {
            "Món nước" -> vietnamMeals.filter { it.type == VietnamMealType.MON_NUOC }
            "Món khô" -> vietnamMeals.filter { it.type == VietnamMealType.MON_KHO }
            "Món bánh" -> {
                when (selectedCakeSub) {
                    "Bánh dân gian" -> vietnamMeals.filter {
                        it.type == VietnamMealType.MON_BANH && it.subType == VietnamCakeSubType.BANH_DAN_GIAN
                    }
                    "Bánh Việt Nam" -> vietnamMeals.filter {
                        it.type == VietnamMealType.MON_BANH && it.subType == VietnamCakeSubType.BANH_VIET_NAM
                    }
                    else -> vietnamMeals.filter { it.type == VietnamMealType.MON_BANH }
                }
            }
            else -> vietnamMeals
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        item {
            Text(
                text = "Khám phá món ăn Việt",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        // Main category chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(listOf("Tất cả", "Món nước", "Món khô", "Món bánh")) { category ->
                    FilterChip(
                        selected = category == selectedMain,
                        onClick = {
                            selectedMain = category
                            if (category != "Món bánh") selectedCakeSub = "Tất cả"
                        },
                        label = { Text(category, fontSize = 13.sp) },
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

        // Sub category chips for "Món bánh"
        if (selectedMain == "Món bánh") {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(listOf("Tất cả", "Bánh dân gian", "Bánh Việt Nam")) { sub ->
                        FilterChip(
                            selected = sub == selectedCakeSub,
                            onClick = { selectedCakeSub = sub },
                            label = { Text(sub, fontSize = 13.sp) },
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
        } else {
            item { Spacer(modifier = Modifier.height(4.dp)) }
        }

        items(filteredMeals) { item ->
            VietnamMealCard(
                item = item,
                onClick = { onOpenYoutube(item.youtubeUrl) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            if (filteredMeals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Chưa có món phù hợp bộ lọc",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun VietnamMealCard(
    item: VietnamMealItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = BlackSecondary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = item.thumbUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )

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
                            text = "Vietnamese",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                val typeLabel = when (item.type) {
                    VietnamMealType.MON_NUOC -> "Món nước"
                    VietnamMealType.MON_KHO -> "Món khô"
                    VietnamMealType.MON_BANH -> {
                        when (item.subType) {
                            VietnamCakeSubType.BANH_DAN_GIAN -> "Bánh dân gian"
                            VietnamCakeSubType.BANH_VIET_NAM -> "Bánh Việt Nam"
                            else -> "Món bánh"
                        }
                    }
                }

                Text(
                    text = typeLabel,
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = Color(0xFF3C3C3E),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = PastelGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mở YouTube liên quan",
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/* =========================================================
   2 TAB CÒN LẠI: GIỮ NGUYÊN CODE
   ========================================================= */

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
                                // NOTE: tui giữ nguyên logic cũ, chỉ sửa typo ở text cho đúng tiếng Việt
                                text = "${meal.ingredients.size} nguyên liệu",
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
