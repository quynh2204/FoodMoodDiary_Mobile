package com.haphuongquynh.foodmooddiary.presentation.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haphuongquynh.foodmooddiary.domain.model.*
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.DateRange
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.StatisticsViewModel
import com.haphuongquynh.foodmooddiary.ui.theme.*

/* ======================= HELPERS ======================= */

private fun moodColorFromScore(score: Float): Color = when {
    score >= 8f -> MoodType.HAPPY.color
    score >= 6f -> MoodType.ENERGETIC.color
    score >= 4f -> MoodType.TIRED.color
    score > 0f -> MoodType.SAD.color
    else -> Color(0xFF3C3C3E)
}

private fun moodTypeFromScore(score: Float): MoodType? {
    return MoodType.entries
        .minByOrNull { kotlin.math.abs(it.score - score) }
}


/* ======================= CONSTANTS ======================= */

private val periodOptions = listOf(
    DateRange.LAST_7_DAYS to "7 ngày",
    DateRange.LAST_30_DAYS to "30 ngày",
    DateRange.LAST_90_DAYS to "90 ngày",
    DateRange.LAST_YEAR to "12 tháng",
    DateRange.ALL_TIME to "Tất cả"
)

/**
 * Statistics Screen - Thống kê nhật ký tâm trạng và thói quen ăn uống
 */
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val moodTrend by viewModel.moodTrend.collectAsStateWithLifecycle()
    val topFoods by viewModel.topFoods.collectAsStateWithLifecycle()
    val mealDistribution by viewModel.mealDistribution.collectAsStateWithLifecycle()
    val colorDistribution by viewModel.colorDistribution.collectAsStateWithLifecycle()
    val insights by viewModel.insights.collectAsStateWithLifecycle()
    val weeklySummary by viewModel.weeklySummary.collectAsStateWithLifecycle()
    
    var selectedRange by remember { mutableStateOf(DateRange.LAST_7_DAYS) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tổng quan", "Biểu đồ", "Lịch", "AI Insights")

    LaunchedEffect(selectedRange) {
        viewModel.setDateRange(selectedRange)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp))
            
            Text(
                text = "Thống kê nhật ký",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Box(modifier = Modifier.size(40.dp))
        }
        
        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = PastelGreen
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    selectedContentColor = PastelGreen,
                    unselectedContentColor = Color.Gray
                )
            }
        }
        
        // Tab Content
        when (selectedTab) {
            0 -> OverviewTab(
                moodTrend = moodTrend,
                topFoods = topFoods,
                weeklySummary = weeklySummary,
                selectedRange = selectedRange,
                onPeriodChange = { selectedRange = it }
            )
            1 -> ChartsTab(
                moodTrend = moodTrend,
                mealDistribution = mealDistribution,
                colorDistribution = colorDistribution,
                weeklySummary = weeklySummary,
                selectedRange = selectedRange,
                onPeriodChange = { selectedRange = it }
            )
            2 -> CalendarTab(moodTrend = moodTrend)
            3 -> AIInsightsTab(insights = insights)
        }
    }
}

