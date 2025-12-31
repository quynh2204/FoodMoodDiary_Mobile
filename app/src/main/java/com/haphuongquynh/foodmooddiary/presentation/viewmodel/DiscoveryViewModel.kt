package com.haphuongquynh.foodmooddiary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haphuongquynh.foodmooddiary.domain.model.Meal
import com.haphuongquynh.foodmooddiary.domain.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Discovery feature
 *
 * - Giữ nguyên logic Favorites cho tab "Món đã lưu"
 * - Tab "Khám phá món ăn" dùng danh sách 20 món Việt cố định (local)
 */
@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val mealRepository: MealRepository
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

    enum class VietnamMealType { MON_NUOC, MON_KHO, MON_BANH }

    enum class VietnamCakeSubType { BANH_DAN_GIAN, BANH_VIET_NAM }

    data class VietnamMeal(
        val name: String,
        val type: VietnamMealType,
        val subType: VietnamCakeSubType? = null,
        val thumbUrl: String,
        val youtubeUrl: String,
    )

    private val _vietnamMeals = MutableStateFlow<List<VietnamMeal>>(emptyList())
    val vietnamMeals: StateFlow<List<VietnamMeal>> = _vietnamMeals.asStateFlow()

    private val _filteredVietnamMeals = MutableStateFlow<List<VietnamMeal>>(emptyList())
    val filteredVietnamMeals: StateFlow<List<VietnamMeal>> = _filteredVietnamMeals.asStateFlow()

    private val _selectedMainCategory = MutableStateFlow("Tất cả") // Tất cả / Món nước / Món khô / Món bánh
    val selectedMainCategory: StateFlow<String> = _selectedMainCategory.asStateFlow()

    private val _selectedCakeSubCategory = MutableStateFlow("Tất cả") // Tất cả / Bánh dân gian / Bánh Việt Nam
    val selectedCakeSubCategory: StateFlow<String> = _selectedCakeSubCategory.asStateFlow()

    init {
        // Giữ nguyên tab Saved Meals
        loadFavorites()

        // Khám phá mới (local)
        loadVietnamMeals()
        applyVietnamFilter("Tất cả", "Tất cả")

        // Không auto gọi API random/categories/areas nữa để tránh loading + call thừa
        _isLoading.value = false
        _error.value = null
    }

    /* =============================
       Vietnamese Discover logic
       ============================= */

    fun setVietnamCategory(main: String) {
        _selectedMainCategory.value = main
        if (main != "Món bánh") {
            _selectedCakeSubCategory.value = "Tất cả"
        }
        applyVietnamFilter(_selectedMainCategory.value, _selectedCakeSubCategory.value)
    }

    fun setVietnamCakeSubCategory(sub: String) {
        _selectedCakeSubCategory.value = sub
        applyVietnamFilter(_selectedMainCategory.value, _selectedCakeSubCategory.value)
    }

    private fun applyVietnamFilter(main: String, sub: String) {
        val all = _vietnamMeals.value

        val filtered = when (main) {
            "Món nước" -> all.filter { it.type == VietnamMealType.MON_NUOC }
            "Món khô" -> all.filter { it.type == VietnamMealType.MON_KHO }
            "Món bánh" -> {
                when (sub) {
                    "Bánh dân gian" -> all.filter {
                        it.type == VietnamMealType.MON_BANH && it.subType == VietnamCakeSubType.BANH_DAN_GIAN
                    }
                    "Bánh Việt Nam" -> all.filter {
                        it.type == VietnamMealType.MON_BANH && it.subType == VietnamCakeSubType.BANH_VIET_NAM
                    }
                    else -> all.filter { it.type == VietnamMealType.MON_BANH }
                }
            }
            else -> all
        }

        _filteredVietnamMeals.value = filtered
    }

    private fun loadVietnamMeals() {
        _vietnamMeals.value = listOf(
            // MÓN NƯỚC (8)
            VietnamMeal(
                name = "Hủ tiếu Nam Vang",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=c%C3%A1ch+l%C3%A0m+h%E1%BB%A7+ti%E1%BA%BFu+nam+vang"
            ),
            VietnamMeal(
                name = "Bánh canh cua",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1604908176997-125f25cc500f?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=b%C3%A1nh+canh+cua+s%C3%A0i+g%C3%B2n+ngon"
            ),
            VietnamMeal(
                name = "Phở bò",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1555126634-323283e090fa?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=c%C3%A1ch+l%C3%A0m+ph%E1%BB%9F+b%C3%B2"
            ),
            VietnamMeal(
                name = "Bún bò Huế",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1617093727343-374698b1b08d?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=b%C3%BAn+b%C3%B2+hu%E1%BA%BF+c%C3%A1ch+l%C3%A0m"
            ),
            VietnamMeal(
                name = "Bún riêu",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1604908554027-1b0bff98982f?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=c%C3%A1ch+l%C3%A0m+b%C3%BAn+ri%C3%AAu"
            ),
            VietnamMeal(
                name = "Mì Quảng",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1526318896980-cf78c088247c?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=m%C3%AC+qu%E1%BA%A3ng+c%C3%A1ch+l%C3%A0m"
            ),
            VietnamMeal(
                name = "Bún chả",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1550367363-29a61d1a67a4?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=b%C3%BAn+ch%E1%BA%A3+h%C3%A0+n%E1%BB%99i+ngon"
            ),
            VietnamMeal(
                name = "Bún thịt nướng",
                type = VietnamMealType.MON_NUOC,
                thumbUrl = "https://images.unsplash.com/photo-1541542684-4b26f1c8b3b9?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=b%C3%BAn+th%E1%BB%8Bt+n%C6%B0%E1%BB%9Bng+c%C3%A1ch+l%C3%A0m"
            ),

            // MÓN KHÔ (8)
            VietnamMeal(
                name = "Cơm sườn (Sài Gòn)",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=top+qu%C3%A1n+c%C6%A1m+s%C6%B0%E1%BB%9Dn+s%C3%A0i+g%C3%B2n"
            ),
            VietnamMeal(
                name = "Cơm tấm bì chả",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1559847844-5315695dadae?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=c%C3%A1ch+l%C3%A0m+c%C6%A1m+t%E1%BA%A5m+s%C6%B0%E1%BB%9Dn+b%C3%AC+ch%E1%BA%A3"
            ),
            VietnamMeal(
                name = "Bánh mì thịt",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=top+b%C3%A1nh+m%C3%AC+vi%E1%BB%87t+nam+ngon"
            ),
            VietnamMeal(
                name = "Gỏi cuốn",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=c%C3%A1ch+l%C3%A0m+g%E1%BB%8Fi+cu%E1%BB%91n"
            ),
            VietnamMeal(
                name = "Bò lúc lắc",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1604908176997-125f25cc500f?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=b%C3%B2+l%C3%BAc+l%E1%BA%AFc+c%C3%A1ch+l%C3%A0m"
            ),
            VietnamMeal(
                name = "Cá kho tộ",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1559847844-5315695dadae?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=c%C3%A1+kho+t%E1%BB%99+c%C3%A1ch+l%C3%A0m"
            ),
            VietnamMeal(
                name = "Thịt kho trứng",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=th%E1%BB%8Bt+kho+tr%E1%BB%A9ng+c%C3%A1ch+l%C3%A0m"
            ),
            VietnamMeal(
                name = "Bún đậu mắm tôm",
                type = VietnamMealType.MON_KHO,
                thumbUrl = "https://images.unsplash.com/photo-1550367363-29a61d1a67a4?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=b%C3%BAn+%C4%91%E1%BA%ADu+m%E1%BA%AFm+t%C3%B4m+ngon"
            ),

            // MÓN BÁNH (4)
            VietnamMeal(
                name = "Bánh da lợn",
                type = VietnamMealType.MON_BANH,
                subType = VietnamCakeSubType.BANH_DAN_GIAN,
                thumbUrl = "https://images.unsplash.com/photo-1541781286675-1f2a65d4a78d?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=c%C3%A1ch+l%C3%A0m+b%C3%A1nh+da+l%E1%BB%A3n"
            ),
            VietnamMeal(
                name = "Bánh trôi – bánh chay",
                type = VietnamMealType.MON_BANH,
                subType = VietnamCakeSubType.BANH_DAN_GIAN,
                thumbUrl = "https://images.unsplash.com/photo-1551024601-bec78aea704b?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=c%C3%A1ch+l%C3%A0m+b%C3%A1nh+tr%C3%B4i+b%C3%A1nh+chay"
            ),
            VietnamMeal(
                name = "Bánh xèo",
                type = VietnamMealType.MON_BANH,
                subType = VietnamCakeSubType.BANH_VIET_NAM,
                thumbUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=b%C3%A1nh+x%C3%A8o+mi%E1%BB%81n+t%C3%A2y+c%C3%A1ch+l%C3%A0m"
            ),
            VietnamMeal(
                name = "Bánh bèo",
                type = VietnamMealType.MON_BANH,
                subType = VietnamCakeSubType.BANH_VIET_NAM,
                thumbUrl = "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?auto=format&fit=crop&w=1200&q=60",
                youtubeUrl = "https://www.youtube.com/results?search_query=b%C3%A1nh+b%C3%A8o+hu%E1%BA%BF+c%C3%A1ch+l%C3%A0m"
            ),
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
