package com.haphuongquynh.foodmooddiary.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthState
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthViewModel
import com.haphuongquynh.foodmooddiary.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    val authState by viewModel.authState.collectAsState()
    val focusManager = LocalFocusManager.current

    // Handle auth state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.PasswordResetSent -> {
                successMessage = "Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư của bạn."
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
            
            // Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
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
                text = "Quên mật khẩu",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = "Nhập email của bạn để nhận liên kết đặt lại mật khẩu",
                fontSize = 13.sp,
                color = Color(0xFFAAAAAA),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Email address field
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Địa chỉ email",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        errorMessage = null
                        successMessage = null
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
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { 
                            focusManager.clearFocus()
                            if (email.isNotBlank()) {
                                viewModel.sendPasswordResetEmail(email)
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

            // Send reset link button
            Button(
                onClick = { 
                    viewModel.sendPasswordResetEmail(email) 
                },
                enabled = authState !is AuthState.Loading && email.isNotBlank(),
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
                        text = "Gửi liên kết đặt lại",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Back to login link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Đã nhớ mật khẩu?",
                    fontSize = 14.sp,
                    color = Color(0xFFAAAAAA)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Đăng nhập",
                    fontSize = 14.sp,
                    color = PastelGreen,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
