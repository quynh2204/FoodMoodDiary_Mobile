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
import kotlinx.coroutines.launch
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
    preselectedMood: String? = null,
    initialPhotoPath: String? = null,
    viewModel: FoodEntryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(0) }
    var photoCaption by remember { mutableStateOf("") }
    var foodName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showCamera by remember { mutableStateOf(false) }
    
    // Map preselected mood from text to emoji
    val initialMood = when (preselectedMood?.lowercase()) {
        "sad" -> "😢"
        "meh" -> "😔"
        "okay" -> "😐"
        "good" -> "😊"
        "great" -> "🥰"
        else -> "😊"
    }
    var selectedMood by remember { mutableStateOf(initialMood) }
    var selectedMealType by remember { mutableStateOf("Dinner") }
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

    // Load ảnh từ photoPath nếu có (từ MainScreen camera)
    LaunchedEffect(initialPhotoPath) {
        if (!initialPhotoPath.isNullOrEmpty() && photoData == null) {
            try {
                val decodedPath = java.net.URLDecoder.decode(initialPhotoPath, "UTF-8")
                val file = File(decodedPath)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(decodedPath)
                    viewModel.processPhoto(file, bitmap)
                }
            } catch (e: Exception) {
                android.util.Log.e("AddEntry", "Error loading photo from path", e)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCurrentLocation()
        // Chỉ mở camera nếu chưa có ảnh VÀ không có photoPath từ MainScreen
        if (photoData == null && currentStep == 0 && initialPhotoPath.isNullOrEmpty()) {
            showCamera = true
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
            onDismiss = { 
                showCamera = false
                if (photoData == null) {
                    navController.navigateUp()
                }
            },
            onGalleryClick = {
                showCamera = false
                galleryLauncher.launch("image/*")
            }
        )
    } else {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BlackPrimary
        ) {
            when (currentStep) {
                0 -> PhotoCaptionStep(
                    photoData = photoData,
                    onChangePhoto = { showCamera = true },
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
                    location = location,
                    onChangePhoto = { showCamera = true },
                    onSave = {
                        val combinedNotes = listOfNotNull(
                            photoCaption.takeIf { it.isNotBlank() },
                            notes.takeIf { it.isNotBlank() }
                        ).joinToString("\n")
                        
                        viewModel.addEntry(
                            foodName = foodName.ifBlank { "Unnamed" },
                            moodColor = selectedColor,
                            mood = selectedMood,
                            notes = combinedNotes,
                            mealType = selectedMealType
                        )
                    },
                    onCancel = { navController.navigateUp() },
                    onBack = { currentStep = 0 },
                    isLoading = entryState is EntryState.Loading
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
                Text("Tiếp tục →", color = BlackPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
    location: com.haphuongquynh.foodmooddiary.domain.model.Location?,
    onChangePhoto: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean
) {
    // 5 Core Moods: Happy, Sad, Angry, Tired, Energetic
    val moods = listOf(
        "😊" to "Vui vẻ",
        "😢" to "Buồn",
        "😠" to "Tức giận",
        "😫" to "Mệt mỏi",
        "💪" to "Năng lượng"
    )
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = BlackPrimary,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    Text("Đổi ảnh", color = PastelGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text("Tên món ăn", color = WhiteText, fontSize = 14.sp)
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

            Text("Cảm xúc", color = WhiteText, fontSize = 14.sp)
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
                    moods.forEach { (emoji, label) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onMoodSelect(emoji) }
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (emoji == selectedMood) PastelGreenDark else Color.Transparent
                            ) {
                                Text(
                                    emoji,
                                    fontSize = 28.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Text(
                                label,
                                fontSize = 10.sp,
                                color = DarkText
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
                    Text("Ngày & Giờ", color = WhiteText, fontSize = 14.sp)
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
                    Text("Vị trí", color = WhiteText, fontSize = 14.sp)
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
                    Text("Ghi chú", color = WhiteText, fontSize = 14.sp)
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
                
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        when {
                            foodName.isBlank() -> {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Vui lòng nhập tên món ăn",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                            foodName.length < 2 -> {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Tên món ăn phải có ít nhất 2 ký tự",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                            else -> onSave()
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PastelGreen),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !isLoading
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
                    Text("Hủy", color = BlackPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
