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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthViewModel
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
 * Avatar, streak counter, notifications toggle, theme selector, data management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
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
                            Icon(Icons.Default.ArrowBack, "Back", tint = WhiteText)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BlackPrimary
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
                // Profile Section with premium avatar + streak
                ProfileHeaderSection(
                    name = currentUser?.displayName ?: currentUser?.email ?: "User",
                    handle = currentUser?.email ?: "",
                    streakDays = currentStreak
                )

                // Report grid
                ReportOptionsGrid()

                // Community & Social
                CommunityCard()
                SocialMediaSection()

                // Notifications
                Text(
                    "Notifications",
                    color = PastelGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ToggleButton(
                        label = "ON",
                        selected = notificationsEnabled,
                        onClick = { notificationsEnabled = true }
                    )
                    ToggleButton(
                        label = "OFF",
                        selected = !notificationsEnabled,
                        onClick = { notificationsEnabled = false }
                    )
                }

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
                            onClick = { selectedTheme = theme }
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
                    text = "→ Clear All Entries",
                    onClick = { /* clear all */ }
                )

                // Export Data
                Text(
                    "Export Data",
                    color = PastelGreen,
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
                    color = PastelGreen,
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
        color = if (selected) PastelGreen else BlackSecondary
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                color = if (selected) BlackPrimary else WhiteText,
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
        Text(
            text,
            modifier = Modifier.padding(16.dp),
            color = WhiteText,
            fontSize = 14.sp
        )
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

@Composable
private fun ReportOptionsGrid(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ReportOptionCard(
            icon = Icons.Default.Restaurant,
            title = "Nutrition",
            color = OrangeAccent,
            modifier = Modifier.weight(1f)
        )

        ReportOptionCard(
            icon = Icons.Default.FitnessCenter,
            title = "Workout",
            color = PastelGreen,
            modifier = Modifier.weight(1f)
        )

        ReportOptionCard(
            icon = Icons.Default.DirectionsWalk,
            title = "Steps",
            color = StreakOrange,
            modifier = Modifier.weight(1f)
        )

        ReportOptionCard(
            icon = Icons.Default.EmojiEvents,
            title = "Weight",
            color = GoldPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ReportOptionCard(
    icon: ImageVector,
    title: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { /* open report */ },
        shape = RoundedCornerShape(14.dp),
        color = BlackSecondary,
        border = BorderStroke(1.dp, BlackTertiary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                color = WhiteText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CommunityCard(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* join community */ },
        shape = RoundedCornerShape(16.dp),
        color = BlackSecondary,
        border = BorderStroke(1.dp, PastelGreenDark)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Community",
                    color = PastelGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Share your streaks and meals with friends.",
                    color = GrayText,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { /* join now */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PastelGreen,
                        contentColor = BlackPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Join now", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PastelGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Group,
                    contentDescription = null,
                    tint = PastelGreen,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun SocialMediaSection(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Follow FoodMoodDiary",
            fontSize = 14.sp,
            color = GrayText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SocialMediaButton(
                name = "Tiktok",
                onClick = { /* tiktok */ },
                modifier = Modifier.weight(1f)
            )

            SocialMediaButton(
                name = "Facebook",
                onClick = { /* facebook */ },
                modifier = Modifier.weight(1f)
            )

            SocialMediaButton(
                name = "Instagram",
                onClick = { /* instagram */ },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SocialMediaButton(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = BlackSecondary,
        border = BorderStroke(1.dp, BlackTertiary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                when (name) {
                    "Tiktok" -> Icons.Default.MusicNote
                    "Facebook" -> Icons.Default.Facebook
                    else -> Icons.Default.CameraAlt
                },
                contentDescription = name,
                tint = WhiteText,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = name,
                fontSize = 13.sp,
                color = WhiteText
            )
        }
    }
}
