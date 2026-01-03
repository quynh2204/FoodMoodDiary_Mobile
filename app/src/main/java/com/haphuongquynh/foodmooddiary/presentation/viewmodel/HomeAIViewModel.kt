package com.haphuongquynh.foodmooddiary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.model.LocalColorAnalysis
import com.haphuongquynh.foodmooddiary.domain.model.MoodAnalysis
import com.haphuongquynh.foodmooddiary.domain.model.MoodType
import com.haphuongquynh.foodmooddiary.domain.usecase.entry.GetEntriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel for home screen analysis features
 * 100% LOCAL - No API needed
 * Analysis based on 5 core moods: Happy, Sad, Angry, Tired, Energetic
 */
@HiltViewModel
class HomeAIViewModel @Inject constructor(
    getEntriesUseCase: GetEntriesUseCase
) : ViewModel() {

    // All entries
    val entries: StateFlow<List<FoodEntry>> = getEntriesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Mood Insight state
    private val _moodInsight = MutableStateFlow<MoodInsightState>(MoodInsightState.Idle)
    val aiInsight: StateFlow<MoodInsightState> = _moodInsight.asStateFlow()
    
    // Color/Mood Analysis state
    private val _colorAnalysis = MutableStateFlow<ColorAnalysisState>(ColorAnalysisState.Idle)
    val colorAnalysis: StateFlow<ColorAnalysisState> = _colorAnalysis.asStateFlow()
    
    /**
     * Generate mood-based insight - LOCAL ANALYSIS
     */
    fun generateAIInsight() {
        viewModelScope.launch {
            _moodInsight.value = MoodInsightState.Loading
            
            val currentEntries = entries.value
            val analysis = analyzeMoods(currentEntries)
            
            _moodInsight.value = MoodInsightState.Success(analysis)
        }
    }
    
    private fun analyzeMoods(allEntries: List<FoodEntry>): MoodAnalysis {
        if (allEntries.isEmpty()) {
            return MoodAnalysis(
                dominantMood = null,
                moodCounts = emptyMap(),
                totalEntries = 0,
                happyPercentage = 0,
                insight = "🌟 Chào bạn! Bắt đầu ghi lại bữa ăn và cảm xúc đầu tiên nhé!",
                suggestion = "Chụp ảnh món ăn và chọn cảm xúc của bạn."
            )
        }
        
        val weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        val weekEntries = allEntries.filter { it.timestamp >= weekAgo }
        val todayEntries = allEntries.filter { isToday(it.timestamp) }
        
        // Count moods
        val moodCounts = mutableMapOf<MoodType, Int>()
        weekEntries.forEach { entry ->
            val mood = MoodType.fromEmoji(entry.mood ?: "") 
                ?: MoodType.fromColorInt(entry.moodColor)
            mood?.let {
                moodCounts[it] = (moodCounts[it] ?: 0) + 1
            }
        }
        
        val dominantMood = moodCounts.maxByOrNull { it.value }?.key
        val totalMoods = moodCounts.values.sum()
        val happyCount = moodCounts[MoodType.HAPPY] ?: 0
        val happyPercentage = if (totalMoods > 0) (happyCount * 100) / totalMoods else 0
        
        val streak = calculateStreak(allEntries)
        
        // Generate insight based on mood analysis
        val insight = generateMoodInsight(
            dominantMood = dominantMood,
            moodCounts = moodCounts,
            weekMeals = weekEntries.size,
            todayMeals = todayEntries.size,
            streak = streak,
            happyPercentage = happyPercentage
        )
        
        val suggestion = generateMoodSuggestion(dominantMood, moodCounts)
        
        return MoodAnalysis(
            dominantMood = dominantMood,
            moodCounts = moodCounts,
            totalEntries = weekEntries.size,
            happyPercentage = happyPercentage,
            insight = insight,
            suggestion = suggestion
        )
    }
    
    private fun generateMoodInsight(
        dominantMood: MoodType?,
        moodCounts: Map<MoodType, Int>,
        weekMeals: Int,
        todayMeals: Int,
        streak: Int,
        happyPercentage: Int
    ): String {
        return when {
            weekMeals == 0 -> "🌟 Tuần mới! Hãy bắt đầu ghi lại bữa ăn và cảm xúc nhé!"
            streak >= 7 -> "🔥 Tuyệt vời! $streak ngày liên tiếp! Bạn đang duy trì thói quen tốt."
            streak >= 3 -> "💪 $streak ngày kiên trì! Cứ tiếp tục nhé!"
            todayMeals == 0 -> "🍽️ Hôm nay chưa có bữa ăn nào. Đừng quên ghi lại nhé!"
            happyPercentage >= 70 -> "😊 Tuyệt vời! ${happyPercentage}% bữa ăn tuần này bạn cảm thấy vui!"
            happyPercentage >= 50 -> "🌈 Khá tốt! ${happyPercentage}% thời gian bạn cảm thấy vui khi ăn."
            dominantMood == MoodType.TIRED -> "😫 Tuần này bạn hay mệt mỏi. Nghỉ ngơi và ăn uống đủ chất nhé!"
            dominantMood == MoodType.SAD -> "💙 Tuần này bạn hơi buồn. Thử món ăn yêu thích để vui hơn nhé!"
            dominantMood == MoodType.ANGRY -> "😤 Có vẻ bạn hay căng thẳng. Thư giãn khi ăn sẽ tốt hơn đấy!"
            dominantMood == MoodType.ENERGETIC -> "💪 Bạn đang rất năng động! Tiếp tục duy trì nhé!"
            else -> "📊 Tuần này: $weekMeals bữa ăn được ghi lại. ${getMoodSummary(moodCounts)}"
        }
    }
    
    private fun getMoodSummary(moodCounts: Map<MoodType, Int>): String {
        if (moodCounts.isEmpty()) return ""
        val dominant = moodCounts.maxByOrNull { it.value }?.key ?: return ""
        return "Cảm xúc chủ đạo: ${dominant.emoji} ${dominant.labelVi}"
    }
    
    private fun generateMoodSuggestion(
        dominantMood: MoodType?,
        moodCounts: Map<MoodType, Int>
    ): String {
        return when (dominantMood) {
            MoodType.HAPPY -> "Tiếp tục duy trì tâm trạng tốt! 🌟"
            MoodType.SAD -> "Thử ăn món yêu thích hoặc đi ăn cùng bạn bè 💙"
            MoodType.ANGRY -> "Ăn chậm, nhai kỹ và thư giãn khi ăn sẽ giúp bạn bình tĩnh hơn 🧘"
            MoodType.TIRED -> "Bổ sung thêm protein và vitamin. Nghỉ ngơi đủ giấc nhé! 😴"
            MoodType.ENERGETIC -> "Tuyệt vời! Tiếp tục duy trì chế độ ăn uống hiện tại 💪"
            null -> "Ghi lại nhiều bữa ăn hơn để nhận gợi ý phù hợp!"
        }
    }
    
    /**
     * Analyze mood colors distribution - LOCAL ANALYSIS
     */
    fun analyzeColorPalette() {
        viewModelScope.launch {
            _colorAnalysis.value = ColorAnalysisState.Loading
            
            val currentEntries = entries.value
            val analysis = analyzeColorsLocally(currentEntries)
            
            _colorAnalysis.value = ColorAnalysisState.Success(analysis)
        }
    }
    
    private fun analyzeColorsLocally(allEntries: List<FoodEntry>): LocalColorAnalysis {
        if (allEntries.isEmpty()) {
            return LocalColorAnalysis(
                dominantMood = null,
                colorDistribution = emptyMap(),
                insight = "🎨 Chưa có dữ liệu màu sắc cảm xúc.",
                suggestion = "Thêm bữa ăn và chọn cảm xúc để xem phân tích!"
            )
        }
        
        val recentEntries = allEntries.sortedByDescending { it.timestamp }.take(20)
        
        // Count mood colors
        val colorDistribution = mutableMapOf<MoodType, Int>()
        recentEntries.forEach { entry ->
            val mood = MoodType.fromEmoji(entry.mood ?: "") 
                ?: MoodType.fromColorInt(entry.moodColor)
            mood?.let {
                colorDistribution[it] = (colorDistribution[it] ?: 0) + 1
            }
        }
        
        val dominantMood = colorDistribution.maxByOrNull { it.value }?.key
        
        val insight = when {
            colorDistribution.isEmpty() -> "🎨 Chưa có dữ liệu cảm xúc."
            colorDistribution.size >= 4 -> "🌈 Cảm xúc đa dạng! Bạn trải nghiệm nhiều cung bậc khác nhau."
            colorDistribution.size == 1 -> "📊 Cảm xúc khá ổn định: ${dominantMood?.emoji} ${dominantMood?.labelVi}"
            else -> "📊 Cảm xúc chủ đạo: ${dominantMood?.emoji} ${dominantMood?.labelVi}"
        }
        
        val suggestion = when (dominantMood) {
            MoodType.HAPPY -> "Màu vàng chiếm ưu thế - Bạn đang có tâm trạng tốt! 🌟"
            MoodType.SAD -> "Màu xanh dương nhiều - Hãy tìm niềm vui trong bữa ăn 💙"
            MoodType.ANGRY -> "Màu đỏ chiếm ưu thế - Thư giãn hơn khi ăn nhé 🔴"
            MoodType.TIRED -> "Màu xám nhiều - Nghỉ ngơi và ăn uống đủ chất 😴"
            MoodType.ENERGETIC -> "Màu xanh ngọc - Bạn đang rất năng động! 💪"
            null -> "Thêm bữa ăn để xem phân tích màu sắc cảm xúc!"
        }
        
        return LocalColorAnalysis(
            dominantMood = dominantMood,
            colorDistribution = colorDistribution,
            insight = insight,
            suggestion = suggestion
        )
    }
    
    /**
     * Refresh all analysis
     */
    fun refreshAllAI() {
        generateAIInsight()
        analyzeColorPalette()
    }
    
    // ==================== HELPERS ====================
    
    private fun isToday(timestamp: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp }
        val cal2 = Calendar.getInstance()
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    
    private fun calculateStreak(entries: List<FoodEntry>): Int {
        if (entries.isEmpty()) return 0
        
        val entriesByDay = entries.groupBy { entry ->
            val cal = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
        }
        
        var streak = 0
        val checkDate = Calendar.getInstance()
        
        while (true) {
            val key = "${checkDate.get(Calendar.YEAR)}-${checkDate.get(Calendar.DAY_OF_YEAR)}"
            if (entriesByDay.containsKey(key)) {
                streak++
                checkDate.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        
        return streak
    }
}

// ==================== STATE CLASSES ====================

sealed class MoodInsightState {
    data object Idle : MoodInsightState()
    data object Loading : MoodInsightState()
    data class Success(val analysis: MoodAnalysis) : MoodInsightState()
    data class Error(val message: String) : MoodInsightState()
}

sealed class ColorAnalysisState {
    data object Idle : ColorAnalysisState()
    data object Loading : ColorAnalysisState()
    data class Success(val analysis: LocalColorAnalysis) : ColorAnalysisState()
    data class Error(val message: String) : ColorAnalysisState()
}
