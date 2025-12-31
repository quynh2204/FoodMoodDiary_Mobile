package com.haphuongquynh.foodmooddiary.presentation.screens.entry

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.haphuongquynh.foodmooddiary.ui.theme.*
import com.haphuongquynh.foodmooddiary.presentation.screens.camera.CameraScreen
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.EntryState
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.FoodEntryViewModel
import com.haphuongquynh.foodmooddiary.util.color.ColorAnalyzer
import java.io.File

/**
 * Add Entry Screen - 2 Step Flow with Full Backend Integration
 * Step 1: Photo + Caption with Continue button
 * Step 2: Clean form with all features (Mood, Date/Time, Location, Meal Type, Rating, Notes)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryScreen(
    navController: NavController,
    viewModel: FoodEntryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(0) }
    var photoCaption by remember { mutableStateOf("") }
    var foodName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showCamera by remember { mutableStateOf(false) }
    var showPhotoSourceDialog by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf("😊") }
    var selectedMealType by remember { mutableStateOf("Dinner") }
    var rating by remember { mutableStateOf(0) }
    var selectedColor by remember { mutableStateOf(android.graphics.Color.parseColor("#FFA726")) }
    
    val photoData by viewModel.currentPhoto.collectAsState()
    val location by viewModel.currentLocation.collectAsState()
    val entryState by viewModel.entryState.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                
                val tempFile = File(context.cacheDir, "gallery_${System.currentTimeMillis()}.jpg")
                tempFile.outputStream().use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                
                viewModel.processPhoto(tempFile, bitmap)
                photoData?.let { selectedColor = it.dominantColor }
                currentStep = 0
            } catch (e: Exception) {
                android.util.Log.e("AddEntry", "Gallery error", e)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCurrentLocation()
        if (photoData == null && currentStep == 0) {
            showPhotoSourceDialog = true
        }
    }

    LaunchedEffect(entryState) {
        when (entryState) {
            is EntryState.Success -> {
                navController.navigateUp()
                viewModel.resetEntryState()
            }
            else -> {}
        }
    }

    if (showCamera) {
        CameraScreen(
            onPhotoCaptured = { file, bitmap ->
                viewModel.processPhoto(file, bitmap)
                photoData?.let { selectedColor = it.dominantColor }
                showCamera = false
                currentStep = 0
            },
            onDismiss = { showCamera = false }
        )
    } else {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BlackPrimary
        ) {
            when (currentStep) {
                0 -> PhotoCaptionStep(
                    photoData = photoData,
                    onChangePhoto = { showPhotoSourceDialog = true },
                    onContinue = { 
                        if (photoData != null) currentStep = 1
                    },
                    onBack = { navController.navigateUp() }
                )
                1 -> EntryFormStep(
                    photoData = photoData,
                    photoCaption = photoCaption,
                    foodName = foodName,
                    onFoodNameChange = { foodName = it },
                    notes = notes,
                    onNotesChange = { notes = it },
                    selectedMood = selectedMood,
                    onMoodSelect = { selectedMood = it },
                    selectedMealType = selectedMealType,
                    onMealTypeSelect = { selectedMealType = it },
                    rating = rating,
                    onRatingChange = { rating = it },
                    location = location,
                    onChangePhoto = { showPhotoSourceDialog = true },
                    onSave = {
                        val combinedNotes = listOfNotNull(
                            photoCaption.takeIf { it.isNotBlank() },
                            notes.takeIf { it.isNotBlank() }
                        ).joinToString("\n")
                        
                        // Calculate mood color from selected emoji
                        val moodColor = ColorAnalyzer().getMoodColor(selectedMood)
                        
                        viewModel.addEntry(
                            foodName = foodName.ifBlank { "Unnamed" },
                            moodColor = moodColor,
                            mood = selectedMood,
                            notes = combinedNotes,
                            mealType = selectedMealType,
                            rating = rating
                        )
                    },
                    onCancel = { navController.navigateUp() },
                    onBack = { currentStep = 0 },
                    isLoading = entryState is EntryState.Loading
                )
            }

            if (showPhotoSourceDialog) {
                AlertDialog(
                    onDismissRequest = { 
                        showPhotoSourceDialog = false
                        if (photoData == null && currentStep == 0) {
                            navController.navigateUp()
                        }
                    },
                    containerColor = BlackSecondary,
                    title = { Text("Choose Photo", color = WhiteText, fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    showPhotoSourceDialog = false
                                    showCamera = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = PastelGreen)
                            ) {
                                Icon(Icons.Default.CameraAlt, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Take Photo", color = BlackPrimary)
                            }
                            Button(
                                onClick = {
                                    showPhotoSourceDialog = false
                                    galleryLauncher.launch("image/*")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = BlackTertiary)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, null, tint = WhiteText)
                                Spacer(Modifier.width(8.dp))
                                Text("Choose from Gallery", color = WhiteText)
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        TextButton(onClick = { 
                            showPhotoSourceDialog = false
                            if (photoData == null && currentStep == 0) {
                                navController.navigateUp()
                            }
                        }) {
                            Text("Cancel", color = PastelGreen)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoCaptionStep(
    photoData: com.haphuongquynh.foodmooddiary.presentation.viewmodel.PhotoData?,
    onChangePhoto: () -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = BlackPrimary,
        topBar = {
            TopAppBar(
                title = { Text("Add Entry", color = WhiteText, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = WhiteText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackPrimary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f),
                shape = RoundedCornerShape(24.dp),
                color = BlackSecondary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    photoData?.bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Icon(
                        Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = GrayText
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PastelGreen),
                shape = RoundedCornerShape(28.dp),
                enabled = photoData != null
            ) {
                Text("Continue →", color = BlackPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryFormStep(
    photoData: com.haphuongquynh.foodmooddiary.presentation.viewmodel.PhotoData?,
    photoCaption: String,
    foodName: String,
    onFoodNameChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    selectedMood: String,
    onMoodSelect: (String) -> Unit,
    selectedMealType: String,
    onMealTypeSelect: (String) -> Unit,
    rating: Int,
    onRatingChange: (Int) -> Unit,
    location: com.haphuongquynh.foodmooddiary.domain.model.Location?,
    onChangePhoto: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean
) {
    val moods = listOf("😊", "😌", "😔", "😫", "🎉", "💪")
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = BlackPrimary,
        topBar = {
            TopAppBar(
                title = { Text("Add Entry", color = WhiteText, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = WhiteText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackPrimary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = BlackSecondary
                ) {
                    photoData?.bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                
                TextButton(onClick = onChangePhoto) {
                    Text("Change photo", color = PastelGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text("Food Name", color = WhiteText, fontSize = 14.sp)
            TextField(
                value = foodName,
                onValueChange = onFoodNameChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Bánh kem dâu Giáng sinh đáng yêu", color = GrayText) },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = PastelGreenVeryLight,
                    unfocusedContainerColor = PastelGreenVeryLight,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = DarkText,
                    unfocusedTextColor = DarkText
                )
            )

            Text("Mood", color = WhiteText, fontSize = 14.sp)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PastelGreenVeryLight
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    moods.forEach { mood ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (mood == selectedMood) PastelGreenDark else Color.Transparent,
                            modifier = Modifier.clickable { onMoodSelect(mood) }
                        ) {
                            Text(
                                mood,
                                fontSize = 32.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Date & Time", color = WhiteText, fontSize = 14.sp)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PastelGreenVeryLight
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(
                                getCurrentDateTime(), 
                                color = DarkText, 
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text("Location", color = WhiteText, fontSize = 14.sp)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PastelGreenVeryLight
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(
                                location?.address?.take(25) ?: "Fetching...", 
                                color = DarkText, 
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Text("Meal Type", color = WhiteText, fontSize = 14.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                mealTypes.forEach { type ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onMealTypeSelect(type) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedMealType == type) PastelGreenDark else PastelGreenVeryLight
                    ) {
                        Box(modifier = Modifier.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                            Text(
                                type,
                                color = if (selectedMealType == type) WhiteText else DarkText,
                                fontSize = 12.sp,
                                fontWeight = if (selectedMealType == type) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Notes", color = WhiteText, fontSize = 14.sp)
                    TextField(
                        value = notes,
                        onValueChange = onNotesChange,
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("Write something...", color = GrayText) },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = PastelGreenVeryLight,
                            unfocusedContainerColor = PastelGreenVeryLight,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = DarkText,
                            unfocusedTextColor = DarkText
                        )
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text("Rating", color = WhiteText, fontSize = 14.sp)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PastelGreenVeryLight
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.Center) {
                                repeat(5) { index ->
                                    Icon(
                                        if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp).clickable { onRatingChange(index + 1) },
                                        tint = PastelGreen
                                    )
                                }
                            }
                            
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(containerColor = PastelGreenDark),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Gợi ý AI 🧠", color = WhiteText, fontSize = 10.sp)
                            }
                            
                            if (rating > 0) {
                                Text("Bánh màu đỏ → Happy", color = DarkText, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PastelGreen),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !isLoading && foodName.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = BlackPrimary)
                    } else {
                        Text("Save", color = BlackPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WhiteText),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !isLoading
                ) {
                    Text("Cancel", color = BlackPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Get current date and time formatted
 */
private fun getCurrentDateTime(): String {
    val sdf = java.text.SimpleDateFormat("dd MMM yyyy - hh:mm a", java.util.Locale.getDefault())
    return sdf.format(java.util.Date())
}
