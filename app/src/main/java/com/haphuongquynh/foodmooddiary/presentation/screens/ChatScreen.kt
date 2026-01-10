package com.haphuongquynh.foodmooddiary.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
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
    var showSettingsDialog by remember { mutableStateOf(false) }
    var aiTemperature by remember { mutableStateOf(0.7f) }
    var maxTokens by remember { mutableStateOf(150) }
    var conversationContext by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    // Test connection khi khởi động
    LaunchedEffect(Unit) {
        messages = listOf("Xin chào! Mình là trợ lý cảm xúc AI. Hãy chia sẻ cảm xúc của bạn!" to false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackPrimary)
    ) {
        // Header - single card with green background
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF6B8E7F), // Pastel green background
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Robot icon with black circular background
                    Surface(
                        shape = CircleShape,
                        color = Color.Black,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.SmartToy,
                                contentDescription = "AI Assistant",
                                tint = PastelGreen,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = "Trợ lý AI",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhiteText
                    )
                }
                
                // Settings icon
                IconButton(onClick = { showSettingsDialog = true }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = WhiteText,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Chat messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
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

        // Input area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                                
                                // Sử dụng gemini-2.5-flash (model mới nhất)
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
                                android.util.Log.e("ChatScreen", "API Error: ${result.code} - $responseBody")
                                
                                // Thông báo lỗi thân thiện dựa trên mã lỗi
                                val friendlyError = when (result.code) {
                                    503 -> "Xin lỗi, AI đang bận quá! 😅 Có vẻ nhiều người đang sử dụng cùng lúc. Bạn thử lại sau vài giây nhé?"
                                    429 -> "Ối, bạn gửi tin nhắn hơi nhanh rồi! 😊 Hãy thở sâu và thử lại sau chút nhé?"
                                    401 -> "Có lỗi xác thực rồi! 🔑 Bạn thử khởi động lại ứng dụng xem sao?"
                                    500 -> "Máy chủ đang gặp sự cố nhỏ! 🛠️ Mình thử lại sau nhé?"
                                    else -> "Hmm, có điều gì đó không ổn! 🤔 Bạn kiểm tra kết nối mạng và thử lại nhé?"
                                }
                                messages = messages + (friendlyError to false)
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("ChatScreen", "API Error", e)
                            e.printStackTrace()
                            
                            // Thông báo lỗi thân thiện cho exception
                            val friendlyError = when {
                                e.message?.contains("timeout", ignoreCase = true) == true -> 
                                    "Kết nối hơi chậm quá! ⏰ Bạn thử lại xem sao?"
                                e.message?.contains("network", ignoreCase = true) == true || 
                                e.message?.contains("internet", ignoreCase = true) == true ->
                                    "Mạng internet có vấn đề rồi! 📶 Bạn kiểm tra kết nối nhé?"
                                else -> 
                                    "Ối, có lỗi bất ngờ xảy ra! 🙈 Bạn thử khởi động lại ứng dụng nhé?"
                            }
                            messages = messages + (friendlyError to false)
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
    
    // Settings Dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            containerColor = BlackSecondary,
            title = {
                Text(
                    text = "Cài đặt Trợ lý AI",
                    color = PastelGreen,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Temperature setting
                    Column {
                        Text(
                            text = "Độ sáng tạo: ${String.format("%.1f", aiTemperature)}",
                            color = WhiteText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Cao hơn = phản hồi sáng tạo hơn",
                            color = GrayText,
                            fontSize = 12.sp
                        )
                        Slider(
                            value = aiTemperature,
                            onValueChange = { aiTemperature = it },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = PastelGreen,
                                activeTrackColor = PastelGreen,
                                inactiveTrackColor = GrayText
                            )
                        )
                    }
                    
                    // Max tokens setting
                    Column {
                        Text(
                            text = "Độ dài phản hồi: $maxTokens tokens",
                            color = WhiteText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Số lượng từ tối đa trong câu trả lời",
                            color = GrayText,
                            fontSize = 12.sp
                        )
                        Slider(
                            value = maxTokens.toFloat(),
                            onValueChange = { maxTokens = it.toInt() },
                            valueRange = 50f..500f,
                            steps = 9,
                            colors = SliderDefaults.colors(
                                thumbColor = PastelGreen,
                                activeTrackColor = PastelGreen,
                                inactiveTrackColor = GrayText
                            )
                        )
                    }
                    
                    // Conversation context toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Nhớ ngữ cảnh",
                                color = WhiteText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "AI nhớ các tin nhắn trước",
                                color = GrayText,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = conversationContext,
                            onCheckedChange = { conversationContext = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PastelGreen,
                                checkedTrackColor = PastelGreen.copy(alpha = 0.5f),
                                uncheckedThumbColor = GrayText,
                                uncheckedTrackColor = GrayText.copy(alpha = 0.3f)
                            )
                        )
                    }
                    
                    // Clear conversation button
                    Button(
                        onClick = {
                            messages = listOf("Xin chào! Mình là trợ lý cảm xúc AI. Hãy chia sẻ cảm xúc của bạn!" to false)
                            showSettingsDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlackPrimary
                        ),
                        border = BorderStroke(1.dp, PastelGreen.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = PastelGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Xóa lịch sử hội thoại",
                            color = PastelGreen
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showSettingsDialog = false }
                ) {
                    Text("Đóng", color = PastelGreen)
                }
            }
        )
    }
}