package com.haphuongquynh.foodmooddiary.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.FoodEntryViewModel
import com.haphuongquynh.foodmooddiary.util.WorkManagerHelper
import androidx.compose.ui.platform.LocalContext

/**
 * Profile & Settings Screen - Day 22
 * User profile, stats, and app settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    entryViewModel: FoodEntryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val workManagerHelper = remember { WorkManagerHelper(context) }
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val entries by entryViewModel.entries.collectAsStateWithLifecycle()
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    Surface(color = Color(0xFF1C1C1E)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Profile & Settings", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1C1C1E),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Profile Header
            currentUser?.let { user ->
                ProfileHeader(
                    userName = user.displayName ?: user.email,
                    userEmail = user.email,
                    totalEntries = entries.size,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Statistics Summary
            StatsSummarySection(
                totalEntries = entries.size,
                streak = 0, // TODO: Calculate actual streak
                modifier = Modifier.fillMaxWidth()
            )

            // Settings Section
            SettingsSection(
                onNotificationToggle = { enabled ->
                    workManagerHelper.setRemindersEnabled(enabled)
                },
                onThemeChange = { /* TODO */ },
                onExportData = { showExportDialog = true },
                onClearData = { showClearDataDialog = true },
                onLogout = { showLogoutDialog = true }
            )
        }

        // Dialogs
        if (showLogoutDialog) {
            LogoutConfirmDialog(
                onConfirm = {
                    authViewModel.logout()
                    showLogoutDialog = false
                    onNavigateToLogin()
                },
                onDismiss = { showLogoutDialog = false }
            )
        }

        if (showClearDataDialog) {
            ClearDataConfirmDialog(
                onConfirm = {
                    entries.forEach { entryViewModel.deleteEntry(it.id) }
                    showClearDataDialog = false
                },
                onDismiss = { showClearDataDialog = false }
            )
        }

        if (showExportDialog) {
            ExportDataDialog(
                onExportCSV = {
                    // TODO: Export as CSV
                    showExportDialog = false
                },
                onExportPDF = {
                    // TODO: Export as PDF
                    showExportDialog = false
                },
                onDismiss = { showExportDialog = false }
            )
        }
        }
    }
}

@Composable
fun ProfileHeader(
    userName: String,
    userEmail: String,
    totalEntries: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2E)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD700)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.Black
                )
            }

            // User Info
            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFA8A8A8)
            )

            // Total Entries Badge
            Surface(
                color = Color(0xFFFFD700),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "🔥 $totalEntries days in a row",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun StatsSummarySection(
    totalEntries: Int,
    streak: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(
                icon = Icons.Default.Restaurant,
                label = "Total Entries",
                value = totalEntries.toString(),
                color = MaterialTheme.colorScheme.primary
            )

            StatCard(
                icon = Icons.Default.LocalFireDepartment,
                label = "Streak",
                value = "$streak days",
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2E)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color(0xFFFFD700)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFA8A8A8)
            )
        }
    }
}

@Composable
fun SettingsSection(
    onNotificationToggle: (Boolean) -> Unit,
    onThemeChange: (String) -> Unit,
    onExportData: () -> Unit,
    onClearData: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf("System") }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2C2C2E)
            )
        ) {
            Column {
                // Notifications
                SettingItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Daily reminders",
                    trailing = {
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = {
                                notificationsEnabled = it
                                onNotificationToggle(it)
                            }
                        )
                    }
                )

                HorizontalDivider()

                // Theme
                SettingItem(
                    icon = Icons.Default.Palette,
                    title = "Theme",
                    subtitle = selectedTheme,
                    onClick = { /* TODO: Show theme picker */ }
                )

                HorizontalDivider()

                // Export Data
                SettingItem(
                    icon = Icons.Default.FileDownload,
                    title = "Export Data",
                    subtitle = "Save as CSV or PDF",
                    onClick = onExportData
                )

                HorizontalDivider()

                // Clear Data
                SettingItem(
                    icon = Icons.Default.DeleteForever,
                    title = "Clear All Data",
                    subtitle = "Delete all entries",
                    onClick = onClearData,
                    destructive = true
                )

                HorizontalDivider()

                // Logout
                SettingItem(
                    icon = Icons.Default.Logout,
                    title = "Log Out",
                    subtitle = "Sign out of your account",
                    onClick = onLogout,
                    destructive = true
                )
            }
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    destructive: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (destructive) Color(0xFFFF5252)
                  else Color(0xFFFFD700)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (destructive) Color(0xFFFF5252)
                       else Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFA8A8A8)
            )
        }

        trailing?.invoke()
    }
}

@Composable
fun LogoutConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Logout, null) },
        title = { Text("Log Out") },
        text = { Text("Are you sure you want to log out?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Log Out", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ClearDataConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
        title = { Text("Clear All Data") },
        text = { Text("This will permanently delete all your food entries. This action cannot be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete All", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ExportDataDialog(
    onExportCSV: () -> Unit,
    onExportPDF: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.FileDownload, null) },
        title = { Text("Export Data") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Choose export format:")
                TextButton(
                    onClick = onExportCSV,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export as CSV")
                }
                TextButton(
                    onClick = onExportPDF,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export as PDF")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
