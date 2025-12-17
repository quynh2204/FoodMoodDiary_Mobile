package com.haphuongquynh.foodmooddiary.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Animated visibility with fade and slide effects
 */
@Composable
fun FadeSlideInOut(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(300)) + 
                slideInVertically(animationSpec = tween(300)) { it / 2 },
        exit = fadeOut(animationSpec = tween(300)) + 
               slideOutVertically(animationSpec = tween(300)) { it / 2 },
        content = content
    )
}

/**
 * Scale animation for buttons and cards
 */
@Composable
fun ScaleInOut(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn() + scaleIn(
            initialScale = 0.8f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        exit = fadeOut() + scaleOut(targetScale = 0.8f),
        content = content
    )
}

/**
 * Shimmer loading effect
 */
@Composable
fun Modifier.shimmerEffect(): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    return this.then(
        Modifier.graphicsLayer {
            alpha = 0.3f + (0.7f * (translateAnim % 500f) / 500f)
        }
    )
}

/**
 * Pulse animation for notifications
 */
@Composable
fun Modifier.pulseAnimation(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Shake animation for errors
 */
@Composable
fun Modifier.shakeAnimation(trigger: Boolean): Modifier {
    var startShake by remember { mutableStateOf(false) }
    
    LaunchedEffect(trigger) {
        if (trigger) {
            startShake = true
            kotlinx.coroutines.delay(500)
            startShake = false
        }
    }
    
    val transition = updateTransition(targetState = startShake, label = "shake")
    val offsetX by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                keyframes {
                    durationMillis = 500
                    0f at 0
                    -10f at 50
                    10f at 100
                    -10f at 150
                    10f at 200
                    -5f at 250
                    5f at 300
                    0f at 350
                }
            } else {
                tween(0)
            }
        },
        label = "shake"
    ) { if (it) 1f else 0f }
    
    return this.graphicsLayer {
        translationX = if (startShake) offsetX * 10f else 0f
    }
}

/**
 * Bounce animation for success states
 */
@Composable
fun Modifier.bounceAnimation(trigger: Boolean): Modifier {
    var startBounce by remember { mutableStateOf(false) }
    
    LaunchedEffect(trigger) {
        if (trigger) {
            startBounce = true
            kotlinx.coroutines.delay(600)
            startBounce = false
        }
    }
    
    val scale by animateFloatAsState(
        targetValue = if (startBounce) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bounce"
    )
    
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Slide in from side animation
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SlideInFromSide(
    visible: Boolean,
    fromLeft: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInHorizontally(
            initialOffsetX = { if (fromLeft) -it else it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = slideOutHorizontally(
            targetOffsetX = { if (fromLeft) -it else it }
        ) + fadeOut(),
        content = content
    )
}

/**
 * Expand vertically animation
 */
@Composable
fun ExpandVertically(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = expandVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        content = content
    )
}

/**
 * Rotate animation
 */
@Composable
fun Modifier.rotateAnimation(rotating: Boolean): Modifier {
    val rotation by animateFloatAsState(
        targetValue = if (rotating) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )
    
    return this.graphicsLayer {
        rotationZ = if (rotating) rotation else 0f
    }
}