@Composable
fun OverviewTab(
    moodTrend: List<MoodTrendPoint>,
    topFoods: List<FoodFrequency>,
    weeklySummary: WeeklySummary?,
    selectedRange: DateRange,
    onPeriodChange: (DateRange) -> Unit
) {
    val periodLabel = when (selectedRange) {
        DateRange.LAST_7_DAYS -> "7 ngày"
        DateRange.LAST_30_DAYS -> "30 ngày"
        DateRange.LAST_90_DAYS -> "90 ngày"
        DateRange.LAST_YEAR -> "12 tháng"
        DateRange.ALL_TIME -> "tất cả thời gian"
    }

    val averageMood = weeklySummary?.averageMoodScore ?: if (moodTrend.isNotEmpty()) {
        moodTrend.map { it.averageMoodScore }.average().toFloat()
    } else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // Period Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val currentIndex = periodOptions.indexOfFirst { it.first == selectedRange }
val currentLabel = periodOptions
    .getOrNull(currentIndex)
    ?.second
    ?: ""

DateRangeSelector(
    currentRange = currentLabel,
    onPreviousClick = {
        if (currentIndex > 0) {
            onPeriodChange(periodOptions[currentIndex - 1].first)
        }
    },
    onNextClick = {
        if (currentIndex < periodOptions.lastIndex) {
            onPeriodChange(periodOptions[currentIndex + 1].first)
        }
    },
    modifier = Modifier.padding(horizontal = 16.dp)
)


            periodOptions.forEach { (range, label) ->
                PeriodButton(
                    text = label,
                    isSelected = selectedRange == range,
                    onClick = { onPeriodChange(range) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        
        // Mood Trend Chart Section
        Text(
            text = "Xu hướng tâm trạng theo $periodLabel",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Chart
        MoodTrendChart(
            moodTrend = moodTrend,
            selectedRange = selectedRange,
            modifier = Modifier.padding(horizontal = 16.dp)
        )


        Spacer(modifier = Modifier.height(24.dp))

        // Entry Summary
        EntrySummaryCards(
            totalEntries = weeklySummary?.totalEntries ?: moodTrend.sumOf { it.entryCount },
            avgMoodScore = averageMood.toInt().coerceAtLeast(0),
            topFoods = topFoods.take(3),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Mood Distribution
        Text(
            text = "Phân bố tâm trạng",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        MoodDistributionCards(
            moodTrend = moodTrend,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Info Message
        InfoMessageCard(
            message = "Ghi chép thường xuyên để xem được xu hướng tâm trạng và thói quen ăn uống của bạn!",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun PeriodButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) PastelGreen else BlackSecondary,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DateRangeSelector(
    currentRange: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(BlackSecondary)
        ) {
            Icon(
                Icons.Default.ChevronLeft,
                contentDescription = "Previous",
                tint = Color.White
            )
        }
        
        Text(
            text = currentRange,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        
        IconButton(
            onClick = onNextClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(BlackSecondary)
        ) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Next",
                tint = Color.White
            )
        }
    }
}

@Composable
fun MoodTrendChart(
    moodTrend: List<MoodTrendPoint>,
    selectedRange: DateRange,
    modifier: Modifier = Modifier
) {
    // 1️⃣ Chuẩn bị data cho chart theo DateRange
    val chartData: List<Pair<String, Float>> = when (selectedRange) {

        DateRange.LAST_7_DAYS -> {
            moodTrend
                .takeLast(7)
                .mapIndexed { index, point ->
                    val label = when (index) {
                        0 -> "T2"
                        1 -> "T3"
                        2 -> "T4"
                        3 -> "T5"
                        4 -> "T6"
                        5 -> "T7"
                        else -> "CN"
                    }
                    label to point.averageMoodScore
                }
        }

        DateRange.LAST_30_DAYS -> {
            moodTrend
                .takeLast(30)
                .chunked(7)
                .mapIndexed { index, week ->
                    "Tuần ${index + 1}" to
                        week.map { it.averageMoodScore }.average().toFloat()
                }
        }

        DateRange.LAST_90_DAYS -> {
            moodTrend
                .takeLast(90)
                .chunked(7)
                .mapIndexed { index, week ->
                    "Tuần ${index + 1}" to
                        week.map { it.averageMoodScore }.average().toFloat()
                }
        }

        else -> emptyList()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = BlackSecondary
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Chart title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Điểm tâm trạng (1-10)",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(PastelGreen)
                    )
                    Text(
                        text = "Trung bình",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2️⃣ Vẽ chart theo chartData (KHÔNG cố định 7 cột)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                chartData.forEach { (label, moodValue) ->
                    val barHeight = (moodValue * 20).dp.coerceAtLeast(4.dp)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (moodValue > 0) {
                            Text(
                                text = moodValue.toInt().toString(),
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(barHeight)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 4.dp,
                                        topEnd = 4.dp
                                    )
                                )
                                .background(
                                    when {
                                        moodValue >= 8f -> Color(0xFF4CAF50)
                                        moodValue >= 5f -> PastelGreen
                                        moodValue > 0f -> Color(0xFFFF9800)
                                        else -> Color(0xFF3C3C3E)
                                    }
                                )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = label,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EntrySummaryCards(
    totalEntries: Int,
    avgMoodScore: Int,
    topFoods: List<FoodFrequency>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatInfoCard(
                title = "Tổng số bài ghi",
                value = "$totalEntries bài",
                icon = Icons.Default.Restaurant,
                modifier = Modifier.weight(1f)
            )
            
            StatInfoCard(
                title = "Tâm trạng TB",
                value = if (avgMoodScore > 0) "$avgMoodScore/10" else "N/A",
                icon = Icons.Default.Mood,
                modifier = Modifier.weight(1f),
                iconColor = when {
                    avgMoodScore >= 8 -> Color(0xFF4CAF50)
                    avgMoodScore >= 5 -> PastelGreen
                    avgMoodScore > 0 -> Color(0xFFFF9800)
                    else -> Color.Gray
                }
            )
        }
        
        if (topFoods.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = BlackSecondary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Fastfood,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Món ăn phổ biến",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    
                    topFoods.forEach { food ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = food.foodName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                            Text(
                                text = "${food.count} lần",
                                fontSize = 14.sp,
                                color = PastelGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatInfoCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    iconColor: Color = PastelGreen
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = BlackSecondary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
            
            Text(
                text = title,
                fontSize = 13.sp,
                color = Color.Gray
            )
            
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun MoodDistributionCards(
    moodTrend: List<MoodTrendPoint>,
    modifier: Modifier = Modifier
) {
    val happy = moodTrend.count { it.averageMoodScore >= 8f }
    val neutral = moodTrend.count { it.averageMoodScore in 5f..7.9f }
    val sad = moodTrend.count { it.averageMoodScore < 5f }
    val total = happy + neutral + sad

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MoodTypeCard("😊", "Vui vẻ", happy, total, MoodType.HAPPY.color, Modifier.weight(1f))
        MoodTypeCard("😐", "Bình thường", neutral, total, MoodType.TIRED.color, Modifier.weight(1f))
        MoodTypeCard("😔", "Buồn", sad, total, MoodType.SAD.color, Modifier.weight(1f))
    }
}

@Composable
fun MoodTypeCard(
    emoji: String,
    label: String,
    count: Int,
    total: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val percent = if (total == 0) 0 else (count * 100 / total)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = BlackSecondary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 32.sp)
            Text(label, fontSize = 13.sp, color = Color.Gray)
            Text("$count ngày", fontWeight = FontWeight.Bold, color = Color.White)
            Text("$percent%", color = color)
        }
    }
}

@Composable
fun InfoMessageCard(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B4332)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2D6A4F)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = Color(0xFF95D5B2),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color(0xFFD8F3DC),
                lineHeight = 20.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
