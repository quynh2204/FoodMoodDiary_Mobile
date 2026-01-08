package com.haphuongquynh.foodmooddiary.ui.animations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Skeleton loading placeholder
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Box(
        modifier = modifier
            .background(color)
            .shimmerEffect()
    )
}


