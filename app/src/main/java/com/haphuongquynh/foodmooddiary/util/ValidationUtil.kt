package com.haphuongquynh.foodmooddiary.util

/**
 * Validation utility for user input
 */
object ValidationUtil {
    
    /**
     * Validate email format
     * Returns true if email is valid, false otherwise
     */
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        
        // Check basic email format
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"
        if (!email.matches(emailRegex.toRegex())) return false
        
        // Check for valid domain
        val parts = email.split("@")
        if (parts.size != 2) return false
        
        val domain = parts[1]
        // Domain should have at least one dot and valid format
        if (!domain.contains(".") || domain.startsWith(".") || domain.endsWith(".")) {
            return false
        }
        
        // Check domain extension (at least 2 characters)
        val domainParts = domain.split(".")
        if (domainParts.last().length < 2) return false
        
        return true
    }
    
    /**
     * Validate username
     * Requirements:
     * - At least 3 characters
     * - At most 30 characters
     * - Only alphanumeric, underscore, and dot allowed
     * - Must start with alphanumeric
     * - Must end with alphanumeric
     */
    fun isValidUsername(username: String): Boolean {
        if (username.isBlank()) return false
        
        // Check length
        if (username.length < 3 || username.length > 30) return false
        
        // Check first and last character (must be alphanumeric)
        if (!username.first().isLetterOrDigit() || !username.last().isLetterOrDigit()) {
            return false
        }
        
        // Check allowed characters (alphanumeric, underscore, dot)
        val usernameRegex = "^[a-zA-Z0-9][a-zA-Z0-9._]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$"
        if (!username.matches(usernameRegex.toRegex())) return false
        
        return true
    }
    
    /**
     * Validate password
     * Requirements:
     * - At least 6 characters
     * - At most 128 characters
     */
    fun isValidPassword(password: String): Boolean {
        if (password.isBlank()) return false
        
        // Check length
        if (password.length < 6 || password.length > 128) return false
        
        return true
    }
    
    /**
     * Get error message for invalid email
     */
    fun getEmailErrorMessage(email: String): String? {
        return when {
            email.isBlank() -> "Email không được để trống"
            !email.contains("@") -> "Email phải chứa ký tự '@'"
            email.split("@").size != 2 -> "Email không hợp lệ"
            !email.split("@")[1].contains(".") -> "Email phải chứa tên miền hợp lệ"
            !isValidEmail(email) -> "Email không hợp lệ. Vui lòng kiểm tra lại"
            else -> null
        }
    }
    
    /**
     * Get error message for invalid username
     */
    fun getUsernameErrorMessage(username: String): String? {
        return when {
            username.isBlank() -> "Username không được để trống"
            username.length < 3 -> "Username phải có ít nhất 3 ký tự"
            username.length > 30 -> "Username không được vượt quá 30 ký tự"
            !username.first().isLetterOrDigit() || !username.last().isLetterOrDigit() -> {
                "Username phải bắt đầu và kết thúc bằng ký tự chữ hoặc số"
            }
            !isValidUsername(username) -> {
                "Username chỉ chứa chữ, số, dấu gạch dưới (_) và dấu chấm (.)"
            }
            else -> null
        }
    }
    
    /**
     * Get error message for invalid password
     */
    fun getPasswordErrorMessage(password: String): String? {
        return when {
            password.isBlank() -> "Mật khẩu không được để trống"
            password.length < 6 -> "Mật khẩu phải có ít nhất 6 ký tự"
            password.length > 128 -> "Mật khẩu không được vượt quá 128 ký tự"
            else -> null
        }
    }
}
