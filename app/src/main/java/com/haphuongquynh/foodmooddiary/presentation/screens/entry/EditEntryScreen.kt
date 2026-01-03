package com.haphuongquynh.foodmooddiary.presentation.screens.entry

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.EntryState
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.FoodEntryViewModel
import com.haphuongquynh.foodmooddiary.ui.theme.*

/**
 * Edit Entry Screen - Edit existing food entry
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEntryScreen(
    navController: NavController,
    entryId: String,
    viewModel: FoodEntryViewModel = hiltViewModel()
) {
    val entries by viewModel.entries.collectAsState()
    val entry = entries.firstOrNull { it.id == entryId }
    val entryState by viewModel.entryState.collectAsState()
    
    // Form state - initialize from entry
    var foodName by remember(entry) { mutableStateOf(entry?.foodName ?: "") }
    var notes by remember(entry) { mutableStateOf(entry?.notes ?: "") }
    var selectedMood by remember(entry) { mutableStateOf(entry?.mood ?: "😊") }
    var selectedMealType by remember(entry) { mutableStateOf(entry?.mealType ?: "Dinner") }
    
    LaunchedEffect(entryState) {
        when (entryState) {
            is EntryState.Success -> {
                navController.navigateUp()
                viewModel.resetEntryState()
            }
            else -> {}
        }
    }
    
    if (entry == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BlackPrimary
    ) {
        Scaffold(
            containerColor = BlackPrimary,
            topBar = {
                TopAppBar(
                    title = { Text("Edit Entry", color = WhiteText) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = WhiteText)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BlackPrimary
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Photo preview (read-only)
                val photoPath = entry.localPhotoPath ?: entry.photoUrl
                if (photoPath != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = BlackSecondary
                    ) {
                        AsyncImage(
                            model = photoPath,
                            contentDescription = "Food photo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                
                // Food Name
                OutlinedTextField(
                    value = foodName,
                    onValueChange = { foodName = it },
                    label = { Text("Food Name", color = GrayText) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PastelGreen,
                        unfocusedBorderColor = BlackTertiary,
                        focusedTextColor = WhiteText,
                        unfocusedTextColor = WhiteText,
                        cursorColor = PastelGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Mood Selector
                Text(
                    text = "Mood",
                    color = WhiteText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("😢", "😔", "😐", "😊", "🥰").forEach { mood ->
                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { selectedMood = mood },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedMood == mood) PastelGreen.copy(alpha = 0.3f) else BlackSecondary,
                            border = if (selectedMood == mood) BorderStroke(2.dp, PastelGreen) else null
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(mood, fontSize = 24.sp)
                            }
                        }
                    }
                }
                
                // Meal Type
                Text(
                    text = "Meal Type",
                    color = WhiteText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Breakfast", "Lunch", "Dinner", "Snack").forEach { type ->
                        FilterChip(
                            selected = selectedMealType == type,
                            onClick = { selectedMealType = type },
                            label = { Text(type, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PastelGreen,
                                selectedLabelColor = BlackPrimary,
                                containerColor = BlackSecondary,
                                labelColor = WhiteText
                            )
                        )
                    }
                }
                
                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes", color = GrayText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PastelGreen,
                        unfocusedBorderColor = BlackTertiary,
                        focusedTextColor = WhiteText,
                        unfocusedTextColor = WhiteText,
                        cursorColor = PastelGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Save Button
                Button(
                    onClick = {
                        val updatedEntry = entry.copy(
                            foodName = foodName.ifBlank { "Unnamed" },
                            notes = notes,
                            mood = selectedMood,
                            mealType = selectedMealType,
                            updatedAt = System.currentTimeMillis()
                        )
                        viewModel.updateEntry(updatedEntry)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PastelGreen
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = entryState !is EntryState.Loading
                ) {
                    if (entryState is EntryState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = BlackPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Save, "Save", tint = BlackPrimary)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Save Changes",
                            color = BlackPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
