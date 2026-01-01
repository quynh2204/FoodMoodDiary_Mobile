package com.haphuongquynh.foodmooddiary.presentation.screens.profile

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.DataManagementViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.ExportState
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.StatisticsViewModel
import com.haphuongquynh.foodmooddiary.ui.theme.BlackPrimary
import com.haphuongquynh.foodmooddiary.ui.theme.BlackSecondary
import com.haphuongquynh.foodmooddiary.ui.theme.BlackTertiary
import com.haphuongquynh.foodmooddiary.ui.theme.GoldPrimary
import com.haphuongquynh.foodmooddiary.ui.theme.GoldSecondary
import com.haphuongquynh.foodmooddiary.ui.theme.GrayText
import com.haphuongquynh.foodmooddiary.ui.theme.OrangeAccent
import com.haphuongquynh.foodmooddiary.ui.theme.PastelGreen
import com.haphuongquynh.foodmooddiary.ui.theme.PastelGreenDark
import com.haphuongquynh.foodmooddiary.ui.theme.PastelGreenLight
import com.haphuongquynh.foodmooddiary.ui.theme.StreakOrange
import com.haphuongquynh.foodmooddiary.ui.theme.WhiteText

/**
 * Modern Profile & Settings Screen
 * Avatar, streak counter, theme selector, data management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    statisticsViewModel: StatisticsViewModel = hiltViewModel(),
    dataManagementViewModel: DataManagementViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentUser by authViewModel.currentUser.collectAsState()
    val exportState by dataManagementViewModel.exportState.collectAsState()

    var currentStreak by remember { mutableStateOf(0) }
    var selectedTheme by remember { mutableStateOf(currentUser?.themePreference ?: "Auto") }
    var showClearDataDialog by remember { mutableStateOf(false) }

    // Load streak on init
    LaunchedEffect(Unit) {
        statisticsViewModel.getCurrentStreak { streak ->
            currentStreak = streak
        }
    }

    // Update local state when currentUser changes
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            selectedTheme = user.themePreference
        }
    }

    // Handle export state changes
    LaunchedEffect(exportState) {
        when (val state = exportState) {
            is ExportState.Success -> {
                Toast.makeText(context, "${state.format} exported successfully", Toast.LENGTH_SHORT).show()
                dataManagementViewModel.resetState()
            }
            is ExportState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                dataManagementViewModel.resetState()
            }
            is ExportState.DataCleared -> {
                Toast.makeText(context, "All entries cleared", Toast.LENGTH_SHORT).show()
                dataManagementViewModel.resetState()
            }
            else -> {}
        }
    }

    // Clear data confirmation dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Clear All Entries?", color = WhiteText) },
            text = {
                Text(
                    "This will permanently delete all your food entries. This action cannot be undone.",
                    color = GrayText
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dataManagementViewModel.clearAllData()
                        showClearDataDialog = false
                    }
                ) {
                    Text("Clear", color = OrangeAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel", color = PastelGreen)
                }
            },
            containerColor = BlackSecondary
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BlackPrimary
    ) {
        Scaffold(
            containerColor = BlackPrimary,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Profile & Settings",
                            color = WhiteText,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = WhiteText)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BlackPrimary
                    )
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Section with premium avatar + streak
                    ProfileHeaderSection(
                        name = currentUser?.displayName ?: currentUser?.email ?: "User",
                        handle = currentUser?.email ?: "",
                        streakDays = currentStreak
                    )

                    // Theme
                    Text(
                        "Theme",
                        color = PastelGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Light", "Dark", "Auto").forEach { theme ->
                            ThemeButton(
                                label = theme,
                                selected = selectedTheme == theme,
                                onClick = {
                                    selectedTheme = theme
                                    authViewModel.updateThemePreference(theme)
                                }
                            )
                        }
                    }

                    // Data Management
                    Text(
                        "Data Management",
                        color = PastelGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OptionButton(
                        text = "Clear All Entries",
                        onClick = { showClearDataDialog = true }
                    )

                    // Export Data
                    Text(
                        "Export Data",
                        color = PastelGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OptionButton(
                        text = "Export as CSV",
                        onClick = { dataManagementViewModel.exportToCSV(context) }
                    )
                    OptionButton(
                        text = "Export as PDF",
                        onClick = { dataManagementViewModel.exportToPDF(context) }
                    )

                    // About
                    Text(
                        "About",
                        color = PastelGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OptionButton(
                        text = "Terms of Service",
                        onClick = {
                            Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
                        }
                    )
                    OptionButton(
                        text = "Privacy Policy",
                        onClick = {
                            Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
                        }
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
                            containerColor = OrangeAccent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Log out",
                            color = WhiteText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Loading overlay
                if (exportState is ExportState.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BlackPrimary.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PastelGreen)
                    }
                }
            }
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
        color = if (selected) PastelGreen else BlackTertiary,
        border = if (selected) BorderStroke(2.dp, PastelGreenLight) else BorderStroke(1.dp, BlackSecondary)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                color = if (selected) BlackPrimary else WhiteText,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
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
        color = BlackSecondary,
        border = BorderStroke(1.dp, BlackTertiary)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text,
                modifier = Modifier.weight(1f),
                color = WhiteText,
                fontSize = 14.sp
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = GrayText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ProfileHeaderSection(
    name: String,
    handle: String,
    streakDays: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(120.dp)) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(GoldPrimary, OrangeAccent, GoldSecondary)
                        )
                    )
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(BlackSecondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = PastelGreen,
                    modifier = Modifier.size(56.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(GoldPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Premium",
                    tint = BlackPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = name,
            color = WhiteText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        if (handle.isNotBlank()) {
            Text(
                text = handle,
                color = GrayText,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        StreakChip(streakDays = streakDays)
    }
}

@Composable
private fun StreakChip(streakDays: Int) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = BlackSecondary,
        border = BorderStroke(1.dp, PastelGreenDark)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = StreakOrange,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "$streakDays-day streak",
                color = WhiteText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
