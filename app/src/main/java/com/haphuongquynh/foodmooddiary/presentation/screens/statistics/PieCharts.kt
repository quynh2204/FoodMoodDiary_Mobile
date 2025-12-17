package com.haphuongquynh.foodmooddiary.presentation.screens.statistics

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.haphuongquynh.foodmooddiary.domain.model.ColorDistribution
import com.haphuongquynh.foodmooddiary.domain.model.MealDistribution

/**
 * Pie Chart for Meal Type Distribution
 */
@Composable
fun MealDistributionChart(
    data: List<MealDistribution>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        EmptyChartState(message = "No meal data yet", modifier = modifier)
        return
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                setUsePercentValues(true)
                setDrawHoleEnabled(true)
                setHoleColor(Color.TRANSPARENT)
                setTransparentCircleRadius(58f)
                setDrawCenterText(true)
                centerText = "Meal Types"
                setCenterTextSize(16f)
                setCenterTextColor(Color.GRAY)
                animateY(1000, Easing.EaseInOutQuad)

                legend.apply {
                    textColor = Color.GRAY
                    textSize = 12f
                }
            }
        },
        update = { chart ->
            val entries = data.map { meal ->
                PieEntry(meal.percentage, meal.mealType.name.lowercase().replaceFirstChar { it.uppercase() })
            }

            val colors = listOf(
                Color.parseColor("#FF9800"), // Orange - Breakfast
                Color.parseColor("#4CAF50"), // Green - Lunch  
                Color.parseColor("#2196F3"), // Blue - Dinner
                Color.parseColor("#9C27B0")  // Purple - Snack
            )

            val dataSet = PieDataSet(entries, "").apply {
                setColors(colors)
                sliceSpace = 2f
                selectionShift = 5f
                valueTextColor = Color.WHITE
                valueTextSize = 12f
                valueFormatter = PercentFormatter(chart)
            }

            chart.data = PieData(dataSet)
            chart.invalidate()
        }
    )
}

/**
 * Pie Chart for Color Distribution
 */
@Composable
fun ColorDistributionChart(
    data: List<ColorDistribution>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        EmptyChartState(message = "No color data yet", modifier = modifier)
        return
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                setUsePercentValues(true)
                setDrawHoleEnabled(true)
                setHoleColor(Color.TRANSPARENT)
                setTransparentCircleRadius(58f)
                setDrawCenterText(true)
                centerText = "Color Palette"
                setCenterTextSize(16f)
                setCenterTextColor(Color.GRAY)
                animateY(1000, Easing.EaseInOutQuad)

                legend.apply {
                    textColor = Color.GRAY
                    textSize = 12f
                }
            }
        },
        update = { chart ->
            val entries = data.map { color ->
                PieEntry(color.percentage, color.colorName)
            }

            val dataSet = PieDataSet(entries, "").apply {
                colors = data.map { it.colorValue }
                sliceSpace = 2f
                selectionShift = 5f
                valueTextColor = Color.WHITE
                valueTextSize = 12f
                valueFormatter = PercentFormatter(chart)
            }

            chart.data = PieData(dataSet)
            chart.invalidate()
        }
    )
}
