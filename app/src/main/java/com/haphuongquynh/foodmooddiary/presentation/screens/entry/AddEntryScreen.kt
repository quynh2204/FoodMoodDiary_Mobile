package com.haphuongquynh.foodmooddiary.presentation.screens.entry

import android.graphics.Bitmap
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.haphuongquynh.foodmooddiary.presentation.screens.camera.CameraScreen
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.EntryState
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.FoodEntryViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryScreen(
    navController: NavController,
    viewModel: FoodEntryViewModel = hiltViewModel()
) {
    var foodName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showCamera by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(android.graphics.Color.parseColor("#4CAF50")) }
    
    val photoData by viewModel.currentPhoto.collectAsState()
    val location by viewModel.currentLocation.collectAsState()
    val entryState by viewModel.entryState.collectAsState()
    val scrollState = rememberScrollState()

    // Fetch location on screen load
    LaunchedEffect(Unit) {
        viewModel.fetchCurrentLocation()
    }

    // Handle entry state
    LaunchedEffect(entryState) {
        when (entryState) {
            is EntryState.Success -> {
                navController.navigateUp()
                viewModel.resetEntryState()
            }
            else -> {}
        }
    }

    // Show camera if requested
    if (showCamera) {
        CameraScreen(
            onPhotoCaptured = { file, bitmap ->
                viewModel.processPhoto(file, bitmap)
                // Use dominant color from photo
                photoData?.let { selectedColor = it.dominantColor }
                showCamera = false
            },
            onDismiss = { showCamera = false }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Food Entry") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.addEntry(
                        foodName = foodName,
                        notes = notes,
                        moodColor = selectedColor,
                        photoFile = photoData?.file
                    )
                },
                icon = { Icon(Icons.Default.Check, "Save") },
                text = { Text("Save Entry") },
                expanded = scrollState.value == 0
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { showCamera = true },
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoData != null) {
                        Image(
                            bitmap = photoData!!.bitmap.asImageBitmap(),
                            contentDescription = "Food Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Tap to take photo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Food Name Field
            OutlinedTextField(
                value = foodName,
                onValueChange = { foodName = it },
                label = { Text("Food Name *") },
                leadingIcon = { Icon(Icons.Default.Restaurant, "Food") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Notes Field
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                leadingIcon = { Icon(Icons.Default.Notes, "Notes") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            // Mood Color Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Palette, "Color")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Mood Color",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(selectedColor))
                                .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        )
                    }

                    if (photoData != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Color extracted from photo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showColorPicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Choose Custom Color")
                    }
                }
            }

            // Location Section
            if (location != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, "Location")
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "Location",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                location!!.address ?: "${location!!.latitude}, ${location!!.longitude}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Loading indicator
            if (entryState is EntryState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            // Error message
            if (entryState is EntryState.Error) {
                Text(
                    text = (entryState as EntryState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Bottom spacing for FAB
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Color Picker Dialog
    if (showColorPicker) {
        ColorPickerDialog(
            currentColor = selectedColor,
            onColorSelected = { color ->
                selectedColor = color
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }
}

@Composable
fun ColorPickerDialog(
    currentColor: Int,
    onColorSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val predefinedColors = listOf(
        android.graphics.Color.parseColor("#F44336"), // Red
        android.graphics.Color.parseColor("#E91E63"), // Pink
        android.graphics.Color.parseColor("#9C27B0"), // Purple
        android.graphics.Color.parseColor("#673AB7"), // Deep Purple
        android.graphics.Color.parseColor("#3F51B5"), // Indigo
        android.graphics.Color.parseColor("#2196F3"), // Blue
        android.graphics.Color.parseColor("#03A9F4"), // Light Blue
        android.graphics.Color.parseColor("#00BCD4"), // Cyan
        android.graphics.Color.parseColor("#009688"), // Teal
        android.graphics.Color.parseColor("#4CAF50"), // Green
        android.graphics.Color.parseColor("#8BC34A"), // Light Green
        android.graphics.Color.parseColor("#CDDC39"), // Lime
        android.graphics.Color.parseColor("#FFEB3B"), // Yellow
        android.graphics.Color.parseColor("#FFC107"), // Amber
        android.graphics.Color.parseColor("#FF9800"), // Orange
        android.graphics.Color.parseColor("#FF5722"), // Deep Orange
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Mood Color") },
        text = {
            Column {
                predefinedColors.chunked(4).forEach { rowColors ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowColors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(Color(color))
                                    .border(
                                        width = if (color == currentColor) 3.dp else 1.dp,
                                        color = if (color == currentColor)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.outline,
                                        shape = CircleShape
                                    )
                                    .clickable { onColorSelected(color) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
