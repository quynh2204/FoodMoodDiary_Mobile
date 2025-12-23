package com.haphuongquynh.foodmooddiary.presentation.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.haphuongquynh.foodmooddiary.presentation.navigation.Screen
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthState
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthViewModel
import com.haphuongquynh.foodmooddiary.presentation.screens.camera.CameraScreen
import com.haphuongquynh.foodmooddiary.presentation.screens.statistics.StatisticsScreen
import com.haphuongquynh.foodmooddiary.presentation.screens.map.MapScreen
import com.haphuongquynh.foodmooddiary.presentation.screens.discovery.DiscoveryScreen

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home_tab", Icons.Default.Home, "Home")
    object Statistics : BottomNavItem("statistics_tab", Icons.Default.BarChart, "Statistics")
    object Camera : BottomNavItem("camera_tab", Icons.Default.CameraAlt, "Camera")
    object Map : BottomNavItem("map_tab", Icons.Default.Map, "Map")
    object Discovery : BottomNavItem("discovery_tab", Icons.Default.Restaurant, "Discovery")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(BottomNavItem.Camera.route) }
    val authState by viewModel.authState.collectAsState()
    var showCamera by remember { mutableStateOf(false) }
    val currentUser by viewModel.currentUser.collectAsState()

    // Handle logout
    LaunchedEffect(authState) {
        if (authState is AuthState.LoggedOut) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Main.route) { inclusive = true }
            }
            viewModel.resetAuthState()
        }
    }

    Scaffold(
        topBar = {
            if (!showCamera) {
                TopAppBar(
                    title = { 
                        Text(
                            when(selectedTab) {
                                BottomNavItem.Home.route -> "FoodMoodDiary"
                                BottomNavItem.Statistics.route -> "Statistics"
                                BottomNavItem.Map.route -> "Map"
                                BottomNavItem.Discovery.route -> "Discovery"
                                else -> "FoodMoodDiary"
                            },
                            color = Color.White
                        )
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                            Icon(Icons.Default.Person, "Profile", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1C1C1E),
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1C1C1E),
                contentColor = Color(0xFFFFD700)
            ) {
                val items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Statistics,
                    BottomNavItem.Camera,
                    BottomNavItem.Map,
                    BottomNavItem.Discovery
                )

                items.forEach { item ->
                    if (item == BottomNavItem.Camera) {
                        // Camera button in the middle - larger and circular
                        Spacer(modifier = Modifier.weight(1f))
                        FloatingActionButton(
                            onClick = {
                                selectedTab = item.route
                                showCamera = true
                            },
                            containerColor = Color(0xFFFFD700),
                            contentColor = Color(0xFF1C1C1E),
                            modifier = Modifier
                                .size(56.dp)
                                .offset(y = (-16).dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                            selected = selectedTab == item.route,
                            onClick = {
                                selectedTab = item.route
                                showCamera = false
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFFFD700),
                                selectedTextColor = Color(0xFFFFD700),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color(0xFFFFD700).copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF1C1C1E))
        ) {
            when {
                showCamera && selectedTab == BottomNavItem.Camera.route -> {
                    CameraScreen(
                        onPhotoCaptured = { file, bitmap ->
                            // Navigate to AddEntry with captured photo
                            showCamera = false
                            navController.navigate(Screen.AddEntry.route)
                        },
                        onDismiss = {
                            showCamera = false
                            selectedTab = BottomNavItem.Home.route
                        }
                    )
                }
                selectedTab == BottomNavItem.Home.route -> {
                    com.haphuongquynh.foodmooddiary.presentation.screens.entry.EntryListScreen(
                        navController = navController
                    )
                }
                selectedTab == BottomNavItem.Statistics.route -> {
                    StatisticsScreen()
                }
                selectedTab == BottomNavItem.Map.route -> {
                    MapScreen(
                        onNavigateBack = { selectedTab = BottomNavItem.Home.route }
                    )
                }
                selectedTab == BottomNavItem.Discovery.route -> {
                    DiscoveryScreen(
                        onNavigateBack = { selectedTab = BottomNavItem.Home.route }
                    )
                }
            }
        }
    }
}
