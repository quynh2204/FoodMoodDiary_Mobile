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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haphuongquynh.foodmooddiary.domain.model.Insight
import com.haphuongquynh.foodmooddiary.domain.model.InsightType
import com.haphuongquynh.foodmooddiary.ui.theme.*

@Composable
fun AIInsightsTab(insights: List<Insight>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (insights.isEmpty()) {
            // Empty state
            EmptyInsightsState()
        } else {
            // Có insights - hiển thị summary + chi tiết
            AISummaryCard(insightCount = insights.size)
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Chi tiết insights",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = WhiteText
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            insights.forEach { insight ->
                val (icon, color) = insightStyle(insight.type)
                InsightCard(
                    icon = icon,
                    title = insight.title,
                    value = getInsightValue(insight.type),
                    description = insight.description,
                    trend = if (insight.actionable) "Nên thử ngay" else null,
                    color = color
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun EmptyInsightsState() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Bắt đầu ghi lại",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = WhiteText
        )
        Spacer(modifier = Modifier.height(16.dp))
        RecommendationCard(
            icon = Icons.Default.CalendarMonth,
            title = "Chưa có dữ liệu",
            description = "Ghi chép vài bữa ăn để hệ thống tạo insight cho bạn. Mỗi bữa ăn sẽ giúp AI hiểu rõ hơn về thói quen của bạn."
        )
    }
}

@Composable
private fun AISummaryCard(insightCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BlackSecondary),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    text = "Bạn có $insightCount insight từ AI phân tích thói quen ăn uống",
                    fontSize = 15.sp,
                    color = WhiteText,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

private fun insightStyle(type: InsightType): Pair<ImageVector, Color> = when (type) {
    InsightType.MOOD_PATTERN -> Icons.Default.SentimentSatisfiedAlt to PastelGreen
    InsightType.FOOD_CORRELATION -> Icons.Default.Restaurant to GoldPrimary
    InsightType.TIME_PATTERN -> Icons.Default.Schedule to Color(0xFF90CAF9)
    InsightType.COLOR_PATTERN -> Icons.Default.Palette to ErrorRed
    InsightType.STREAK -> Icons.Default.LocalFireDepartment to StreakOrange
    InsightType.RECOMMENDATION -> Icons.Default.AutoAwesome to PastelGreen
}

private fun getInsightValue(type: InsightType): String = when (type) {
    InsightType.MOOD_PATTERN -> "Mẫu tâm trạng"
    InsightType.FOOD_CORRELATION -> "Tương quan thực phẩm"
    InsightType.TIME_PATTERN -> "Mẫu thời gian"
    InsightType.COLOR_PATTERN -> "Mẫu màu sắc"
    InsightType.STREAK -> "Chuỗi liên tiếp"
    InsightType.RECOMMENDATION -> "Khuyến nghị"
}

@Composable
fun InsightCard(
    icon: ImageVector,
    title: String,
    value: String,
    description: String,
    trend: String?,
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
            verticalAlignment = Alignment.Top
        ) {
            // Icon box
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
            
            // Content
            Column(modifier = Modifier.weight(1f)) {
                // Title label
                Text(
                    text = title,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Value
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Description
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = WhiteText,
                    lineHeight = 18.sp
                )
                
                // Trend badge
                if (!trend.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = trend,
                            fontSize = 11.sp,
                            color = color,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
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
