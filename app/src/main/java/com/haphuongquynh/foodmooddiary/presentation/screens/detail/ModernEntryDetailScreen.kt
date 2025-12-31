package com.haphuongquynh.foodmooddiary.presentation.screens.detail

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import java.text.SimpleDateFormat
import java.util.*

/**
 * Modern Entry Detail Screen
 * Shows large photo, AI palette, AI suggestions, share button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernEntryDetailScreen(
    entry: FoodEntry,
    onNavigateBack: () -> Unit,
    onShare: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1C1C1E)
    ) {
        Scaffold(
            containerColor = Color(0xFF1C1C1E),
            topBar = {
                TopAppBar(
                    title = { Text("Entry Detail", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* edit */ }) {
                            Icon(Icons.Default.Edit, "Edit", tint = Color.White)
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
            ) {
                // Large Photo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    if (entry.localPhotoPath != null) {
                        AsyncImage(
                            model = entry.localPhotoPath,
                            contentDescription = entry.foodName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF2C2C2E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Food Name with Emoji
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            entry.foodName,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            entry.mood ?: "😊",
                            fontSize = 24.sp
                        )
                    }

                    // Date & Time, Location
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoColumn(
                            label = "Date & Time",
                            value = formatDateTime(entry.timestamp)
                        )
                        InfoColumn(
                            label = "Location",
                            value = entry.location?.address ?: "Unknown"
                        )
                    }

                    // Meal Type, Rating
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoColumn(
                            label = "Meal Type",
                            value = "Dinner"
                        )
                        Column {
                            Text(
                                "Rating",
                                color = Color(0xFF9FD4A8),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = Color(0xFF9FD4A8),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Notes
                    if (entry.notes.isNotEmpty()) {
                        Column {
                            Text(
                                "Notes",
                                color = Color(0xFF9FD4A8),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                entry.notes,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // AI Suggestion
                    Text(
                        "AI Suggestion",
                        color = Color(0xFF9FD4A8),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF2C2C2E)
                    ) {
                        Text(
                            "Ảnh có nhiều màu đỏ → Gợi ý mood Happy hoặc Energy",
                            modifier = Modifier.padding(16.dp),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                    // Share Button
                    Button(
                        onClick = onShare,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9FD4A8)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, "Share", tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Share",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoColumn(label: String, value: String) {
    Column {
        Text(
            label,
            color = Color(0xFF9FD4A8),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

private fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy - h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
