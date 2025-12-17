package com.haphuongquynh.foodmooddiary.presentation.screens.statistics

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.haphuongquynh.foodmooddiary.domain.model.FoodFrequency

/**
 * Bar Chart for Top Foods
 * Shows food frequency with mood score coloring
 */
@Composable
fun TopFoodsChart(
    data: List<FoodFrequency>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        EmptyChartState(message = "No food data yet", modifier = modifier)
        return
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp),
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                setDrawGridBackground(false)
                animateY(1000)
                setFitBars(true)

                // X Axis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    textColor = Color.GRAY
                    labelRotationAngle = -45f
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return if (index in data.indices) {
                                data[index].foodName.take(10) // Limit length
                            } else ""
                        }
                    }
                }

                // Y Axis (Left)
                axisLeft.apply {
                    textColor = Color.GRAY
                    axisMinimum = 0f
                    granularity = 1f
                    setDrawGridLines(true)
                }

                // Y Axis (Right)
                axisRight.isEnabled = false

                // Legend
                legend.textColor = Color.GRAY
            }
        },
        update = { chart ->
            val entries = data.mapIndexed { index, food ->
                BarEntry(index.toFloat(), food.count.toFloat())
            }

            val dataSet = BarDataSet(entries, "Food Frequency").apply {
                // Color by mood score
                colors = data.map { food ->
                    when {
                        food.averageMoodScore >= 7f -> Color.parseColor("#4CAF50") // Green (Happy)
                        food.averageMoodScore >= 5f -> Color.parseColor("#FFC107") // Yellow (Neutral)
                        else -> Color.parseColor("#F44336") // Red (Sad)
                    }
                }
                valueTextColor = Color.GRAY
                valueTextSize = 10f
            }

            chart.data = BarData(dataSet)
            chart.invalidate()
        }
    )
}
