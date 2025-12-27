package com.haphuongquynh.foodmooddiary.presentation.screens.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haphuongquynh.foodmooddiary.ui.theme.*

/**
 * Empty state screen - only camera button shown when no entries exist
 */
@Composable
fun EmptyCameraOnlyScreen(
    onCameraClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Welcome text
            Text(
                text = "Food Mood Diary",
                color = WhiteText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Chụp ảnh món ăn đầu tiên\nđể bắt đầu ghi nhật ký",
                color = GrayText,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Large camera button
            FloatingActionButton(
                onClick = onCameraClick,
                containerColor = PastelGreen,
                contentColor = BlackPrimary,
                shape = CircleShape,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Chụp ảnh",
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = "Nhấn để chụp ảnh",
                color = PastelGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
