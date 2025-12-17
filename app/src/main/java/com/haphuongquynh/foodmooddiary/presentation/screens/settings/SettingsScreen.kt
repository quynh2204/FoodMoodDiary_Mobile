package com.haphuongquynh.foodmooddiary.presentation.screens.settings

import android.hardware.Sensor
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.SensorViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.ThemeMode
import com.haphuongquynh.foodmooddiary.util.hasSensorSupport

/**
 * Settings Screen for sensor and app configuration
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SensorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val shakeEnabled by viewModel.shakeEnabled.collectAsState()
    val adaptiveThemeEnabled by viewModel.adaptiveThemeEnabled.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()
    
    val hasAccelerometer = remember { hasSensorSupport(context, Sensor.TYPE_ACCELEROMETER) }
    val hasLightSensor = remember { hasSensorSupport(context, Sensor.TYPE_LIGHT) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sensor Settings Section
            Text(
                "Sensor Settings",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Shake Detection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Vibration,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Shake to Add Entry",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                if (hasAccelerometer) "Shake your device to quickly add a food entry"
                                else "Accelerometer not available",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = shakeEnabled && hasAccelerometer,
                            onCheckedChange = { viewModel.setShakeEnabled(it) },
                            enabled = hasAccelerometer
                        )
                    }
                    
                    Divider()
                    
                    // Adaptive Theme
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Brightness4,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Adaptive Theme",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                if (hasLightSensor) "Adjust theme based on ambient light"
                                else "Light sensor not available",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = adaptiveThemeEnabled && hasLightSensor,
                            onCheckedChange = { viewModel.setAdaptiveThemeEnabled(it) },
                            enabled = hasLightSensor
                        )
                    }
                }
            }
            
            // Theme Settings Section
            Text(
                "Theme Settings",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeOption(
                        icon = Icons.Default.LightMode,
                        label = "Light Mode",
                        selected = currentTheme == ThemeMode.LIGHT,
                        onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) }
                    )
                    
                    ThemeOption(
                        icon = Icons.Default.DarkMode,
                        label = "Dark Mode",
                        selected = currentTheme == ThemeMode.DARK,
                        onClick = { viewModel.setThemeMode(ThemeMode.DARK) }
                    )
                    
                    ThemeOption(
                        icon = Icons.Default.SettingsBrightness,
                        label = "System Default",
                        selected = currentTheme == ThemeMode.SYSTEM,
                        onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) }
                    )
                    
                    if (hasLightSensor) {
                        ThemeOption(
                            icon = Icons.Default.Brightness4,
                            label = "Adaptive (Light Sensor)",
                            selected = currentTheme == ThemeMode.ADAPTIVE,
                            onClick = { viewModel.setThemeMode(ThemeMode.ADAPTIVE) },
                            enabled = hasLightSensor
                        )
                    }
                }
            }
            
            // Sensor Status Section
            Text(
                "Sensor Status",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SensorStatusItem(
                        icon = Icons.Default.Vibration,
                        label = "Accelerometer",
                        available = hasAccelerometer
                    )
                    
                    Divider()
                    
                    SensorStatusItem(
                        icon = Icons.Default.Brightness4,
                        label = "Light Sensor",
                        available = hasLightSensor
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer 
                else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                      else MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.width(16.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                       else MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.weight(1f))
            if (selected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun SensorStatusItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    available: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (available) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(16.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        
        if (available) {
            AssistChip(
                onClick = {},
                label = { Text("Available") },
                leadingIcon = {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        } else {
            AssistChip(
                onClick = {},
                label = { Text("Not Available") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    labelColor = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        }
    }
}
