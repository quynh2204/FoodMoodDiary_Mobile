package com.haphuongquynh.foodmooddiary.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

@Composable
fun ChatScreen() {
    // Model AI & Key cứng (để test)
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AIzaSyBsQQPvN3KcSBcujgvy-jHDTVLCcBM4Q5A"
        )
    }

    var inputText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Text(
            text = "Trợ lý Cảm Xúc AI ✨",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { (msg, isUser) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        modifier = Modifier.widthIn(max = 280.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) Color(0xFFBBDEFB) else Color.White
                        )
                    ) {
                        Text(
                            text = msg,
                            modifier = Modifier.padding(12.dp),
                            color = Color.Black
                        )
                    }
                }
            }
            if (isLoading) {
                item { Text("Đang nhập...", fontSize = 12.sp, color = Color.Gray) }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Bạn đang nghĩ gì?") },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                enabled = !isLoading,
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFF4CAF50), RoundedCornerShape(25.dp)),
                onClick = {
                    if (inputText.isBlank()) return@IconButton
                    val userMessage = inputText
                    inputText = ""
                    messages = messages + (userMessage to true)
                    isLoading = true
                    scope.launch {
                        try {
                            val response = generativeModel.generateContent(userMessage)
                            messages = messages + ((response.text ?: "...") to false)
                        } catch (e: Exception) {
                            messages = messages + ("Lỗi: ${e.message}" to false)
                        }
                        isLoading = false
                    }
                }
            ) {
                Icon(Icons.Default.Send, "Gửi", tint = Color.White)
            }
        }
    }
}