package com.haphuongquynh.foodmooddiary.presentation.screens.statistics

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.haphuongquynh.foodmooddiary.domain.model.MoodTrendPoint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Line Chart for Mood Trend
 * Shows mood score over time (7 days, 30 days, etc.)
 */
@Composable
fun MoodTrendChart(
    data: List<MoodTrendPoint>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        EmptyChartState(message = "No mood data yet", modifier = modifier)
        return
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                setDrawGridBackground(false)
                animateX(1000)

                // X Axis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    textColor = Color.GRAY
                    valueFormatter = object : ValueFormatter() {
                        private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                        override fun getFormattedValue(value: Float): String {
                            return dateFormat.format(Date(value.toLong()))
                        }
                    }
                }

                // Y Axis (Left)
                axisLeft.apply {
                    textColor = Color.GRAY
                    axisMinimum = 0f
                    axisMaximum = 10f
                    setDrawGridLines(true)
                }

                // Y Axis (Right)
                axisRight.isEnabled = false

                // Legend
                legend.textColor = Color.GRAY
            }
        },
        update = { chart ->
            val entries = data.map { point ->
                Entry(point.date.toFloat(), point.averageMoodScore)
            }

            val dataSet = LineDataSet(entries, "Mood Score").apply {
                color = Color.parseColor("#4CAF50")
                setCircleColor(Color.parseColor("#4CAF50"))
                lineWidth = 2.5f
                circleRadius = 4f
                setDrawCircleHole(true)
                setDrawValues(true)
                valueTextColor = Color.GRAY
                valueTextSize = 10f
                mode = LineDataSet.Mode.CUBIC_BEZIER // Smooth curve
                setDrawFilled(true)
                fillColor = Color.parseColor("#4CAF50")
                fillAlpha = 50
            }

            chart.data = LineData(dataSet)
            chart.invalidate() // Refresh
        }
    )
}

@Composable
fun EmptyChartState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
