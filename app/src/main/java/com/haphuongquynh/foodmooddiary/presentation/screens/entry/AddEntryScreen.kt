package com.haphuongquynh.foodmooddiary.presentation.screens.entry

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var screenState by remember { mutableStateOf(0) } // 0: Photo, 1: Detail, 2: Form
    var caption by remember { mutableStateOf("Cung hỉ Christmas, Merry phát tài") }
    
    // Screen 1: Simple photo capture view
    if (screenState == 0) {
        SimplePhotoCapture(
            caption = caption,
            onContinue = { screenState = 1 },
            onBack = { navController.navigateUp() }
        )
        return
    }
    
    // Screen 2: Entry Detail view
    if (screenState == 1) {
        EntryDetailScreen(
            caption = caption,
            onEdit = { screenState = 2 },
            onBack = { screenState = 0 }
        )
        return
    }
    
    // Screen 3: Full form for editing
    AddEntryFormScreen(
        navController = navController,
        viewModel = viewModel,
        onBack = { screenState = 1 }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryFormScreen(
    navController: NavController,
    viewModel: FoodEntryViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var foodName by remember { mutableStateOf("Bánh kem dâu Giáng sinh đáng yêu") }
    var selectedMood by remember { mutableStateOf("😊") }
    var notes by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(3) }
    var mealType by remember { mutableStateOf("Dinner") }
    var showCamera by remember { mutableStateOf(false) }
    
    val photoData by viewModel.currentPhoto.collectAsState()
    val location by viewModel.currentLocation.collectAsState()
    val scrollState = rememberScrollState()
    
    // Mood emojis
    val moods = listOf("😊", "😌", "😔", "😫", "🎉", "💪")
    
    // Show camera if requested
    if (showCamera) {
        CameraScreen(
            onPhotoCaptured = { file, bitmap ->
                viewModel.processPhoto(file, bitmap)
                showCamera = false
            },
            onDismiss = { showCamera = false }
        )
        return
    }
    
    Scaffold(
        containerColor = Color(0xFF1C1C1E),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Add Entry",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1C1C1E)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo with "Change photo" text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF3C3C3E))
                    .clickable { showCamera = true }
            ) {
                if (photoData != null) {
                    Image(
                        bitmap = photoData!!.bitmap.asImageBitmap(),
                        contentDescription = "Food Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎂",
                            fontSize = 60.sp
                        )
                    }
                }
                
                // "Change photo" text overlay
                Text(
                    text = "Change photo",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    color = Color(0xFFFFC857),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Food Name
            Column {
                Text(
                    text = "Food Name",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = foodName,
                    onValueChange = { foodName = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFA8A8A8),
                        unfocusedContainerColor = Color(0xFFA8A8A8),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }
            
            // Mood
            Column {
                Text(
                    text = "Mood",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFA8A8A8)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        moods.forEach { mood ->
                            Text(
                                text = mood,
                                fontSize = 32.sp,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if (mood == selectedMood) Color(0xFFFFD700)
                                        else Color.Transparent
                                    )
                                    .clickable { selectedMood = mood }
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
            
            // Date & Time and Location row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Date & Time",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFA8A8A8)
                    ) {
                        Text(
                            text = "Now (tap to change)",
                            modifier = Modifier.padding(12.dp),
                            color = Color.Black,
                            fontSize = 12.sp
                        )
                    }
                }
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Location",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFA8A8A8)
                    ) {
                        Text(
                            text = location?.address ?: "",
                            modifier = Modifier.padding(12.dp),
                            color = Color.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            // Meal Type
            Column {
                Text(
                    text = "Meal Type",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Breakfast", "Lunch", "Dinner", "Snack").forEach { type ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { mealType = type },
                            shape = RoundedCornerShape(12.dp),
                            color = if (mealType == type) Color(0xFF6C6C6C) else Color(0xFFA8A8A8)
                        ) {
                            Text(
                                text = type,
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = Color.Black,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = if (mealType == type) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
            
            // Notes and Rating row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1.2f)
                ) {
                    Text(
                        text = "Notes",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { 
                            Text(
                                "Write something...",
                                color = Color.Gray,
                                fontSize = 12.sp
                            ) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFA8A8A8),
                            unfocusedContainerColor = Color(0xFFA8A8A8),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                }
                
                Column(
                    modifier = Modifier.weight(0.8f)
                ) {
                    Text(
                        text = "Rating",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFA8A8A8)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(5) { index ->
                                    Text(
                                        text = if (index < rating) "⭐" else "☆",
                                        fontSize = 20.sp,
                                        modifier = Modifier.clickable { rating = index + 1 }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // AI Suggestion button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* AI action */ },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2C2C2E)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Gợi ý AI 🧠",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Bánh màu đỏ → Happy",
                        color = Color(0xFFFFC857),
                        fontSize = 12.sp
                    )
                }
            }
            
            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.addEntry(
                            foodName = foodName,
                            notes = notes,
                            moodColor = android.graphics.Color.parseColor("#FFC857"),
                            photoFile = photoData?.file
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFC857),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Save",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimplePhotoCapture(
    caption: String,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFF1C1C1E),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Add Entry",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1C1C1E)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.5f))
            
            // Image with caption overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF3C3C3E))
            ) {
                // Placeholder for captured image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF4A4A4A)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎂",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Christmas Cake",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Caption overlay at bottom
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.95f)
                ) {
                    Text(
                        text = caption,
                        modifier = Modifier.padding(16.dp),
                        color = Color.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(0.5f))
            
            // Continue button
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC857),
                    contentColor = Color.Black
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "→",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailScreen(
    caption: String,
    onEdit: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFF1C1C1E),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Entry Detail",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1C1C1E)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF3C3C3E))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF4A4A4A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎂",
                        fontSize = 80.sp
                    )
                }
            }
            
            // Food name with emoji
            Text(
                text = "Bánh kem dâu Giáng sinh đáng yêu 😋",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Date & Time and Location row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Date & Time",
                        color = Color(0xFFFFC857),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "12 Dec 2025 - 8:30 PM",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Location",
                        color = Color(0xFFFFC857),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Thành phố biển",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
            
            // Meal Type and Rating row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Meal Type",
                        color = Color(0xFFFFC857),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Dinner",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Rating",
                        color = Color(0xFFFFC857),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(5) {
                            Text(
                                text = "⭐",
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
            
            // Notes
            Column {
                Text(
                    text = "Notes",
                    color = Color(0xFFFFC857),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Cảm giác Giáng sinh quá, ngon xỉu 😋🎄",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            
            // AI Palette Extracted
            Column {
                Text(
                    text = "AI Palette Extracted",
                    color = Color(0xFFFFC857),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF6B6B))
                    )
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFC857))
                    )
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFA67B5B))
                    )
                }
            }
            
            // AI Suggestion
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF2C2C2E)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "AI Suggestion",
                        color = Color(0xFFFFC857),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ảnh có nhiều màu đỏ → Gợi ý mood Happy hoặc Energy",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
            
            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { /* Save action */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFC857),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Save",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Button(
                    onClick = { /* Share action */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Share",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
