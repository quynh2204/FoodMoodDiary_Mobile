package com.haphuongquynh.foodmooddiary.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.haphuongquynh.foodmooddiary.presentation.navigation.Screen
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthState
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Handle logout
    LaunchedEffect(authState) {
        if (authState is AuthState.LoggedOut) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
            viewModel.resetAuthState()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.BarChart, null) },
                    label = { Text("Statistics") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Statistics.route)
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Map, null) },
                    label = { Text("Map") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Map.route)
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Profile.route)
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Restaurant, null) },
                    label = { Text("Discover Meals") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Discovery.route)
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("FoodMoodDiary") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            com.haphuongquynh.foodmooddiary.presentation.screens.entry.EntryListScreen(
                navController = navController
            )
        }
    }
}
