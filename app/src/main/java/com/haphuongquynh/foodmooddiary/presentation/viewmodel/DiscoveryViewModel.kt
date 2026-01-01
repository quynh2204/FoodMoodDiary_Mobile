package com.haphuongquynh.foodmooddiary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haphuongquynh.foodmooddiary.domain.model.FoodEntry
import com.haphuongquynh.foodmooddiary.domain.model.Meal
import com.haphuongquynh.foodmooddiary.domain.repository.FoodEntryRepository
import com.haphuongquynh.foodmooddiary.domain.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel for Discovery feature
 *
 * - Giữ nguyên logic Favorites cho tab "Món đã lưu"
 * - Tab "Khám phá món ăn" dùng danh sách 20 món Việt cố định (local)
 */
@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val foodEntryRepository: FoodEntryRepository
) : ViewModel() {

    /* =============================
       OLD STATES (giữ để không phá các nơi khác đang gọi)
       ============================= */

    private val _currentMeal = MutableStateFlow<Meal?>(null)
    val currentMeal: StateFlow<Meal?> = _currentMeal.asStateFlow()

    private val _favorites = MutableStateFlow<List<Meal>>(emptyList())
    val favorites: StateFlow<List<Meal>> = _favorites.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Meal>>(emptyList())
    val searchResults: StateFlow<List<Meal>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _areas = MutableStateFlow<List<String>>(emptyList())
    val areas: StateFlow<List<String>> = _areas.asStateFlow()

    /* =============================
       NEW STATES for Vietnamese Discover
       ============================= */

    enum class VietnamMealType(val displayName: String) {
        MON_NUOC("Món nước"),
        MON_KHO("Món khô"),
        TRANG_MIENG("Tráng miệng")
    }

    data class VietnamMeal(
        val id: String,
        val name: String,
        val type: VietnamMealType,
        val thumbUrl: String,
        val youtubeUrl: String
    )

    data class RecommendedMeal(
        val meal: VietnamMeal,
        val reason: String,
        val score: Float
    )

    private val _vietnamMeals = MutableStateFlow<List<VietnamMeal>>(emptyList())
    val vietnamMeals: StateFlow<List<VietnamMeal>> = _vietnamMeals.asStateFlow()

    private val _filteredVietnamMeals = MutableStateFlow<List<VietnamMeal>>(emptyList())
    val filteredVietnamMeals: StateFlow<List<VietnamMeal>> = _filteredVietnamMeals.asStateFlow()

    private val _selectedMainCategory = MutableStateFlow("Tất cả")
    val selectedMainCategory: StateFlow<String> = _selectedMainCategory.asStateFlow()

    private val _recommendations = MutableStateFlow<List<RecommendedMeal>>(emptyList())
    val recommendations: StateFlow<List<RecommendedMeal>> = _recommendations.asStateFlow()

    private val _recommendationReason = MutableStateFlow("")
    val recommendationReason: StateFlow<String> = _recommendationReason.asStateFlow()

    init {
        loadFavorites()
        loadVietnamMeals()
        applyVietnamFilter("Tất cả")
        generateRecommendations()
        _isLoading.value = false
        _error.value = null
    }

    /* =============================
       Vietnamese Discover logic
       ============================= */

    fun setVietnamCategory(category: String) {
        _selectedMainCategory.value = category
        applyVietnamFilter(category)
    }

    private fun applyVietnamFilter(category: String) {
        val all = _vietnamMeals.value
        val filtered = when (category) {
            "Món nước" -> all.filter { it.type == VietnamMealType.MON_NUOC }
            "Món khô" -> all.filter { it.type == VietnamMealType.MON_KHO }
            "Tráng miệng" -> all.filter { it.type == VietnamMealType.TRANG_MIENG }
            else -> all
        }
        _filteredVietnamMeals.value = filtered
    }

    /* =============================
       Recommendation Algorithm
       ============================= */

    fun generateRecommendations() {
        viewModelScope.launch {
            try {
                val entries = foodEntryRepository.getAllEntries().first()
                val allMeals = _vietnamMeals.value

                if (entries.isEmpty() || allMeals.isEmpty()) {
                    // New user - show popular items
                    _recommendations.value = allMeals.take(6).map {
                        RecommendedMeal(it, "Món ăn phổ biến", 0.5f)
                    }
                    _recommendationReason.value = "Khám phá những món ăn Việt Nam phổ biến"
                    return@launch
                }

                val recommendations = computeRecommendations(entries, allMeals)
                _recommendations.value = recommendations
                _recommendationReason.value = "Dựa trên lịch sử ăn uống của bạn"
            } catch (e: Exception) {
                // Fallback to popular items
                _recommendations.value = _vietnamMeals.value.take(6).map {
                    RecommendedMeal(it, "Món ăn phổ biến", 0.5f)
                }
                _recommendationReason.value = "Khám phá những món ăn Việt Nam phổ biến"
            }
        }
    }

    private fun computeRecommendations(
        entries: List<FoodEntry>,
        allMeals: List<VietnamMeal>
    ): List<RecommendedMeal> {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        // Get user's recent food names (lowercase for matching)
        val recentFoods = entries.take(20).map { it.foodName.lowercase() }
        val foodFrequency = recentFoods.groupingBy { it }.eachCount()

        // Determine preferred type based on time of day
        val timeBasedType = when (currentHour) {
            in 6..10 -> VietnamMealType.MON_NUOC      // Breakfast - soups
            in 11..14 -> VietnamMealType.MON_KHO      // Lunch - main dishes
            in 15..17 -> VietnamMealType.TRANG_MIENG  // Afternoon - desserts
            in 18..21 -> VietnamMealType.MON_KHO      // Dinner - main dishes
            else -> VietnamMealType.MON_NUOC          // Late night - soups
        }

        return allMeals.map { meal ->
            var score = 0f
            var reason = ""

            // 1. Food name matching (40% weight)
            val nameMatch = recentFoods.any { recent ->
                meal.name.lowercase().contains(recent) ||
                recent.contains(meal.name.lowercase().take(4))
            }
            if (nameMatch) {
                score += 0.4f
                reason = "Tương tự món bạn đã ăn"
            }

            // 2. Time-based recommendation (30% weight)
            if (meal.type == timeBasedType) {
                score += 0.3f
                if (reason.isEmpty()) {
                    reason = when (timeBasedType) {
                        VietnamMealType.MON_NUOC -> "Phù hợp cho bữa sáng"
                        VietnamMealType.MON_KHO -> "Phù hợp cho bữa chính"
                        VietnamMealType.TRANG_MIENG -> "Thích hợp cho buổi chiều"
                    }
                }
            }

            // 3. Variety bonus (30% weight) - items not eaten recently
            val notRecentlyEaten = !recentFoods.any { recent ->
                meal.name.lowercase().contains(recent) ||
                recent.contains(meal.name.lowercase())
            }
            if (notRecentlyEaten) {
                score += 0.3f
                if (reason.isEmpty()) {
                    reason = "Thử món mới"
                }
            }

            // Default reason
            if (reason.isEmpty()) {
                reason = "Gợi ý cho bạn"
            }

            RecommendedMeal(meal, reason, score)
        }.sortedByDescending { it.score }.take(6)
    }

    private fun loadVietnamMeals() {
        _vietnamMeals.value = listOf(
            // ═══════════════════════════════════════════
            // MÓN NƯỚC (8) - Soup/Noodle dishes
            // ═══════════════════════════════════════════
            VietnamMeal(
                id = "pho_bo",
                name = "Phở bò",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?q=80&w=1674&auto=format&fit=crop",
                youtubeUrl = "https://www.youtube.com/watch?v=99tOr7JSr0k"
            ),
            VietnamMeal(
                id = "bun_bo_hue",
                name = "Bún bò Huế",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1597345637412-9fd611e758f3?q=80&w=2070&auto=format&fit=crop",
                youtubeUrl = "https://www.youtube.com/watch?v=A_o2qfaTgKs"
            ),
            VietnamMeal(
                id = "bun_rieu",
                name = "Bún riêu cua",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://cdn.tgdd.vn/2020/08/CookProduct/Untitled-1-1200x676-10.jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=CiPDEMxUfTY"
            ),
            VietnamMeal(
                id = "mi_quang",
                name = "Mì Quảng",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://daivietourist.vn/wp-content/uploads/2025/06/mi-quang-hoi-an-2.jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=g3V_oNeMdHs"
            ),
            VietnamMeal(
                id = "hu_tieu",
                name = "Hủ tiếu Nam Vang",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://i-giadinh.vnecdn.net/2023/05/15/Buoc-8-Thanh-pham-1-8-8366-1684125654.jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=fziqSn-xkws"
            ),
            VietnamMeal(
                id = "banh_canh",
                name = "Bánh canh cua",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://cdn.tgdd.vn/2021/05/CookProduct/thumbcmscn-1200x676-4.jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=uTIZmap4UnQ"
            ),
            VietnamMeal(
                id = "bun_cha",
                name = "Bún chả Hà Nội",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://cdn.zsoft.solutions/poseidon-web/app/media/uploaded-files/090724-bun-cha-ha-noi-buffet-poseidon-1.jpeg",
                youtubeUrl = "https://www.youtube.com/watch?v=tcbSm6nSUhM"
            ),
            VietnamMeal(
                id = "bun_thit_nuong",
                name = "Bún thịt nướng",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://cooponline.vn/tin-tuc/wp-content/uploads/2025/10/cach-lam-bun-thit-nuong-chuan-vi-sai-gon-thom-ngon-dam-da-kho-cuong.png",
                youtubeUrl = "https://www.youtube.com/watch?v=emlDazeUIMM"
            ),

            // ═══════════════════════════════════════════
            // MÓN KHÔ (8) - Main dishes
            // ═══════════════════════════════════════════
            VietnamMeal(
                id = "com_tam",
                name = "Cơm tấm sườn bì chả",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1559847844-5315695dadae?auto=format&fit=crop&w=800&q=80",
                youtubeUrl = "https://www.youtube.com/watch?v=M7gLg1z3M6w"
            ),
            VietnamMeal(
                id = "banh_mi",
                name = "Bánh mì thịt",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1600454309261-3e59309e23af?auto=format&fit=crop&w=800&q=80",
                youtubeUrl = "https://www.youtube.com/watch?v=RkXJx5Q6VQk"
            ),
            VietnamMeal(
                id = "goi_cuon",
                name = "Gỏi cuốn tôm thịt",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?auto=format&fit=crop&w=800&q=80",
                youtubeUrl = "https://www.youtube.com/watch?v=y5HLDhPQCp4"
            ),
            VietnamMeal(
                id = "bo_luc_lac",
                name = "Bò lúc lắc",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=800&q=80",
                youtubeUrl = "https://www.youtube.com/watch?v=yC-IhU5u5jw"
            ),
            VietnamMeal(
                id = "ca_kho_to",
                name = "Cá kho tộ",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1467003909585-2f8a72700288?auto=format&fit=crop&w=800&q=80",
                youtubeUrl = "https://www.youtube.com/watch?v=cNJBKvYBlew"
            ),
            VietnamMeal(
                id = "thit_kho_trung",
                name = "Thịt kho trứng",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1598515214211-89d3c73ae83b?auto=format&fit=crop&w=800&q=80",
                youtubeUrl = "https://www.youtube.com/watch?v=B_xfD9q9H-0"
            ),
            VietnamMeal(
                id = "bun_dau",
                name = "Bún đậu mắm tôm",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1562967916-eb82221dfb92?auto=format&fit=crop&w=800&q=80",
                youtubeUrl = "https://www.youtube.com/watch?v=E_5BpLk7bR8"
            ),
            VietnamMeal(
                id = "com_suon",
                name = "Cơm sườn nướng",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&w=800&q=80",
                youtubeUrl = "https://www.youtube.com/watch?v=_6v_LY2_nrE"
            ),

            // ═══════════════════════════════════════════
            // TRÁNG MIỆNG (6) - Desserts
            // ═══════════════════════════════════════════
            VietnamMeal(
                id = "che_ba_mau",
                name = "Chè ba màu",
                type = VietnamMealType.TRANG_MIENG,
                thumbUrl = "https://images.unsplash.com/photo-1563805042-7684c019e1cb?auto=format&fit=crop&w=800&q=80",
                youtubeUrl = "https://www.youtube.com/watch?v=nQvQB8cYJXA"
            ),
            VietnamMeal(
                id = "banh_flan",
                name = "Bánh flan caramen",
                type = VietnamMealType.TRANG_MIENG,
                thumbUrl = "https://images.unsplash.com/photo-1528207776546-365bb710ee93?auto=format&fit=crop&w=800&q=80",
                youtubeUrl = "https://www.youtube.com/watch?v=dKEPJgHMDQg"
            ),
            VietnamMeal(
                id = "banh_da_lon",
                name = "Bánh da lợn",
                type = VietnamMealType.TRANG_MIENG,
                thumbUrl = "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?auto=format&fit=crop&w=800&q=80",
                youtubeUrl = "https://www.youtube.com/watch?v=GLpSElmgDCs"
            ),
            VietnamMeal(
                id = "banh_troi",
                name = "Bánh trôi bánh chay",
                type = VietnamMealType.TRANG_MIENG,
                thumbUrl = "https://images.unsplash.com/photo-1551024601-bec78aea704b?auto=format&fit=crop&w=800&q=80",
                youtubeUrl = "https://www.youtube.com/watch?v=VvPXqDvD3hQ"
            ),
            VietnamMeal(
                id = "banh_xeo",
                name = "Bánh xèo miền Tây",
                type = VietnamMealType.TRANG_MIENG,
                thumbUrl = "https://images.unsplash.com/photo-1590301157890-4810ed352733?auto=format&fit=crop&w=800&q=80",
                youtubeUrl = "https://www.youtube.com/watch?v=n5mJMu-tVno"
            ),
            VietnamMeal(
                id = "banh_beo",
                name = "Bánh bèo Huế",
                type = VietnamMealType.TRANG_MIENG,
                thumbUrl = "https://images.unsplash.com/photo-1565557623262-b51c2513a641?auto=format&fit=crop&w=800&q=80",
                youtubeUrl = "https://www.youtube.com/watch?v=EF1NrJlrz1I"
            )
        )
    }

    /* =============================
       Favorites (GIỮ NGUYÊN)
       ============================= */

    fun toggleFavorite(meal: Meal) {
        viewModelScope.launch {
            if (meal.isFavorite) {
                mealRepository.removeFromFavorites(meal.id)
                    .onSuccess {
                        _currentMeal.value = _currentMeal.value?.copy(isFavorite = false)
                        loadFavorites()
                    }
                    .onFailure { exception ->
                        _error.value = exception.message ?: "Failed to remove from favorites"
                    }
            } else {
                mealRepository.addToFavorites(meal)
                    .onSuccess {
                        _currentMeal.value = _currentMeal.value?.copy(isFavorite = true)
                        loadFavorites()
                    }
                    .onFailure { exception ->
                        _error.value = exception.message ?: "Failed to add to favorites"
                    }
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            mealRepository.getFavoriteMeals().collect { meals ->
                _favorites.value = meals
            }
        }
    }

    /* =============================
       Utils (GIỮ NGUYÊN)
       ============================= */

    fun clearError() {
        _error.value = null
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    /* =============================
       OPTIONAL: nếu còn màn khác dùng API, bà có thể bật lại
       (hiện tại tab khám phá mới không cần)
       ============================= */

    fun loadRandomMeal() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            mealRepository.getRandomMeal()
                .onSuccess { meal ->
                    _currentMeal.value = meal
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load random meal"
                    _isLoading.value = false
                }
        }
    }

    fun searchMealsByName(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            mealRepository.searchMealsByName(query)
                .onSuccess { meals ->
                    _searchResults.value = meals
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to search meals"
                    _isLoading.value = false
                }
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            mealRepository.filterByCategory(category)
                .onSuccess { meals ->
                    _searchResults.value = meals
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to filter by category"
                    _isLoading.value = false
                }
        }
    }

    fun filterByArea(area: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            mealRepository.filterByArea(area)
                .onSuccess { meals ->
                    _searchResults.value = meals
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to filter by area"
                    _isLoading.value = false
                }
        }
    }

    fun loadMealById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            mealRepository.getMealById(id)
                .onSuccess { meal ->
                    _currentMeal.value = meal
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load meal"
                    _isLoading.value = false
                }
        }
    }
}
