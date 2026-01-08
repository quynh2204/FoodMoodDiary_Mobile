package com.haphuongquynh.foodmooddiary.presentation.screens.profile

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.DataManagementViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.ExportState
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.ProfileUpdateState
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
import com.haphuongquynh.foodmooddiary.ui.theme.StreakOrange
import com.haphuongquynh.foodmooddiary.ui.theme.WhiteText
import java.io.File

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
    val profileUpdateState by authViewModel.profileUpdateState.collectAsState()

    var currentStreak by remember { mutableStateOf(0) }
    var totalMeals by remember { mutableStateOf(0) }
    var topFood by remember { mutableStateOf<String?>(null) }
    var avgMood by remember { mutableStateOf(0f) }

    var showClearDataDialog by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var editNameText by remember { mutableStateOf(currentUser?.displayName ?: "") }

    // Stat dialog states
    var showStreakDialog by remember { mutableStateOf(false) }
    var showMealsDialog by remember { mutableStateOf(false) }
    var showTopFoodDialog by remember { mutableStateOf(false) }
    var showMoodDialog by remember { mutableStateOf(false) }

    // Gallery launcher for profile image
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                // Save to temp file
                val tempFile = File(context.cacheDir, "profile_${System.currentTimeMillis()}.jpg")
                tempFile.outputStream().use { out ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
                }

                // Upload profile image
                authViewModel.updateProfileImage(tempFile.absolutePath)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Load all stats on init
    LaunchedEffect(Unit) {
        statisticsViewModel.getCurrentStreak { streak ->
            currentStreak = streak
        }
        statisticsViewModel.getTotalMeals { count ->
            totalMeals = count
        }
        statisticsViewModel.getTopFood { food ->
            topFood = food
        }
        statisticsViewModel.getAverageMood { mood ->
            avgMood = mood
        }
    }

    // Update local state when currentUser changes
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            editNameText = user.displayName
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

    // Handle profile update state
    LaunchedEffect(profileUpdateState) {
        when (val state = profileUpdateState) {
            is ProfileUpdateState.Success -> {
                Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                authViewModel.resetProfileUpdateState()
            }
            is ProfileUpdateState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                authViewModel.resetProfileUpdateState()
            }
            else -> {}
        }
    }

    // Clear data confirmation dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Xóa tất cả bài viết?", color = WhiteText) },
            text = {
                Text(
                    "Thao tác này sẽ xóa vĩnh viễn tất cả các bài viết của bạn. Hành động này không thể hoàn tác.",
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
                    Text("Xóa", color = OrangeAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Hủy", color = PastelGreen)
                }
            },
            containerColor = BlackSecondary
        )
    }

    // Edit name dialog
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Sửa tên hiển thị", color = WhiteText) },
            text = {
                OutlinedTextField(
                    value = editNameText,
                    onValueChange = { editNameText = it },
                    label = { Text("Tên hiển thị", color = GrayText) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = WhiteText,
                        unfocusedTextColor = WhiteText,
                        focusedBorderColor = PastelGreen,
                        unfocusedBorderColor = BlackTertiary,
                        cursorColor = PastelGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editNameText.isNotBlank()) {
                            authViewModel.updateDisplayName(editNameText.trim())
                            showEditNameDialog = false
                        }
                    }
                ) {
                    Text("Lưu", color = PastelGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    editNameText = currentUser?.displayName ?: ""
                    showEditNameDialog = false
                }) {
                    Text("Hủy", color = GrayText)
                }
            },
            containerColor = BlackSecondary
        )
    }

    // Streak stat dialog
    if (showStreakDialog) {
        StatDialog(
            title = "Chuỗi đăng nhập",
            icon = Icons.Default.LocalFireDepartment,
            iconTint = StreakOrange,
            statValue = "$currentStreak ngày",
            description = if (currentStreak > 0)
                "Bạn đã ghi nhận bữa ăn liên tục trong $currentStreak ngày! Tiếp tục nhé!"
                else "Bắt đầu ghi nhận bữa ăn hàng ngày để xây dựng chuỗi của bạn!",
            onDismiss = { showStreakDialog = false }
        )
    }

    // Meals stat dialog
    if (showMealsDialog) {
        StatDialog(
            title = "Bữa ăn đã ghi",
            icon = Icons.Default.Restaurant,
            iconTint = PastelGreen,
            statValue = "$totalMeals bữa",
            description = "Bạn đã ghi nhận tổng cộng $totalMeals bữa ăn. Mỗi bữa đều đáng giá!",
            onDismiss = { showMealsDialog = false }
        )
    }

    // Top food stat dialog
    if (showTopFoodDialog) {
        StatDialog(
            title = "Món ăn yêu thích",
            icon = Icons.Default.Favorite,
            iconTint = OrangeAccent,
            statValue = topFood ?: "Chưa có dữ liệu",
            description = if (topFood != null)
                "\"$topFood\" là món ăn bạn ghi nhận nhiều nhất. Có vẻ đây là món yêu thích của bạn!"
                else "Bắt đầu ghi nhận bữa ăn để khám phá món yêu thích của bạn!",
            onDismiss = { showTopFoodDialog = false }
        )
    }

    // Mood stat dialog
    if (showMoodDialog) {
        StatDialog(
            title = "Cảm xúc trung bình",
            icon = Icons.Default.Mood,
            iconTint = GoldPrimary,
            statValue = if (avgMood > 0) String.format("%.1f/10", avgMood) else "Chưa có dữ liệu",
            description = when {
                avgMood >= 8 -> "Cảm xúc trung bình của bạn rất tốt! Các lựa chọn thực ăn của bạn có vẻ mang lại niềm vui."
                avgMood >= 6 -> "Cảm xúc trung bình của bạn ổn. Tiếp tục theo dõi để tìm ra điều gì khiến bạn hạnh phúc nhất!"
                avgMood >= 4 -> "Cảm xúc của bạn ở mức trung bình. Thử thực nghiệm với các món ăn khác nhau!"
                avgMood > 0 -> "Cảm xúc của bạn có thể tốt hơn. Hãy thử các món ăn nâng cao tinh thần!"
                else -> "Bắt đầu ghi nhận bữa ăn với cảm xúc để theo dõi tinh thần của bạn!"
            },
            onDismiss = { showMoodDialog = false }
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
                            "Hồ sơ & Cài đặt",
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
                        photoUrl = currentUser?.photoUrl,
                        streakDays = currentStreak,
                        onEditName = { showEditNameDialog = true },
                        onEditPhoto = { galleryLauncher.launch("image/*") }
                    )

                    // Stats Grid - 4 buttons
                    StatsButtonsGrid(
                        onStreakClick = { showStreakDialog = true },
                        onMealsClick = { showMealsDialog = true },
                        onTopFoodClick = { showTopFoodDialog = true },
                        onMoodClick = { showMoodDialog = true }
                    )

                    // Follow Us Section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            tint = PastelGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Theo dõi FoodMoodDiary",
                            color = PastelGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    SocialMediaButtons(
                        onTikTokClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com"))
                            context.startActivity(intent)
                        },
                        onFacebookClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"))
                            context.startActivity(intent)
                        },
                        onInstagramClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com"))
                            context.startActivity(intent)
                        }
                    )

                    // Data Management
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = null,
                            tint = PastelGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Quản lý dữ liệu",
                            color = PastelGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OptionButton(
                        text = "Xóa tất cả bài viết",
                        onClick = { showClearDataDialog = true }
                    )

                    // Export Data
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Upload,
                            contentDescription = null,
                            tint = PastelGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Xuất dữ liệu",
                            color = PastelGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OptionButton(
                        text = "Xuất dạng CSV",
                        onClick = { dataManagementViewModel.exportToCSV(context) }
                    )
                    OptionButton(
                        text = "Xuất dạng PDF",
                        onClick = { dataManagementViewModel.exportToPDF(context) }
                    )

                    // About
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = PastelGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Thông tin",
                            color = PastelGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OptionButton(
                        text = "Điều khoản dịch vụ",
                        onClick = {
                            Toast.makeText(context, "Sắp ra mắt", Toast.LENGTH_SHORT).show()
                        }
                    )
                    OptionButton(
                        text = "Chính sách bảo mật",
                        onClick = {
                            Toast.makeText(context, "Sắp ra mắt", Toast.LENGTH_SHORT).show()
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
                            containerColor = PastelGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Đăng xuất",
                            color = BlackPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Loading overlay
                if (exportState is ExportState.Loading || profileUpdateState is ProfileUpdateState.Loading) {
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
private fun OptionButton(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = BlackSecondary,
        border = BorderStroke(1.dp, PastelGreen.copy(alpha = 0.5f))
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
    photoUrl: String?,
    streakDays: Int,
    onEditName: () -> Unit,
    onEditPhoto: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with edit button
        Box(modifier = Modifier.size(120.dp)) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .border(2.dp, PastelGreen, CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(BlackSecondary)
                    .clickable(onClick = onEditPhoto),
                contentAlignment = Alignment.Center
            ) {
                if (photoUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(photoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = PastelGreen,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            // Camera edit button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(PastelGreen)
                    .clickable(onClick = onEditPhoto),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = "Change Photo",
                    tint = BlackPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Name with edit button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                color = WhiteText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onEditName,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Name",
                    tint = GrayText,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

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
        color = PastelGreen.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, PastelGreen)
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
                text = "$streakDays ngày liên tục",
                color = WhiteText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StatsButtonsGrid(
    onStreakClick: () -> Unit,
    onMealsClick: () -> Unit,
    onTopFoodClick: () -> Unit,
    onMoodClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Streak - Orange color
        StatButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.LocalFireDepartment,
            iconTint = Color(0xFFFF9500), // Orange
            hasCircle = true,
            onClick = onStreakClick
        )
        // Meals - White color
        StatButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Restaurant,
            iconTint = Color.White,
            hasCircle = true,
            onClick = onMealsClick
        )
        // Top Food - Red color
        StatButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Favorite,
            iconTint = Color(0xFFFF3B30), // Red
            hasCircle = true,
            onClick = onTopFoodClick
        )
        // Mood - Yellow color
        StatButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Mood,
            iconTint = Color(0xFFFFCC00), // Yellow
            hasCircle = true,
            onClick = onMoodClick
        )
    }
}

@Composable
private fun StatButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    hasCircle: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = BlackSecondary,
        border = BorderStroke(1.dp, PastelGreen.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (hasCircle) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(28.dp)
                    )
                }
            } else {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun StatDialog(
    title: String,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    statValue: String,
    description: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, color = WhiteText, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = statValue,
                    color = iconTint,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = description,
                    color = GrayText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng", color = PastelGreen)
            }
        },
        containerColor = BlackSecondary
    )
}

@Composable
private fun SocialMediaButtons(
    onTikTokClick: () -> Unit,
    onFacebookClick: () -> Unit,
    onInstagramClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SocialButton(
            modifier = Modifier.weight(1f),
            logoRes = com.haphuongquynh.foodmooddiary.R.drawable.tiktok_logo,
            label = "Tiktok",
            onClick = onTikTokClick
        )
        SocialButton(
            modifier = Modifier.weight(1f),
            logoRes = com.haphuongquynh.foodmooddiary.R.drawable.facebook_logo,
            label = "Facebook",
            onClick = onFacebookClick
        )
        SocialButton(
            modifier = Modifier.weight(1f),
            logoRes = com.haphuongquynh.foodmooddiary.R.drawable.instagram_logo,
            label = "Instagram",
            onClick = onInstagramClick
        )
    }
}

@Composable
private fun SocialButton(
    modifier: Modifier = Modifier,
    logoRes: Int,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E1E1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(logoRes)
                    .crossfade(true)
                    .build(),
                contentDescription = label,
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}
