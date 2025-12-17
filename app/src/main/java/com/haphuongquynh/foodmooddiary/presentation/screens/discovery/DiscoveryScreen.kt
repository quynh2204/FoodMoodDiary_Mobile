package com.haphuongquynh.foodmooddiary.presentation.screens.discovery

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.haphuongquynh.foodmooddiary.domain.model.Meal
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.DiscoveryViewModel

/**
 * Discovery Screen - Explore meals from TheMealDB API
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryScreen(
    viewModel: DiscoveryViewModel = hiltViewModel()
) {
    val currentMeal by viewModel.currentMeal.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val areas by viewModel.areas.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedArea by remember { mutableStateOf<String?>(null) }
    var showFilters by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover Meals") },
                actions = {
                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(Icons.Default.Search, "Search")
                    }
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.FilterList, "Filters")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0 && currentMeal != null) {
                FloatingActionButton(
                    onClick = { currentMeal?.let { viewModel.toggleFavorite(it) } }
                ) {
                    Icon(
                        imageVector = if (currentMeal?.isFavorite == true) 
                            Icons.Default.Favorite 
                        else 
                            Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle Favorite",
                        tint = if (currentMeal?.isFavorite == true) 
                            Color.Red 
                        else 
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            if (showSearchBar) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { 
                        searchQuery = it
                        if (it.length >= 2) {
                            viewModel.searchMealsByName(it)
                        } else if (it.isEmpty()) {
                            viewModel.clearSearchResults()
                        }
                    },
                    onClose = { 
                        showSearchBar = false
                        searchQuery = ""
                        viewModel.clearSearchResults()
                    }
                )
            }

            // Filters
            if (showFilters) {
                FiltersRow(
                    categories = categories,
                    areas = areas,
                    selectedCategory = selectedCategory,
                    selectedArea = selectedArea,
                    onCategorySelected = { category ->
                        selectedCategory = if (selectedCategory == category) null else category
                        selectedCategory?.let { viewModel.filterByCategory(it) }
                            ?: viewModel.clearSearchResults()
                    },
                    onAreaSelected = { area ->
                        selectedArea = if (selectedArea == area) null else area
                        selectedArea?.let { viewModel.filterByArea(it) }
                            ?: viewModel.clearSearchResults()
                    }
                )
            }

            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { 
                        selectedTab = 0
                        viewModel.clearSearchResults()
                    },
                    text = { Text("Discover") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Favorites (${favorites.size})") }
                )
            }

            // Content
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    ErrorMessage(
                        message = error ?: "Unknown error",
                        onRetry = {
                            viewModel.clearError()
                            if (selectedTab == 0) {
                                viewModel.loadRandomMeal()
                            }
                        }
                    )
                }
                selectedTab == 0 -> {
                    if (searchResults.isNotEmpty()) {
                        SearchResultsList(
                            meals = searchResults,
                            onMealClick = { 
                                viewModel.loadMealById(it.id)
                                selectedTab = 0
                            },
                            onFavoriteClick = { viewModel.toggleFavorite(it) }
                        )
                    } else {
                        DiscoverTab(
                            meal = currentMeal,
                            onRandomClick = { viewModel.loadRandomMeal() },
                            onYouTubeClick = { url ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
                selectedTab == 1 -> {
                    FavoritesTab(
                        favorites = favorites,
                        onMealClick = { 
                            viewModel.loadMealById(it.id)
                            selectedTab = 0
                        },
                        onFavoriteClick = { viewModel.toggleFavorite(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search meals...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, "Close")
            }
        }
    }
}

@Composable
private fun FiltersRow(
    categories: List<String>,
    areas: List<String>,
    selectedCategory: String?,
    selectedArea: String?,
    onCategorySelected: (String) -> Unit,
    onAreaSelected: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        item {
            Text(
                "Categories",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(8.dp)
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.take(5).forEach { category ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = { onCategorySelected(category) },
                        label = { Text(category) }
                    )
                }
            }
        }
        
        item {
            Text(
                "Areas",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(8.dp)
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                areas.take(5).forEach { area ->
                    FilterChip(
                        selected = area == selectedArea,
                        onClick = { onAreaSelected(area) },
                        label = { Text(area) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscoverTab(
    meal: Meal?,
    onRandomClick: () -> Unit,
    onYouTubeClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Button(
                onClick = onRandomClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Refresh, "Random Meal")
                Spacer(Modifier.width(8.dp))
                Text("Get Random Meal")
            }
        }

        meal?.let {
            item {
                MealCard(
                    meal = it,
                    onYouTubeClick = onYouTubeClick
                )
            }
        }
    }
}

@Composable
private fun MealCard(
    meal: Meal,
    onYouTubeClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Image
            AsyncImage(
                model = meal.thumbUrl,
                contentDescription = meal.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Title
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                // Category & Area
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(meal.category) },
                        leadingIcon = { Icon(Icons.Default.Fastfood, null) }
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(meal.area) },
                        leadingIcon = { Icon(Icons.Default.Place, null) }
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Tags
                if (meal.tags.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        meal.tags.forEach { tag ->
                            SuggestionChip(
                                onClick = {},
                                label = { Text(tag, style = MaterialTheme.typography.bodySmall) }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // Ingredients
                Text(
                    "Ingredients",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                meal.ingredients.forEach { (ingredient, measure) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Text("• $ingredient", modifier = Modifier.weight(1f))
                        Text(measure, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Instructions
                Text(
                    "Instructions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    meal.instructions,
                    style = MaterialTheme.typography.bodyMedium
                )

                // YouTube Button
                meal.youtubeUrl?.let { url ->
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { onYouTubeClick(url) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF0000)
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, "Watch on YouTube")
                        Spacer(Modifier.width(8.dp))
                        Text("Watch on YouTube")
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultsList(
    meals: List<Meal>,
    onMealClick: (Meal) -> Unit,
    onFavoriteClick: (Meal) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(meals) { meal ->
            MealListItem(
                meal = meal,
                onClick = { onMealClick(meal) },
                onFavoriteClick = { onFavoriteClick(meal) }
            )
        }
    }
}

@Composable
private fun FavoritesTab(
    favorites: List<Meal>,
    onMealClick: (Meal) -> Unit,
    onFavoriteClick: (Meal) -> Unit
) {
    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "No favorites yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Discover meals and add them to favorites!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favorites) { meal ->
                MealListItem(
                    meal = meal,
                    onClick = { onMealClick(meal) },
                    onFavoriteClick = { onFavoriteClick(meal) }
                )
            }
        }
    }
}

@Composable
private fun MealListItem(
    meal: Meal,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = meal.thumbUrl,
                contentDescription = meal.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${meal.category} • ${meal.area}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (meal.isFavorite) 
                        Icons.Default.Favorite 
                    else 
                        Icons.Default.FavoriteBorder,
                    contentDescription = "Toggle Favorite",
                    tint = if (meal.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
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
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Oops!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Icon(Icons.Default.Refresh, "Retry")
                Spacer(Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}
