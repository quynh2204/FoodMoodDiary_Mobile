package com.haphuongquynh.foodmooddiary.presentation.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.StatisticsViewModel

/**
 * Modern Profile & Settings Screen
 * Avatar, streak counter, notifications toggle, theme selector, data management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToNotificationSettings: () -> Unit = {},
    onNavigateToDataManagement: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel(),
    statisticsViewModel: StatisticsViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    var currentStreak by remember { mutableStateOf(0) }
    var notificationsEnabled by remember { mutableStateOf(currentUser?.notificationsEnabled ?: true) }
    var selectedTheme by remember { mutableStateOf(currentUser?.themePreference ?: "Auto") }
    
    // Load streak on init
    LaunchedEffect(Unit) {
        statisticsViewModel.getCurrentStreak { streak ->
            currentStreak = streak
        }
    }
    
    // Update local state when currentUser changes
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            notificationsEnabled = user.notificationsEnabled
            selectedTheme = user.themePreference
        }
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1C1C1E)
    ) {
        Scaffold(
            containerColor = Color(0xFF1C1C1E),
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "Profile & Settings",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1C1C1E)
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3C3C3E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Display Name
                    Text(
                        currentUser?.displayName ?: "Quynh",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Streak
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Streak: ",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            "🔥 $currentStreak days in a row",
                            color = Color(0xFF9FD4A8),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Notifications
                Text(
                    "Notifications",
                    color = Color(0xFF9FD4A8),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                OptionButton(
                    text = "→ Notification Settings",
                    onClick = onNavigateToNotificationSettings
                )



                // Data Management
                Text(
                    "Data Management",
                    color = Color(0xFF9FD4A8),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                OptionButton(
                    text = "→ Manage & Export Data",
                    onClick = onNavigateToDataManagement
                )

                // Export Data
                Text(
                    "Export Data",
                    color = Color(0xFF9FD4A8),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                OptionButton(
                    text = "→ Export as CSV",
                    onClick = { /* export CSV */ }
                )
                OptionButton(
                    text = "→ Export as PDF",
                    onClick = { /* export PDF */ }
                )

                // About
                Text(
                    "About",
                    color = Color(0xFF9FD4A8),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                OptionButton(
                    text = "→ Terms of Service",
                    onClick = { /* terms */ }
                )
                OptionButton(
                    text = "→ Privacy Policy",
                    onClick = { /* privacy */ }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Logout Button
                Button(
                    onClick = {
                        authViewModel.logout()
                        onNavigateToLogin()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5252)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Log out",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ToggleButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFF4CAF50) else Color(0xFF3C3C3E)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ThemeButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(70.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = if (selected) Color(0xFF4CAF50) else Color(0xFF3C3C3E),
        border = if (selected) BorderStroke(2.dp, Color(0xFF9FD4A8)) else null
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun OptionButton(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF2C2C2E)
    ) {
        Text(
            text,
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            fontSize = 14.sp
        )
    }
}
