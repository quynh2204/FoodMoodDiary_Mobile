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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.haphuongquynh.foodmooddiary.ui.theme.WhiteText
import com.haphuongquynh.foodmooddiary.ui.theme.*
import com.haphuongquynh.foodmooddiary.presentation.navigation.Screen
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthState
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthViewModel
import com.haphuongquynh.foodmooddiary.presentation.screens.camera.CameraScreen
// Import màn hình Chat
import com.haphuongquynh.foodmooddiary.presentation.screens.ChatScreen

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home_tab", Icons.Default.Home, "Trang chủ")
    object Statistics : BottomNavItem("statistics_tab", Icons.Default.CalendarToday, "Nhật ký")
    object Camera : BottomNavItem("camera_tab", Icons.Default.CameraAlt, "Camera")
    object AI : BottomNavItem("ai_tab", Icons.Default.Face, "Trợ lý AI")
    object Discovery : BottomNavItem("discovery_tab", Icons.Default.Explore, "Khám phá")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(BottomNavItem.Home.route) }
    val authState by viewModel.authState.collectAsState()
    var showCamera by remember { mutableStateOf(false) }
    
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
        containerColor = DarkGray,
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = WhiteText,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkGray
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkGray,
                contentColor = PastelGreen
            ) {
                val items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Statistics,
                    BottomNavItem.Camera,
                    BottomNavItem.AI, 
                    BottomNavItem.Discovery
                )

                items.forEach { item ->
                    if (item == BottomNavItem.Camera) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    selectedTab = item.route
                                    showCamera = true
                                },
                                containerColor = PastelGreen,
                                contentColor = BlackPrimary,
                                shape = CircleShape,
                                modifier = Modifier.size(60.dp)
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    } else {
                        NavigationBarItem(
                            modifier = Modifier.weight(1f),
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = { 
                                Text(
                                    item.label, 
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    maxLines = 1
                                ) 
                            },
                            selected = selectedTab == item.route,
                            onClick = {
                                selectedTab = item.route
                                showCamera = false
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PastelGreen,
                                selectedTextColor = PastelGreen,
                                unselectedIconColor = GrayText,
                                unselectedTextColor = GrayText,
                                indicatorColor = GreenTransparent
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
                .background(DarkGray)
        ) {
            when {
                showCamera && selectedTab == BottomNavItem.Camera.route -> {
                    CameraScreen(
                        onPhotoCaptured = { file, bitmap ->
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
                    com.haphuongquynh.foodmooddiary.presentation.screens.statistics.StatisticsScreen()
                }
                selectedTab == BottomNavItem.AI.route -> {
                    ChatScreen()
                }
                selectedTab == BottomNavItem.Discovery.route -> {
                    com.haphuongquynh.foodmooddiary.presentation.screens.discovery.DiscoveryScreen(
                        onNavigateBack = { selectedTab = BottomNavItem.Home.route }
                    )
                }
            }
        }
    }
}