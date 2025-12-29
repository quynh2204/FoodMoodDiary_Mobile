package com.haphuongquynh.foodmooddiary.util.common

/**
 * A generic wrapper class for handling success, error, and loading states
 * Used throughout the app for consistent state management
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val exception: Exception? = null) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
    
    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        fun error(message: String, exception: Exception? = null): Resource<Nothing> = Error(message, exception)
        fun loading(): Resource<Nothing> = Loading
    }
}

/**
 * Extension function to check if resource is successful
 */
fun <T> Resource<T>.isSuccess(): Boolean = this is Resource.Success

/**
 * Extension function to check if resource is error
 */
fun <T> Resource<T>.isError(): Boolean = this is Resource.Error

/**
 * Extension function to check if resource is loading
 */
fun <T> Resource<T>.isLoading(): Boolean = this is Resource.Loading

/**
 * Extension function to get data or null
 */
fun <T> Resource<T>.getDataOrNull(): T? = when (this) {
    is Resource.Success -> data
    else -> null
}
