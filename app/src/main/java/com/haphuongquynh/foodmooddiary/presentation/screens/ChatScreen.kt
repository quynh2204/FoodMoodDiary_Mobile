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
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.json.JSONArray
import java.io.IOException
import com.haphuongquynh.foodmooddiary.BuildConfig
import com.haphuongquynh.foodmooddiary.ui.theme.BlackPrimary
import com.haphuongquynh.foodmooddiary.ui.theme.BlackSecondary
import com.haphuongquynh.foodmooddiary.ui.theme.PastelGreen
import com.haphuongquynh.foodmooddiary.ui.theme.WhiteText
import com.haphuongquynh.foodmooddiary.ui.theme.GrayText

@Composable
fun ChatScreen() {
    // Load API key from BuildConfig (configured in build.gradle)
    val apiKey = BuildConfig.GEMINI_API_KEY
    
    var inputText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    // Test connection khi khởi động
    LaunchedEffect(Unit) {
        messages = listOf("👋 Xin chào! Mình là trợ lý cảm xúc AI. Hãy chia sẻ cảm xúc của bạn!" to false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackPrimary)
            .padding(16.dp)
    ) {
        Text(
            text = "Trợ lý Cảm Xúc AI ✨",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PastelGreen,
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
                            containerColor = if (isUser) PastelGreen.copy(alpha = 0.3f) else BlackSecondary
                        )
                    ) {
                        Text(
                            text = msg,
                            modifier = Modifier.padding(12.dp),
                            color = WhiteText
                        )
                    }
                }
            }
            if (isLoading) {
                item { Text("Đang nhập...", fontSize = 12.sp, color = GrayText) }
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
                placeholder = { Text("Bạn đang nghĩ gì?", color = GrayText) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BlackSecondary,
                    unfocusedContainerColor = BlackSecondary,
                    focusedTextColor = WhiteText,
                    unfocusedTextColor = WhiteText,
                    focusedBorderColor = PastelGreen,
                    unfocusedBorderColor = GrayText
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                enabled = !isLoading && inputText.isNotBlank(),
                modifier = Modifier
                    .size(50.dp)
                    .background(PastelGreen, RoundedCornerShape(25.dp)),
                onClick = {
                    if (inputText.isBlank()) return@IconButton
                    val userMessage = inputText
                    inputText = ""
                    messages = messages + (userMessage to true)
                    isLoading = true
                    scope.launch {
                        try {
                            // Gọi API trong IO thread
                            val result = withContext(Dispatchers.IO) {
                                val client = OkHttpClient()
                                
                                val systemPrompt = """
                                    Bạn là trợ lý cảm xúc AI thấu hiểu. Hãy phản chiếu và xác nhận cảm xúc người dùng, 
                                    giúp họ nhận diện cảm xúc rõ ràng hơn (đặc biệt về thói quen ăn uống).
                                    Trả lời ấm áp, thấu hiểu, đặt câu hỏi mở (2-4 câu ngắn gọn bằng tiếng Việt).
                                """.trimIndent()
                                
                                val jsonBody = JSONObject().apply {
                                    put("contents", JSONArray().apply {
                                        put(JSONObject().apply {
                                            put("parts", JSONArray().apply {
                                                put(JSONObject().put("text", "$systemPrompt\n\nNgười dùng: $userMessage"))
                                            })
                                        })
                                    })
                                }
                                
                                // Thử v1beta với gemini-2.5-flash (model mới nhất)
                                val request = Request.Builder()
                                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
                                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                                    .build()
                                
                                client.newCall(request).execute()
                            }
                            
                            val responseBody = result.body?.string()
                            android.util.Log.d("ChatScreen", "API Response Code: ${result.code}")
                            android.util.Log.d("ChatScreen", "API Response: $responseBody")
                            
                            if (result.isSuccessful && responseBody != null) {
                                val jsonResponse = JSONObject(responseBody)
                                val aiText = jsonResponse
                                    .getJSONArray("candidates")
                                    .getJSONObject(0)
                                    .getJSONObject("content")
                                    .getJSONArray("parts")
                                    .getJSONObject(0)
                                    .getString("text")
                                messages = messages + (aiText to false)
                            } else {
                                val errorMsg = "Lỗi API ${result.code}: $responseBody"
                                android.util.Log.e("ChatScreen", errorMsg)
                                messages = messages + (errorMsg to false)
                            }
                        } catch (e: Exception) {
                            val errorMessage = "Lỗi: ${e.message}"
                            android.util.Log.e("ChatScreen", "API Error", e)
                            e.printStackTrace()
                            messages = messages + (errorMessage to false)
                        } finally {
                            isLoading = false
                        }
                    }
                }
            ) {
                Icon(Icons.Default.Send, "Gửi", tint = Color.White)
            }
        }
    }
}