package com.haphuongquynh.foodmooddiary.presentation.screens.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.haphuongquynh.foodmooddiary.domain.model.Insight
import com.haphuongquynh.foodmooddiary.domain.model.InsightType

/**
 * Insights Tab
 * Displays AI-generated insights with actionable recommendations
 */
@Composable
fun InsightsTab(
    insights: List<Insight>,
    modifier: Modifier = Modifier
) {
    if (insights.isEmpty()) {
        EmptyInsightsState(modifier = modifier)
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(insights) { insight ->
            InsightCard(insight = insight)
        }
    }
}

@Composable
fun InsightCard(
    insight: Insight,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (insight.type) {
                InsightType.MOOD_PATTERN -> MaterialTheme.colorScheme.primaryContainer
                InsightType.FOOD_CORRELATION -> MaterialTheme.colorScheme.secondaryContainer
                InsightType.TIME_PATTERN -> MaterialTheme.colorScheme.tertiaryContainer
                InsightType.COLOR_PATTERN -> MaterialTheme.colorScheme.surfaceVariant
                InsightType.STREAK -> MaterialTheme.colorScheme.primaryContainer
                InsightType.RECOMMENDATION -> MaterialTheme.colorScheme.secondaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Icon(
                imageVector = getInsightIcon(insight.type),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = when (insight.type) {
                    InsightType.MOOD_PATTERN -> MaterialTheme.colorScheme.primary
                    InsightType.FOOD_CORRELATION -> MaterialTheme.colorScheme.secondary
                    InsightType.TIME_PATTERN -> MaterialTheme.colorScheme.tertiary
                    InsightType.COLOR_PATTERN -> MaterialTheme.colorScheme.onSurfaceVariant
                    InsightType.STREAK -> MaterialTheme.colorScheme.primary
                    InsightType.RECOMMENDATION -> MaterialTheme.colorScheme.secondary
                }
            )

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Description
                Text(
                    text = insight.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Actionable recommendation
                if (insight.actionable) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = "Tap to learn more",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyInsightsState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "No insights available yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Keep logging your meals to get personalized insights!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getInsightIcon(type: InsightType): ImageVector {
    return when (type) {
        InsightType.MOOD_PATTERN -> Icons.Default.SentimentSatisfied
        InsightType.FOOD_CORRELATION -> Icons.Default.Restaurant
        InsightType.TIME_PATTERN -> Icons.Default.Schedule
        InsightType.COLOR_PATTERN -> Icons.Default.Palette
        InsightType.STREAK -> Icons.Default.LocalFireDepartment
        InsightType.RECOMMENDATION -> Icons.Default.Lightbulb
    }
}
