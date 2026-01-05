package com.haphuongquynh.foodmooddiary.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthState
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthViewModel
import com.haphuongquynh.foodmooddiary.ui.theme.*

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    oobCode: String,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    val authState by viewModel.authState.collectAsState()
    val focusManager = LocalFocusManager.current

    // Handle auth state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                successMessage = "Mật khẩu đã được đổi thành công! Vui lòng đăng nhập."
                errorMessage = null
            }
            is AuthState.Error -> {
                errorMessage = (authState as AuthState.Error).message
                successMessage = null
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Logo and title
            Text(
                text = "FoodMoodDiary",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = "Ghi chép bữa ăn. Hiểu rõ cảm xúc.",
                fontSize = 13.sp,
                color = PastelGreenLight,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Title
            Text(
                text = "Đặt lại mật khẩu",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = "Vui lòng nhập mật khẩu mới của bạn",
                fontSize = 13.sp,
                color = Color(0xFFAAAAAA),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // New password field
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Mật khẩu mới",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { 
                        newPassword = it
                        errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                    ),
                    shape = RoundedCornerShape(8.dp),
                    visualTransformation = if (passwordVisible) 
                        VisualTransformation.None 
                    else 
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility 
                                else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password visibility",
                                tint = Color.Gray
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm password field
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Xác nhận mật khẩu",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { 
                        confirmPassword = it
                        errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                    ),
                    shape = RoundedCornerShape(8.dp),
                    visualTransformation = if (confirmPasswordVisible) 
                        VisualTransformation.None 
                    else 
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                if (confirmPasswordVisible) Icons.Default.Visibility 
                                else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password visibility",
                                tint = Color.Gray
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { 
                            focusManager.clearFocus()
                            if (newPassword.isNotBlank() && confirmPassword.isNotBlank()) {
                                if (newPassword == confirmPassword) {
                                    viewModel.confirmPasswordReset(oobCode, newPassword)
                                } else {
                                    errorMessage = "Mật khẩu không khớp"
                                }
                            }
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Success message
            if (successMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = PastelGreen.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = successMessage!!,
                        color = PastelGreen,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Error message
            if (errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorRed.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = errorMessage!!,
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Reset password button
            Button(
                onClick = { 
                    if (newPassword.isBlank() || confirmPassword.isBlank()) {
                        errorMessage = "Vui lòng nhập đầy đủ thông tin"
                    } else if (newPassword != confirmPassword) {
                        errorMessage = "Mật khẩu không khớp"
                    } else if (newPassword.length < 6) {
                        errorMessage = "Mật khẩu phải có ít nhất 6 ký tự"
                    } else {
                        viewModel.confirmPasswordReset(oobCode, newPassword)
                    }
                },
                enabled = authState !is AuthState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PastelGreen,
                    contentColor = BlackPrimary,
                    disabledContainerColor = CharcoalGray,
                    disabledContentColor = GrayText
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = if (successMessage != null) "Quay lại đăng nhập" else "Đặt lại mật khẩu",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Navigate to login after success
            if (successMessage != null) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    navController.navigate("login") {
                        popUpTo("reset_password") { inclusive = true }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
