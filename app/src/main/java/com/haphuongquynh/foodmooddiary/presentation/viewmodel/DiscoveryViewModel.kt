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

    // Saved Vietnamese meals (local favorites)
    private val _savedVietnamMeals = MutableStateFlow<Set<String>>(emptySet())
    val savedVietnamMeals: StateFlow<Set<String>> = _savedVietnamMeals.asStateFlow()

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
       Saved Vietnamese Meals
       ============================= */

    fun toggleSaveVietnamMeal(mealId: String) {
        val current = _savedVietnamMeals.value.toMutableSet()
        if (current.contains(mealId)) {
            current.remove(mealId)
        } else {
            current.add(mealId)
        }
        _savedVietnamMeals.value = current
    }

    fun isVietnamMealSaved(mealId: String): Boolean {
        return _savedVietnamMeals.value.contains(mealId)
    }

    fun getSavedVietnamMealsList(): List<VietnamMeal> {
        return _vietnamMeals.value.filter { _savedVietnamMeals.value.contains(it.id) }
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
                thumbUrl = "https://i-giadinh.vnecdn.net/2024/03/07/7Honthinthnhphm1-1709800144-8583-1709800424.jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=OVb5uoDWspM"
            ),
            VietnamMeal(
                id = "banh_mi",
                name = "Bánh mì thịt",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://www.huongnghiepaau.com/wp-content/uploads/2019/08/banh-mi-kep-thit-nuong-thom-phuc.jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=5dX-hXxKM8E"
            ),
            VietnamMeal(
                id = "goi_cuon",
                name = "Gỏi cuốn tôm thịt",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://cdn.netspace.edu.vn/images/2020/04/25/cach-lam-goi-cuon-tom-thit-cuc-ki-hap-dan-245587-800.jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=LJ_3BeqH63w"
            ),
            VietnamMeal(
                id = "bo_luc_lac",
                name = "Bò lúc lắc",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://cdn.netspace.edu.vn/images/2020/04/28/cach-lam-bo-luc-lac-ngon-244971-800.jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=0X5m98q3Pn0"
            ),
            VietnamMeal(
                id = "ca_kho_to",
                name = "Cá kho tộ",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://cdnv2.tgdd.vn/mwg-static/common/Common/05052025%20-%202025-05-09T154044.858.jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=zvlct2ZXhj8"
            ),
            VietnamMeal(
                id = "thit_kho_trung",
                name = "Thịt kho trứng",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://cdn.tgdd.vn/Files/2017/03/28/965845/cach-lam-thit-kho-trung-5_760x450.jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=Ef2KDf7x1BY"
            ),
            VietnamMeal(
                id = "bun_dau",
                name = "Bún đậu mắm tôm",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://cdn.tgdd.vn/2021/12/CookRecipe/GalleryStep/thanh-pham-1032.jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=wiy_GKcrkxk"
            ),
            VietnamMeal(
                id = "com_suon",
                name = "Cơm sườn nướng",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://cdn.tgdd.vn/2021/08/CookProduct/t1-1200x676.jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=cJu6tFJe_Gc"
            ),

            // ═══════════════════════════════════════════
            // TRÁNG MIỆNG (6) - Desserts
            // ═══════════════════════════════════════════
            VietnamMeal(
                id = "che_ba_mau",
                name = "Chè ba màu",
                type = VietnamMealType.TRANG_MIENG,
                thumbUrl = "https://helenrecipes.com/wp-content/uploads/2015/03/IMG_9091.jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=x9pUWnYw470"
            ),
            VietnamMeal(
                id = "banh_flan",
                name = "Bánh flan caramen",
                type = VietnamMealType.TRANG_MIENG,
                thumbUrl = "https://chefdzung.com.vn/uploads/images/ngoc-linh/banh-caramen.jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=xBCp5-bzfZQ"
            ),
            VietnamMeal(
                id = "banh_da_lon",
                name = "Bánh da lợn",
                type = VietnamMealType.TRANG_MIENG,
                thumbUrl = "https://upload.wikimedia.org/wikipedia/commons/b/b3/B%C3%A1nh_da_l%C6%A1n_%C4%91%E1%BA%ADu_xanh_v%C3%A0_l%C3%A1_d%E1%BB%A9a..jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=8_xg7sOx3ys"
            ),
            VietnamMeal(
                id = "banh_troi",
                name = "Bánh trôi bánh chay",
                type = VietnamMealType.TRANG_MIENG,
                thumbUrl = "https://upload.wikimedia.org/wikipedia/commons/b/ba/Banhchay.JPG",
                youtubeUrl = "https://www.youtube.com/watch?v=hVa8e9zQI6U"
            ),
            VietnamMeal(
                id = "banh_xeo",
                name = "Bánh xèo miền Tây",
                type = VietnamMealType.TRANG_MIENG,
                thumbUrl = "https://i-giadinh.vnecdn.net/2023/09/19/Buoc-10-Thanh-pham-1-1-5225-1695107554.jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=GOUmS6kRoGw"
            ),
            VietnamMeal(
                id = "banh_beo",
                name = "Bánh bèo Huế",
                type = VietnamMealType.TRANG_MIENG,
                thumbUrl = "https://daylambanh.edu.vn/wp-content/uploads/2017/08/luu-y-khi-lam-banh-beo-hue.jpg",
                youtubeUrl = "https://www.youtube.com/watch?v=rafBOn2wOS0"
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
