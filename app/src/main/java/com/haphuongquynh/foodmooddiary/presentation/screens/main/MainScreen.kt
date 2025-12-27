package com.haphuongquynh.foodmooddiary.presentation.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.FoodEntryViewModel
import com.haphuongquynh.foodmooddiary.presentation.screens.camera.CameraScreen
import com.haphuongquynh.foodmooddiary.presentation.screens.camera.EmptyCameraOnlyScreen
import com.haphuongquynh.foodmooddiary.presentation.screens.home.PhotoGalleryHomeScreen

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home_tab", Icons.Default.Home, "Trang chủ")
    object Camera : BottomNavItem("camera_tab", Icons.Default.CameraAlt, "Camera")
    object Profile : BottomNavItem("profile_tab", Icons.Default.Person, "Cá nhân")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    foodEntryViewModel: FoodEntryViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(BottomNavItem.Camera.route) }
    val authState by viewModel.authState.collectAsState()
    var showCamera by remember { mutableStateOf(true) }
    val currentUser by viewModel.currentUser.collectAsState()
    val entries by foodEntryViewModel.entries.collectAsState()
    val hasEntries = entries.isNotEmpty()

    // Handle logout
    LaunchedEffect(authState) {
        if (authState is AuthState.LoggedOut) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Main.route) { inclusive = true }
            }
            viewModel.resetAuthState()
        }
    }

    // Show empty camera screen if no entries yet
    if (!hasEntries) {
        EmptyCameraOnlyScreen(
            onCameraClick = {
                showCamera = true
                selectedTab = BottomNavItem.Camera.route
            }
        )
        
        // Show camera when button clicked
        if (showCamera) {
            CameraScreen(
                onPhotoCaptured = { file, bitmap ->
                    showCamera = false
                    navController.navigate(Screen.AddEntry.route)
                },
                onDismiss = {
                    showCamera = false
                }
            )
        }
        return
    }

    // Show full UI with bottom navigation when user has entries
    Scaffold(
        containerColor = DarkGray,
        floatingActionButton = {
            // Camera FAB at bottom center
            FloatingActionButton(
                onClick = {
                    showCamera = true
                },
                containerColor = PastelGreen,
                contentColor = BlackPrimary,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Chụp ảnh",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DarkGray)
        ) {
            when {
                showCamera -> {
                    CameraScreen(
                        onPhotoCaptured = { file, bitmap ->
                            showCamera = false
                            navController.navigate(Screen.AddEntry.route)
                        },
                        onDismiss = {
                            showCamera = false
                        }
                    )
                }
                else -> {
                    PhotoGalleryHomeScreen(
                        navController = navController
                    )
                }
            }
        }
    }
}
