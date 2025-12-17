package com.haphuongquynh.foodmooddiary.presentation.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.haphuongquynh.foodmooddiary.R
import com.haphuongquynh.foodmooddiary.presentation.navigation.Screen
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val alpha = remember { Animatable(0f) }
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(key1 = true) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500)
        )
        delay(2000)
        
        // Check authentication state
        val destination = if (currentUser != null) {
            Screen.Home.route
        } else {
            Screen.Login.route
        }
        
        navController.navigate(destination) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Replace with actual app logo
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .alpha(alpha.value)
            )
        }
    }
}
