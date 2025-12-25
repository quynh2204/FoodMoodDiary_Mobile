package com.haphuongquynh.foodmooddiary.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haphuongquynh.foodmooddiary.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    onSaveSettings: () -> Unit = {}
) {
    var dailyReminderEnabled by remember { mutableStateOf(true) }
    var mealReminderEnabled by remember { mutableStateOf(true) }
    var insightsEnabled by remember { mutableStateOf(true) }
    var streakReminderEnabled by remember { mutableStateOf(false) }
    
    var breakfastTime by remember { mutableStateOf("08:00") }
    var lunchTime by remember { mutableStateOf("12:00") }
    var dinnerTime by remember { mutableStateOf("18:00") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông báo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = WhiteText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlackPrimary,
                    titleContentColor = WhiteText
                )
            )
        },
        containerColor = BlackPrimary
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Reminder Notifications Section
            SectionHeader(
                icon = Icons.Default.Notifications,
                title = "Nhắc nhở"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            NotificationToggleCard(
                icon = Icons.Default.NotificationsActive,
                title = "Nhắc nhở hàng ngày",
                description = "Nhắc bạn chụp ảnh món ăn mỗi ngày",
                checked = dailyReminderEnabled,
                onCheckedChange = { dailyReminderEnabled = it }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            NotificationToggleCard(
                icon = Icons.Default.Restaurant,
                title = "Nhắc nhở bữa ăn",
                description = "Nhắc bạn ghi lại mỗi bữa ăn",
                checked = mealReminderEnabled,
                onCheckedChange = { mealReminderEnabled = it }
            )
            
            if (mealReminderEnabled) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BlackSecondary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        TimePickerItem(
                            icon = Icons.Default.WbSunny,
                            label = "Bữa sáng",
                            time = breakfastTime,
                            onTimeChange = { breakfastTime = it }
                        )
                        
                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = BlackTertiary
                        )
                        
                        TimePickerItem(
                            icon = Icons.Default.LunchDining,
                            label = "Bữa trưa",
                            time = lunchTime,
                            onTimeChange = { lunchTime = it }
                        )
                        
                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = BlackTertiary
                        )
                        
                        TimePickerItem(
                            icon = Icons.Default.DinnerDining,
                            label = "Bữa tối",
                            time = dinnerTime,
                            onTimeChange = { dinnerTime = it }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Insights Section
            SectionHeader(
                icon = Icons.Default.Insights,
                title = "Thông tin chi tiết"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            NotificationToggleCard(
                icon = Icons.Default.Analytics,
                title = "Thống kê hàng tuần",
                description = "Nhận thống kê và xu hướng mỗi tuần",
                checked = insightsEnabled,
                onCheckedChange = { insightsEnabled = it }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            NotificationToggleCard(
                icon = Icons.Default.LocalFireDepartment,
                title = "Nhắc nhở Streak",
                description = "Nhắc khi streak sắp bị gián đoạn",
                checked = streakReminderEnabled,
                onCheckedChange = { streakReminderEnabled = it }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Save Button
            Button(
                onClick = { /* Save settings */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PastelGreen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Save, "Save")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Lưu cài đặt",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BlackPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionHeader(
    icon: ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = PastelGreen,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = WhiteText
        )
    }
}

@Composable
fun NotificationToggleCard(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BlackSecondary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = PastelGreen,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WhiteText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BlackPrimary,
                    checkedTrackColor = PastelGreen,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = BlackTertiary
                )
            )
        }
    }
}

@Composable
fun TimePickerItem(
    icon: ImageVector,
    label: String,
    time: String,
    onTimeChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = PastelGreen,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = label,
            fontSize = 15.sp,
            color = WhiteText,
            modifier = Modifier.weight(1f)
        )
        
        Button(
            onClick = { /* Show time picker */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = BlackTertiary,
                contentColor = PastelGreen
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                Icons.Default.AccessTime,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = time,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
