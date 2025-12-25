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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.haphuongquynh.foodmooddiary.ui.theme.*

@Composable
fun ChartsTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Period Selector
        var selectedPeriod by remember { mutableStateOf("Tuần") }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Tuần", "Tháng", "Năm").forEach { period ->
                FilterChip(
                    selected = selectedPeriod == period,
                    onClick = { selectedPeriod = period },
                    label = { Text(period) },
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
        
        // Mood Trend Line Chart
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
                MoodLineChart()
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Meals Per Day Bar Chart
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
                MealsBarChart()
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Mood Distribution Pie Chart
        Text(
            text = "Phân bổ cảm xúc",
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
                MoodPieChart()
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Quick Stats Grid
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
                value = "234",
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                icon = Icons.Default.Star,
                label = "Trung bình",
                value = "4.2/5",
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
                value = "7 ngày",
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                icon = Icons.Default.Photo,
                label = "Ảnh chụp",
                value = "189",
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun MoodLineChart() {
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
                
                // X Axis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = android.graphics.Color.parseColor("#9E9E9E")
                    setDrawGridLines(false)
                    granularity = 1f
                    valueFormatter = IndexAxisValueFormatter(
                        arrayOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                    )
                }
                
                // Left Axis
                axisLeft.apply {
                    textColor = android.graphics.Color.parseColor("#9E9E9E")
                    setDrawGridLines(true)
                    gridColor = android.graphics.Color.parseColor("#2C2C2E")
                    axisMinimum = 0f
                    axisMaximum = 5f
                }
                
                // Right Axis
                axisRight.isEnabled = false
                
                // Sample Data
                val entries = listOf(
                    Entry(0f, 3.5f),
                    Entry(1f, 4.0f),
                    Entry(2f, 3.8f),
                    Entry(3f, 4.5f),
                    Entry(4f, 4.2f),
                    Entry(5f, 4.7f),
                    Entry(6f, 4.3f)
                )
                
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
                
                data = LineData(dataSet)
                invalidate()
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun MealsBarChart() {
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(false)
                setDrawGridBackground(false)
                
                // X Axis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = android.graphics.Color.parseColor("#9E9E9E")
                    setDrawGridLines(false)
                    granularity = 1f
                    valueFormatter = IndexAxisValueFormatter(
                        arrayOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                    )
                }
                
                // Left Axis
                axisLeft.apply {
                    textColor = android.graphics.Color.parseColor("#9E9E9E")
                    setDrawGridLines(true)
                    gridColor = android.graphics.Color.parseColor("#2C2C2E")
                    axisMinimum = 0f
                }
                
                // Right Axis
                axisRight.isEnabled = false
                
                // Sample Data
                val entries = listOf(
                    BarEntry(0f, 3f),
                    BarEntry(1f, 4f),
                    BarEntry(2f, 3f),
                    BarEntry(3f, 5f),
                    BarEntry(4f, 4f),
                    BarEntry(5f, 6f),
                    BarEntry(6f, 3f)
                )
                
                val dataSet = BarDataSet(entries, "Meals").apply {
                    color = android.graphics.Color.parseColor("#FFD700")
                    valueTextColor = android.graphics.Color.parseColor("#FFFFFF")
                    valueTextSize = 10f
                }
                
                data = BarData(dataSet).apply {
                    barWidth = 0.6f
                }
                invalidate()
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun MoodPieChart() {
    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = true
                legend.textColor = android.graphics.Color.parseColor("#FFFFFF")
                legend.textSize = 12f
                setDrawEntryLabels(false)
                setUsePercentValues(true)
                
                // Sample Data
                val entries = listOf(
                    PieEntry(45f, "Vui vẻ 😊"),
                    PieEntry(30f, "Bình thường 😐"),
                    PieEntry(15f, "Tuyệt vời 🤩"),
                    PieEntry(10f, "Buồn 😢")
                )
                
                val dataSet = PieDataSet(entries, "").apply {
                    colors = listOf(
                        android.graphics.Color.parseColor("#A8D8A8"),
                        android.graphics.Color.parseColor("#FFD700"),
                        android.graphics.Color.parseColor("#90CAF9"),
                        android.graphics.Color.parseColor("#EF5350")
                    )
                    valueTextColor = android.graphics.Color.parseColor("#1C1C1E")
                    valueTextSize = 14f
                    sliceSpace = 2f
                }
                
                data = PieData(dataSet)
                invalidate()
            }
        },
        modifier = Modifier.fillMaxSize()
    )
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
