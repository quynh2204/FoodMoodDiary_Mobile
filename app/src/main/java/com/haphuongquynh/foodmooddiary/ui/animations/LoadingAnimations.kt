package com.haphuongquynh.foodmooddiary.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Loading indicator with dots
 */
@Composable
fun LoadingDots(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    dotSize: Dp = 10.dp
) {
    val dots = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) }
    )

    dots.forEachIndexed { index, animatable ->
        LaunchedEffect(animatable) {
            delay(index * 100L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0f at 0 with LinearEasing
                        1f at 300 with LinearEasing
                        0f at 600 with LinearEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        dots.forEach { animatable ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .offset(y = (-20 * animatable.value).dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

/**
 * Success checkmark animation
 */
@Composable
fun SuccessAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    ScaleInOut(visible = visible, modifier = modifier) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .bounceAnimation(trigger = visible)
        ) {
            // Use Lottie animation here if needed
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Error animation
 */
@Composable
fun ErrorAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.shakeAnimation(trigger = visible)
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
    }
}

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

/**
 * Loading cards skeleton
 */
@Composable
fun LoadingCardsSkeleton(
    count: Int = 3,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(count) {
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }
    }
}
