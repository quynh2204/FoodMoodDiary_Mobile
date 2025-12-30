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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.haphuongquynh.foodmooddiary.domain.model.ColorDistribution
import com.haphuongquynh.foodmooddiary.domain.model.MealDistribution
import com.haphuongquynh.foodmooddiary.domain.model.MealType
import com.haphuongquynh.foodmooddiary.domain.model.MoodTrendPoint
import com.haphuongquynh.foodmooddiary.domain.model.WeeklySummary
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.DateRange
import com.haphuongquynh.foodmooddiary.ui.theme.BlackPrimary
import com.haphuongquynh.foodmooddiary.ui.theme.BlackSecondary
import com.haphuongquynh.foodmooddiary.ui.theme.PastelGreen
import com.haphuongquynh.foodmooddiary.ui.theme.WhiteText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChartsTab(
    moodTrend: List<MoodTrendPoint>,
    mealDistribution: List<MealDistribution>,
    colorDistribution: List<ColorDistribution>,
    weeklySummary: WeeklySummary?,
    selectedRange: DateRange,
    onPeriodChange: (DateRange) -> Unit
) {
    val periodOptions = listOf(
        DateRange.LAST_7_DAYS to "7 ngày",
        DateRange.LAST_30_DAYS to "30 ngày",
        DateRange.LAST_90_DAYS to "90 ngày"
    )

    val totalEntries = weeklySummary?.totalEntries ?: moodTrend.sumOf { it.entryCount }
    val averageMood = weeklySummary?.averageMoodScore ?: if (moodTrend.isNotEmpty()) {
        moodTrend.map { it.averageMoodScore }.average().toFloat()
    } else 0f
    val streak = weeklySummary?.streak ?: 0
    val photoCount = colorDistribution.sumOf { it.count }.takeIf { it > 0 } ?: totalEntries

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            periodOptions.forEach { (range, label) ->
                FilterChip(
                    selected = selectedRange == range,
                    onClick = { onPeriodChange(range) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PastelGreen,
                        selectedLabelColor = BlackPrimary,
                        containerColor = BlackSecondary,
                        labelColor = WhiteText
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Xu hướng cảm xúc",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = WhiteText
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            colors = CardDefaults.cardColors(containerColor = BlackSecondary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                MoodLineChart(moodTrend)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Số bữa ăn mỗi ngày",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = WhiteText
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            colors = CardDefaults.cardColors(containerColor = BlackSecondary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                MealsBarChart(moodTrend)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Phân bố bữa ăn",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = WhiteText
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            colors = CardDefaults.cardColors(containerColor = BlackSecondary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                MoodPieChart(mealDistribution)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Thống kê nhanh",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = WhiteText
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStatCard(
                icon = Icons.Default.Restaurant,
                label = "Tổng bữa ăn",
                value = if (totalEntries > 0) "$totalEntries" else "--",
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                icon = Icons.Default.Star,
                label = "Tâm trạng TB",
                value = if (averageMood > 0f) String.format(Locale.getDefault(), "%.1f/10", averageMood) else "--",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStatCard(
                icon = Icons.Default.LocalFireDepartment,
                label = "Streak",
                value = if (streak > 0) "$streak ngày" else "--",
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                icon = Icons.Default.Photo,
                label = "Ảnh chụp",
                value = if (photoCount > 0) "$photoCount" else "--",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun MoodLineChart(moodTrend: List<MoodTrendPoint>) {
    if (moodTrend.isEmpty()) {
        ChartEmptyState("Chưa có dữ liệu cảm xúc")
        return
    }

    val sortedTrend = remember(moodTrend) { moodTrend.sortedBy { it.date } }
    val labels = remember(sortedTrend) {
        val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())
        sortedTrend.map { formatter.format(Date(it.date)) }
    }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(false)
                setPinchZoom(false)
                setDrawGridBackground(false)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = android.graphics.Color.parseColor("#9E9E9E")
                    setDrawGridLines(false)
                    granularity = 1f
                }

                axisLeft.apply {
                    textColor = android.graphics.Color.parseColor("#9E9E9E")
                    setDrawGridLines(true)
                    gridColor = android.graphics.Color.parseColor("#2C2C2E")
                    axisMinimum = 0f
                    axisMaximum = 10f
                }

                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val entries = sortedTrend.mapIndexed { index, point ->
                Entry(index.toFloat(), point.averageMoodScore.coerceIn(0f, 10f))
            }

            val dataSet = LineDataSet(entries, "Mood").apply {
                color = android.graphics.Color.parseColor("#A8D8A8")
                lineWidth = 3f
                setCircleColor(android.graphics.Color.parseColor("#A8D8A8"))
                circleRadius = 5f
                setDrawValues(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                fillColor = android.graphics.Color.parseColor("#A8D8A8")
                fillAlpha = 50
            }

            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels.toTypedArray())
            chart.data = LineData(dataSet)
            chart.invalidate()
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun MealsBarChart(moodTrend: List<MoodTrendPoint>) {
    if (moodTrend.isEmpty()) {
        ChartEmptyState("Chưa có dữ liệu bữa ăn")
        return
    }

    val sortedTrend = remember(moodTrend) { moodTrend.sortedBy { it.date } }
    val labels = remember(sortedTrend) {
        val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())
        sortedTrend.map { formatter.format(Date(it.date)) }
    }

    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(false)
                setDrawGridBackground(false)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = android.graphics.Color.parseColor("#9E9E9E")
                    setDrawGridLines(false)
                    granularity = 1f
                }

                axisLeft.apply {
                    textColor = android.graphics.Color.parseColor("#9E9E9E")
                    setDrawGridLines(true)
                    gridColor = android.graphics.Color.parseColor("#2C2C2E")
                    axisMinimum = 0f
                }

                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val entries = sortedTrend.mapIndexed { index, point ->
                BarEntry(index.toFloat(), point.entryCount.toFloat())
            }

            val dataSet = BarDataSet(entries, "Meals").apply {
                color = android.graphics.Color.parseColor("#FFD700")
                valueTextColor = android.graphics.Color.parseColor("#FFFFFF")
                valueTextSize = 10f
            }

            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels.toTypedArray())
            chart.data = BarData(dataSet).apply { barWidth = 0.6f }
            chart.invalidate()
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun MoodPieChart(mealDistribution: List<MealDistribution>) {
    if (mealDistribution.isEmpty()) {
        ChartEmptyState("Chưa có dữ liệu phân bố bữa ăn")
        return
    }

    val entries = remember(mealDistribution) {
        mealDistribution.map {
            PieEntry(it.percentage, mealTypeLabel(it.mealType))
        }
    }

    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = true
                legend.textColor = android.graphics.Color.parseColor("#FFFFFF")
                legend.textSize = 12f
                setDrawEntryLabels(false)
                setUsePercentValues(true)
            }
        },
        update = { chart ->
            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(
                    android.graphics.Color.parseColor("#A8D8A8"),
                    android.graphics.Color.parseColor("#FFD700"),
                    android.graphics.Color.parseColor("#90CAF9"),
                    android.graphics.Color.parseColor("#EF5350")
                )
                valueTextColor = android.graphics.Color.parseColor("#1C1C1E")
                valueTextSize = 12f
                sliceSpace = 2f
            }

            chart.data = PieData(dataSet)
            chart.invalidate()
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun ChartEmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(message, color = Color.Gray)
    }
}

private fun mealTypeLabel(type: MealType): String = when (type) {
    MealType.BREAKFAST -> "Sáng"
    MealType.LUNCH -> "Trưa"
    MealType.DINNER -> "Tối"
    MealType.SNACK -> "Snack"
}

@Composable
fun QuickStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = BlackSecondary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
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
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhiteText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
