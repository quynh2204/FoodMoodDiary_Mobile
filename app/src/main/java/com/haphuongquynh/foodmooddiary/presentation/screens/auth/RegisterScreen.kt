package com.haphuongquynh.foodmooddiary.presentation.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.haphuongquynh.foodmooddiary.presentation.navigation.Screen
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthState
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var acceptTerms by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val authState by viewModel.authState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Handle auth state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }
            is AuthState.Error -> {
                errorMessage = (authState as AuthState.Error).message
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Join FoodMoodDiary",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Start tracking your food and mood journey",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Display Name field
            OutlinedTextField(
                value = displayName,
                onValueChange = { 
                    displayName = it
                    errorMessage = null
                },
                label = { Text("Display Name") },
                leadingIcon = { Icon(Icons.Default.Person, "Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    errorMessage = null
                },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, "Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    errorMessage = null
                },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, "Password") },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility 
                            else Icons.Default.VisibilityOff,
                            "Toggle password visibility"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) 
                    VisualTransformation.None 
                else 
                    PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                supportingText = { 
                    Text("At least 6 characters with 1 number", 
                        style = MaterialTheme.typography.bodySmall) 
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { 
                    confirmPassword = it
                    errorMessage = null
                },
                label = { Text("Confirm Password") },
                leadingIcon = { Icon(Icons.Default.Lock, "Confirm Password") },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            if (confirmPasswordVisible) Icons.Default.Visibility 
                            else Icons.Default.VisibilityOff,
                            "Toggle password visibility"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) 
                    VisualTransformation.None 
                else 
                    PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Terms and conditions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = acceptTerms,
                    onCheckedChange = { acceptTerms = it }
                )
                Text(
                    text = "I agree to Terms and Conditions",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable { acceptTerms = !acceptTerms }
                )
            }

            // Error message
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register button
            Button(
                onClick = { 
                    if (!acceptTerms) {
                        errorMessage = "Please accept terms and conditions"
                    } else {
                        viewModel.register(email, password, confirmPassword, displayName)
                    }
                },
                enabled = authState !is AuthState.Loading && 
                         displayName.isNotBlank() &&
                         email.isNotBlank() && 
                         password.isNotBlank() &&
                         confirmPassword.isNotBlank() &&
                         acceptTerms,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Account", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}
