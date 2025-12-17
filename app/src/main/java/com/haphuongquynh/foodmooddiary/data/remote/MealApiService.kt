package com.haphuongquynh.foodmooddiary.data.remote

import com.haphuongquynh.foodmooddiary.domain.model.Meal
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * TheMealDB API Service - Day 24
 * Free API for discovering meal recipes
 * Base URL: https://www.themealdb.com/api/json/v1/1/
 */
interface MealApiService {

    /**
     * Get a random meal
     */
    @GET("random.php")
    suspend fun getRandomMeal(): Response<MealResponse>

    /**
     * Search meals by name
     */
    @GET("search.php")
    suspend fun searchMealsByName(@Query("s") name: String): Response<MealResponse>

    /**
     * Search meals by first letter
     */
    @GET("search.php")
    suspend fun searchMealsByFirstLetter(@Query("f") letter: String): Response<MealResponse>

    /**
     * Lookup meal by ID
     */
    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id: String): Response<MealResponse>

    /**
     * Filter by category
     */
    @GET("filter.php")
    suspend fun filterByCategory(@Query("c") category: String): Response<MealResponse>

    /**
     * Filter by area (cuisine)
     */
    @GET("filter.php")
    suspend fun filterByArea(@Query("a") area: String): Response<MealResponse>

    /**
     * List all categories
     */
    @GET("categories.php")
    suspend fun getAllCategories(): Response<CategoryResponse>

    /**
     * List all areas
     */
    @GET("list.php?a=list")
    suspend fun getAllAreas(): Response<AreaResponse>
}

/**
 * Response wrapper for meal API
 */
data class MealResponse(
    val meals: List<MealDto>?
)

/**
 * Meal DTO from TheMealDB API
 */
data class MealDto(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String?,
    val strMealThumb: String?,
    val strTags: String?,
    val strYoutube: String?,
    // Ingredients (up to 20)
    val strIngredient1: String?,
    val strIngredient2: String?,
    val strIngredient3: String?,
    val strIngredient4: String?,
    val strIngredient5: String?,
    val strIngredient6: String?,
    val strIngredient7: String?,
    val strIngredient8: String?,
    val strIngredient9: String?,
    val strIngredient10: String?,
    val strIngredient11: String?,
    val strIngredient12: String?,
    val strIngredient13: String?,
    val strIngredient14: String?,
    val strIngredient15: String?,
    val strIngredient16: String?,
    val strIngredient17: String?,
    val strIngredient18: String?,
    val strIngredient19: String?,
    val strIngredient20: String?,
    // Measures
    val strMeasure1: String?,
    val strMeasure2: String?,
    val strMeasure3: String?,
    val strMeasure4: String?,
    val strMeasure5: String?,
    val strMeasure6: String?,
    val strMeasure7: String?,
    val strMeasure8: String?,
    val strMeasure9: String?,
    val strMeasure10: String?,
    val strMeasure11: String?,
    val strMeasure12: String?,
    val strMeasure13: String?,
    val strMeasure14: String?,
    val strMeasure15: String?,
    val strMeasure16: String?,
    val strMeasure17: String?,
    val strMeasure18: String?,
    val strMeasure19: String?,
    val strMeasure20: String?
) {
    /**
     * Convert to domain model
     */
    fun toDomainModel(): Meal {
        val ingredients = mutableListOf<Pair<String, String>>()
        listOf(
            strIngredient1 to strMeasure1,
            strIngredient2 to strMeasure2,
            strIngredient3 to strMeasure3,
            strIngredient4 to strMeasure4,
            strIngredient5 to strMeasure5,
            strIngredient6 to strMeasure6,
            strIngredient7 to strMeasure7,
            strIngredient8 to strMeasure8,
            strIngredient9 to strMeasure9,
            strIngredient10 to strMeasure10,
            strIngredient11 to strMeasure11,
            strIngredient12 to strMeasure12,
            strIngredient13 to strMeasure13,
            strIngredient14 to strMeasure14,
            strIngredient15 to strMeasure15,
            strIngredient16 to strMeasure16,
            strIngredient17 to strMeasure17,
            strIngredient18 to strMeasure18,
            strIngredient19 to strMeasure19,
            strIngredient20 to strMeasure20
        ).forEach { (ingredient, measure) ->
            if (!ingredient.isNullOrBlank() && !measure.isNullOrBlank()) {
                ingredients.add(ingredient.trim() to measure.trim())
            }
        }

        return Meal(
            id = idMeal,
            name = strMeal,
            category = strCategory ?: "",
            area = strArea ?: "",
            instructions = strInstructions ?: "",
            thumbUrl = strMealThumb ?: "",
            tags = strTags?.split(",")?.map { it.trim() } ?: emptyList(),
            youtubeUrl = strYoutube,
            ingredients = ingredients
        )
    }
}

data class CategoryResponse(
    val categories: List<CategoryDto>?
)

data class CategoryDto(
    val idCategory: String,
    val strCategory: String,
    val strCategoryThumb: String?,
    val strCategoryDescription: String?
)

data class AreaResponse(
    val meals: List<AreaDto>?
)

data class AreaDto(
    val strArea: String
)
