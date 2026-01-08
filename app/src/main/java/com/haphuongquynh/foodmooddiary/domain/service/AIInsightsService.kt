package com.haphuongquynh.foodmooddiary.domain.service

import com.haphuongquynh.foodmooddiary.BuildConfig
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.model.Insight
import com.haphuongquynh.foodmooddiary.domain.model.InsightType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI Insights Service - Uses Gemini API to generate intelligent insights
 * Similar to ChatScreen's implementation but optimized for data analysis
 */
@Singleton
class AIInsightsService @Inject constructor() {
    
    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val client = OkHttpClient()
    
    /**
     * Generate AI-powered insights from food entries using Gemini API
     */
    suspend fun generateAIInsights(entries: List<FoodEntry>): List<Insight> {
        android.util.Log.d("AIInsights", "=== Generate AI Insights ===")
        android.util.Log.d("AIInsights", "Total entries: ${entries.size}")
        
        if (entries.isEmpty()) {
            android.util.Log.w("AIInsights", "No entries found, returning empty state")
            return listOf(
                Insight(
                    id = "empty_state",
                    title = "Bắt đầu ghi lại 🚀",
                    description = "Thêm vài bửa ăn để AI có thể phân tích thói quen và đưa ra gợi ý cho bạn!",
                    type = InsightType.RECOMMENDATION,
                    actionable = false
                )
            )
        }
        
        android.util.Log.d("AIInsights", "Recent 5 entries: ${entries.take(5).map { "${it.foodName} - ${it.mood}" }}")
        
        return withContext(Dispatchers.IO) {
            try {
                // Prepare data summary for AI
                val dataSummary = prepareDataSummary(entries)
                
                // Create AI prompt
                val prompt = """
                    Bạn là chuyên gia phân tích sức khỏe và tâm trạng. Dựa trên dữ liệu bữa ăn sau của người dùng, 
                    hãy đưa ra 3-4 insights ngắn gọn bằng tiếng Việt:
                    
                    $dataSummary
                    
                    QUAN TRỌNG: Mỗi insight phải ngắn gọn, description tối đa 60 từ.
                    
                    Trả về ĐÚNG định dạng JSON array sau (chỉ JSON, không thêm text hay markdown):
                    [
                      {
                        "title": "Tiêu đề ngắn với emoji",
                        "description": "Mô tả ngắn gọn trong 1 câu",
                        "type": "FOOD_CORRELATION",
                        "actionable": true
                      }
                    ]
                    
                    Các type hợp lệ: FOOD_CORRELATION, MOOD_PATTERN, TIME_PATTERN, RECOMMENDATION
                """.trimIndent()
                
                // Call Gemini API
                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", prompt))
                            })
                        })
                    })
                    // Add generation config for better JSON output
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.5)
                        put("topK", 40)
                        put("topP", 0.95)
                        put("maxOutputTokens", 2048)
                        put("responseMimeType", "application/json")
                    })
                }
                
                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()
                
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                
                if (response.isSuccessful && responseBody != null) {
                    parseAIResponse(responseBody)
                } else {
                    // Fallback to local insights if API fails
                    android.util.Log.e("AIInsights", "API Error: ${response.code} - $responseBody")
                    generateLocalInsights(entries)
                }
            } catch (e: Exception) {
                android.util.Log.e("AIInsights", "Error generating AI insights", e)
                // Fallback to local insights
                generateLocalInsights(entries)
            }
        }
    }
    
    /**
     * Prepare data summary for AI analysis
     */
    private fun prepareDataSummary(entries: List<FoodEntry>): String {
        val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        val recent = entries.sortedByDescending { it.timestamp }.take(20)
        
        // Count moods
        val moodCounts = recent.groupingBy { it.mood }.eachCount()
        
        // Top foods
        val topFoods = recent.groupingBy { it.foodName }.eachCount()
            .entries.sortedByDescending { it.value }.take(5)
        
        // Time patterns
        val mealTimes = recent.map { 
            Calendar.getInstance().apply { timeInMillis = it.timestamp }.get(Calendar.HOUR_OF_DAY)
        }.groupingBy { it }.eachCount()
        
        return buildString {
            appendLine("=== DỮ LIỆU 20 BỮA ĂN GẦN NHẤT ===")
            appendLine()
            appendLine("Thống kê cảm xúc:")
            moodCounts.forEach { (mood, count) ->
                appendLine("- $mood: $count lần")
            }
            appendLine()
            appendLine("Món ăn phổ biến:")
            topFoods.forEach { (food, count) ->
                appendLine("- $food: $count lần")
            }
            appendLine()
            appendLine("Thời gian ăn phổ biến:")
            mealTimes.entries.sortedByDescending { it.value }.take(3).forEach { (hour, count) ->
                appendLine("- ${hour}h: $count bữa")
            }
            appendLine()
            appendLine("Chi tiết 5 bữa ăn gần nhất:")
            recent.take(5).forEach { entry ->
                appendLine("- ${dateFormat.format(Date(entry.timestamp))}: ${entry.foodName} - Cảm xúc: ${entry.mood ?: "Chưa ghi"}")
            }
        }
    }
    
    /**
     * Parse AI response and convert to Insight objects
     */
    private fun parseAIResponse(responseBody: String): List<Insight> {
        try {
            val jsonResponse = JSONObject(responseBody)
            val aiText = jsonResponse
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
            
            android.util.Log.d("AIInsights", "Raw AI response: $aiText")
            
            // Extract JSON array from response (AI might wrap it in markdown code blocks)
            var jsonArrayText = aiText
                .replace("```json", "")
                .replace("```", "")
                .trim()
            
            // If response starts with non-JSON text, try to extract JSON array
            val arrayStart = jsonArrayText.indexOf('[')
            val arrayEnd = jsonArrayText.lastIndexOf(']')
            if (arrayStart >= 0 && arrayEnd > arrayStart) {
                jsonArrayText = jsonArrayText.substring(arrayStart, arrayEnd + 1)
            }
            
            android.util.Log.d("AIInsights", "Cleaned JSON: $jsonArrayText")
            
            val insightsArray = JSONArray(jsonArrayText)
            val insights = mutableListOf<Insight>()
            
            for (i in 0 until insightsArray.length()) {
                val insightJson = insightsArray.getJSONObject(i)
                
                val typeString = insightJson.optString("type", "RECOMMENDATION")
                val type = try {
                    InsightType.valueOf(typeString)
                } catch (e: Exception) {
                    InsightType.RECOMMENDATION
                }
                
                insights.add(
                    Insight(
                        id = "ai_insight_$i",
                        title = insightJson.getString("title"),
                        description = insightJson.getString("description"),
                        type = type,
                        actionable = insightJson.optBoolean("actionable", false)
                    )
                )
            }
            
            return insights
        } catch (e: Exception) {
            android.util.Log.e("AIInsights", "Error parsing AI response", e)
            return emptyList()
        }
    }
    
    /**
     * Generate local insights as fallback (similar to existing implementation)
     */
    private fun generateLocalInsights(entries: List<FoodEntry>): List<Insight> {
        val insights = mutableListOf<Insight>()
        
        // Mood pattern
        val happyMoods = entries.count { it.mood in listOf("😊", "😄", "💪") }
        val totalWithMood = entries.count { !it.mood.isNullOrEmpty() }
        
        if (totalWithMood > 0 && happyMoods.toFloat() / totalWithMood > 0.6) {
            insights.add(
                Insight(
                    id = "mood_positive",
                    title = "Tâm trạng tích cực! 🌟",
                    description = "Bạn cảm thấy vui vẻ ${(happyMoods * 100 / totalWithMood)}% khi ăn. Hãy duy trì nhé!",
                    type = InsightType.MOOD_PATTERN,
                    actionable = false
                )
            )
        }
        
        // Food correlation
        val topFoods = entries.groupBy { it.foodName }
            .mapValues { (_, list) -> 
                list.count { it.mood in listOf("😊", "😄", "💪") }.toFloat() / list.size 
            }
            .entries.sortedByDescending { it.value }
            .take(1)
        
        if (topFoods.isNotEmpty() && topFoods.first().value > 0.7) {
            insights.add(
                Insight(
                    id = "food_happy",
                    title = "${topFoods.first().key} làm bạn vui! 😊",
                    description = "Bạn thường cảm thấy tốt khi ăn món này. Đây là lựa chọn tuyệt vời!",
                    type = InsightType.FOOD_CORRELATION,
                    actionable = true
                )
            )
        }
        
        // Time pattern
        val mealHours = entries.map { 
            Calendar.getInstance().apply { timeInMillis = it.timestamp }.get(Calendar.HOUR_OF_DAY)
        }
        val peakHour = mealHours.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
        
        if (peakHour != null) {
            insights.add(
                Insight(
                    id = "time_pattern",
                    title = "Thời gian ăn chính ⏰",
                    description = "Bạn thường ăn vào khoảng ${peakHour}h. Thói quen ổn định tốt cho sức khỏe!",
                    type = InsightType.TIME_PATTERN,
                    actionable = false
                )
            )
        }
        
        // Add recommendation
        insights.add(
            Insight(
                id = "recommendation",
                title = "Gợi ý cải thiện 💡",
                description = "Tiếp tục ghi lại bữa ăn đều đặn để có thêm insights chính xác hơn!",
                type = InsightType.RECOMMENDATION,
                actionable = true
            )
        )
        
        return insights
    }
}
