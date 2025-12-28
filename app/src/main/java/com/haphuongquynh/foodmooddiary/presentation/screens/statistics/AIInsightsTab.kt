package com.haphuongquynh.foodmooddiary.presentation.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.haphuongquynh.foodmooddiary.ui.theme.*

@Composable
fun AIInsightsTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // AI Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = BlackSecondary
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                PastelGreen.copy(alpha = 0.2f),
                                GoldPrimary.copy(alpha = 0.1f)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(PastelGreen, GoldPrimary)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = BlackPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = "Phân tích AI",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhiteText
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        text = "Trong tuần qua, cảm xúc của bạn đã cải thiện đáng kể! Các bữa ăn tại nhà hàng Việt Nam có xu hướng làm bạn vui vẻ hơn. Hãy duy trì thói quen ăn sáng đều đặn nhé! 🌟",
                        fontSize = 15.sp,
                        color = WhiteText,
                        lineHeight = 22.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Insights List
        Text(
            text = "Thống kê chi tiết",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = WhiteText
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        InsightCard(
            icon = Icons.Default.Restaurant,
            title = "Món ăn yêu thích",
            value = "Phở Hà Nội",
            description = "Xuất hiện 12 lần trong tháng",
            trend = "+3 lần so với tháng trước",
            color = PastelGreen
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        InsightCard(
            icon = Icons.Default.SentimentSatisfiedAlt,
            title = "Cảm xúc tích cực",
            value = "87%",
            description = "Tỷ lệ bữa ăn vui vẻ",
            trend = "+12% so với tháng trước",
            color = GoldPrimary
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        InsightCard(
            icon = Icons.Default.Schedule,
            title = "Thời gian yêu thích",
            value = "19:00 - 20:00",
            description = "Khung giờ ăn tối thường xuyên nhất",
            trend = "Duy trì đều đặn",
            color = Color(0xFF90CAF9)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        InsightCard(
            icon = Icons.Default.LocationOn,
            title = "Địa điểm ưa chuộng",
            value = "Nhà hàng Việt",
            description = "Quán ăn bạn ghé nhiều nhất",
            trend = "18 lượt check-in",
            color = ErrorRed
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Recommendations
        Text(
            text = "Gợi ý cho bạn",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = WhiteText
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        RecommendationCard(
            icon = Icons.Default.TrendingUp,
            title = "Thử món mới",
            description = "Bạn chưa thử món Ý trong 2 tuần. Thử khám phá món mới để đa dạng hơn nhé!"
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        RecommendationCard(
            icon = Icons.Default.FitnessCenter,
            title = "Cân bằng dinh dưỡng",
            description = "Hãy thêm nhiều rau xanh vào bữa tối để cân bằng chế độ ăn"
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        RecommendationCard(
            icon = Icons.Default.CalendarMonth,
            title = "Thói quen tốt",
            description = "Bạn đã duy trì streak 7 ngày! Tiếp tục ghi nhật ký mỗi ngày nhé 🔥"
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun InsightCard(
    icon: ImageVector,
    title: String,
    value: String,
    description: String,
    trend: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BlackSecondary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = WhiteText
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = color.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = trend,
                        fontSize = 11.sp,
                        color = color,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BlackSecondary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PastelGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = PastelGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhiteText
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
