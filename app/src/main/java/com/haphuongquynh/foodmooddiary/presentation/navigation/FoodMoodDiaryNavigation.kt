package com.haphuongquynh.foodmooddiary.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.haphuongquynh.foodmooddiary.presentation.screens.splash.SplashScreen

@Composable
fun FoodMoodDiaryNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        // Authentication Flow
        composable(route = Screen.Login.route) {
            com.haphuongquynh.foodmooddiary.presentation.screens.auth.LoginScreen(navController = navController)
        }

        composable(route = Screen.Register.route) {
            com.haphuongquynh.foodmooddiary.presentation.screens.auth.RegisterScreen(navController = navController)
        }

        composable(route = Screen.NewPassword.route) {
            com.haphuongquynh.foodmooddiary.presentation.screens.auth.NewPasswordScreen(navController = navController)
        }

        // Main Flow
        composable(route = Screen.Main.route) {
            com.haphuongquynh.foodmooddiary.presentation.screens.main.MainScreen(navController = navController)
        }

        composable(route = Screen.Home.route) {
            com.haphuongquynh.foodmooddiary.presentation.screens.home.SimpleHomeScreen(
                navController = navController
            )
        }

        composable(route = Screen.AddEntry.route) {
            com.haphuongquynh.foodmooddiary.presentation.screens.entry.AddEntryScreen(navController = navController)
        }

        composable(
            route = Screen.EntryDetail.route,
            arguments = listOf(navArgument("entryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getString("entryId") ?: ""
            com.haphuongquynh.foodmooddiary.presentation.screens.detail.EntryDetailScreen(
                entryId = entryId,
                navController = navController
            )
        }

        composable(
            route = Screen.EditEntry.route,
            arguments = listOf(navArgument("entryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getString("entryId")
            // EditEntryScreen(navController = navController, entryId = entryId)
        }

        composable(route = Screen.Statistics.route) {
            com.haphuongquynh.foodmooddiary.presentation.screens.statistics.StatisticsScreen()
        }

        composable(route = Screen.Map.route) {
            com.haphuongquynh.foodmooddiary.presentation.screens.map.MapScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(route = Screen.Profile.route) {
            com.haphuongquynh.foodmooddiary.presentation.screens.profile.ModernProfileScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Settings.route) {
            com.haphuongquynh.foodmooddiary.presentation.screens.settings.SettingsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(route = Screen.Discovery.route) {
            com.haphuongquynh.foodmooddiary.presentation.screens.discovery.DiscoveryScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        composable(route = Screen.Camera.route) {
            com.haphuongquynh.foodmooddiary.presentation.screens.camera.CameraScreen(
                onPhotoCaptured = { file, bitmap ->
                    navController.navigate(Screen.AddEntry.route)
                },
                onDismiss = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object NewPassword : Screen("new_password")
    data object Main : Screen("main")
    data object Home : Screen("home")
    data object AddEntry : Screen("add_entry")
    data object EntryDetail : Screen("entry_detail/{entryId}") {
        fun createRoute(entryId: String) = "entry_detail/$entryId"
    }
    data object EditEntry : Screen("edit_entry/{entryId}") {
        fun createRoute(entryId: String) = "edit_entry/$entryId"
    }
    data object Statistics : Screen("statistics")
    data object Map : Screen("map")
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
    data object Discovery : Screen("discovery")
    data object Camera : Screen("camera")
}
