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
import androidx.navigation.NavController
import com.haphuongquynh.foodmooddiary.presentation.navigation.Screen
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthViewModel
import com.haphuongquynh.foodmooddiary.ui.theme.*

/**
 * Wao + Locket Style Profile Screen
 * - Avatar với gold border
 * - Locket count & streak counter
 * - Health profile (age, height, weight)
 * - Settings & community
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaoLocketProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val scrollState = rememberScrollState()
    
    // Mock data - replace with actual data
    val locketCount = 406
    val streakDays = 5
    val userAge = 21
    val userHeight = 160 // cm
    val userWeight = 52 // kg
    val userName = "Cổm"
    val userHandle = "@hapq"
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkGray
    ) {
        Scaffold(
            containerColor = DarkGray,
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = WhiteText
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = WhiteText
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkGray
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Profile header with avatar
                ProfileHeader(
                    userName = userName,
                    userHandle = userHandle,
                    onEditClick = { /* TODO: Edit profile */ }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Locket Gold banner (if premium)
                LocketGoldBanner(
                    onClick = { /* TODO: Upgrade to gold */ }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Stats: Locket & Streak
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        icon = Icons.Default.Favorite,
                        label = "Lockets",
                        value = locketCount.toString(),
                        iconTint = GoldPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    
                    StatCard(
                        icon = Icons.Default.LocalFireDepartment,
                        label = "${streakDays}d streak",
                        value = "",
                        iconTint = StreakOrange,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Health Profile Card
                HealthProfileCard(
                    age = userAge,
                    height = userHeight,
                    weight = userWeight,
                    onEditClick = { /* TODO: Edit health info */ }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Stats reports
                StatsReportsSection(
                    onClick = { navController.navigate(Screen.Statistics.route) }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Community & Social Section
                CommunitySocialSection()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Logout button
                LogoutButton(
                    onClick = { viewModel.logout() }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    userHandle: String,
    onEditClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with gold gradient border
        Box(
            modifier = Modifier.size(100.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(GoldPrimary, OrangeAccent, GoldSecondary)
                        )
                    )
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(BlackSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = PastelGreen,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            
            // Edit button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(PastelGreen)
                    .clickable { onEditClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Edit",
                    tint = BlackPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = userName,
            color = WhiteText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = userHandle,
            color = GrayText,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun LocketGoldBanner(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = BlackSecondary,
        border = BorderStroke(2.dp, GoldPrimary)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(GoldPrimary, OrangeAccent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = BlackPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Get Locket Gold",
                    color = GoldPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Unlock premium features",
                    color = GrayText,
                    fontSize = 12.sp
                )
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Open",
                tint = GoldPrimary
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = BlackSecondary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (value.isNotEmpty()) {
                Text(
                    text = value,
                    color = WhiteText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = label,
                color = GrayText,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun HealthProfileCard(
    age: Int,
    height: Int,
    weight: Int,
    onEditClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        color = BlackSecondary
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hồ sơ cá nhân",
                    color = WhiteText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Edit",
                        tint = PastelGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Health stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                HealthStatItem(
                    icon = Icons.Default.Cake,
                    label = "$age tuổi"
                )
                
                HealthStatItem(
                    icon = Icons.Default.Height,
                    label = "$height cm"
                )
                
                HealthStatItem(
                    icon = Icons.Default.MonitorWeight,
                    label = "$weight kg"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // BMI or body index section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = PastelGreen.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, PastelGreen)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = PastelGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "Hồ sơ thể chất",
                            color = PastelGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Bạn đang duy trì cân nặng rất tốt!",
                            color = GrayText,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthStatItem(
    icon: ImageVector,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(BlackTertiary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = PastelGreen,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            color = WhiteText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatsReportsSection(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = BlackSecondary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PastelGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.BarChart,
                    contentDescription = null,
                    tint = PastelGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Xem báo cáo thống kê",
                    color = WhiteText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Nutrition & calorie reports",
                    color = GrayText,
                    fontSize = 12.sp
                )
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Open",
                tint = GrayText
            )
        }
    }
}

@Composable
private fun CommunitySocialSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Cộng đồng và hỗ trợ",
            color = WhiteText,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = BlackSecondary
        ) {
            Column {
                CommunityItem(
                    icon = Icons.Default.Group,
                    title = "Gia nhập cộng đồng Wao ngay!",
                    subtitle = "Nơi Wao sẽ đồng hành cùng bạn",
                    onClick = { /* TODO: Join community */ }
                )
                
                Divider(color = BlackTertiary, thickness = 1.dp)
                
                Text(
                    text = "Tìm Wao trên trang mạng xã hội",
                    color = GrayText,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(16.dp)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SocialMediaButton(
                        label = "Tiktok",
                        onClick = { /* TODO: Open Tiktok */ }
                    )
                    SocialMediaButton(
                        label = "Facebook",
                        onClick = { /* TODO: Open Facebook */ }
                    )
                    SocialMediaButton(
                        label = "Instagram",
                        onClick = { /* TODO: Open Instagram */ }
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Divider(color = BlackTertiary, thickness = 1.dp)
                
                CommunityItem(
                    icon = Icons.Default.HelpOutline,
                    title = "Trung tâm hỗ trợ",
                    subtitle = "",
                    onClick = { /* TODO: Support center */ }
                )
            }
        }
    }
}

@Composable
private fun CommunityItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = PastelGreen,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = WhiteText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    color = GrayText,
                    fontSize = 12.sp
                )
            }
        }
        
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = "Open",
            tint = GrayText
        )
    }
}

@Composable
private fun SocialMediaButton(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = BlackTertiary
    ) {
        Text(
            text = label,
            color = WhiteText,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun LogoutButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ErrorRed.copy(alpha = 0.2f),
            contentColor = ErrorRed
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            Icons.Default.Logout,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Đăng xuất",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
