package com.example.voice_moderation.presentation.login

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voice_moderation.R
import com.example.voice_moderation.navigation.HateDetectionAppRouter
import com.example.voice_moderation.navigation.Screen
import com.example.voice_moderation.navigation.SystemBackButtonHandler
import com.example.voice_moderation.presentation.components.ModernButton
import com.example.voice_moderation.presentation.components.ModernPasswordTextField
import com.example.voice_moderation.presentation.components.ModernTextField
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val primaryColor = Color(0xFF38A8D2)
    val uiState = loginViewModel.loginUIState.value
    val isLoggingIn = loginViewModel.loginInProgress.value

    // Animation visibility state
    var isVisible by remember { mutableStateOf(false) }

    // Snackbar host state for error messages
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe login success and error states
    val loginSuccess = loginViewModel.loginSuccess.value
    val loginError = loginViewModel.loginError.value

    // Effect to navigate to MonitorScreen on successful login
    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            // Show success message briefly before navigating
            snackbarHostState.showSnackbar(
                message = "Login successful!",
                duration = SnackbarDuration.Short
            )
            delay(1000) // Wait for 1 second to show the success message
            HateDetectionAppRouter.navigateTo(Screen.MonitorScreen)
        }
    }

    // Effect to show error message
    LaunchedEffect(loginError) {
        if (loginError != null) {
            snackbarHostState.showSnackbar(
                message = loginError,
                duration = SnackbarDuration.Long
            )
        }
    }

    // Trigger animation after composition
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.7f),
                            primaryColor.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Content
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(1000)) +
                    slideInVertically(animationSpec = tween(1000)) { it / 2 },
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // App logo or icon
                Image(
                    painter = painterResource(id = R.drawable.parental_control),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(primaryColor.copy(alpha = 0.1f))
                        .padding(16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "Welcome Back",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "Sign in to continue with Voice Moderation",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Form fields
                ModernTextField(
                    value = uiState.email,
                    onValueChange = { loginViewModel.onEvent(LoginUIEvent.EmailChanged(it)) },
                    label = "Email",
                    leadingIconVector = Icons.Default.Email,
                    isError = uiState.emailError,
                    errorMessage = "Please enter a valid email address",
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                )

                ModernPasswordTextField(
                    value = uiState.password,
                    onValueChange = { loginViewModel.onEvent(LoginUIEvent.PasswordChanged(it)) },
                    label = "Password",
                    leadingIcon = painterResource(id = R.drawable.password),
                    isError = uiState.passwordError,
                    errorMessage = "Password must be at least 6 characters"
                )

                // Forgot password link
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(onClick = { HateDetectionAppRouter.navigateTo(Screen.ForgetPasswordScreen) }) {
                        Text(
                            text = "Forgot Password?",
                            color = primaryColor,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Login button
                ModernButton(
                    text = "LOGIN",
                    onClick = { loginViewModel.onEvent(LoginUIEvent.LoginButtonClicked) },
                    enabled = loginViewModel.allValidationsPassed.value,
                    isLoading = isLoggingIn
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray
                    )
                    Text(
                        text = "OR",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign up link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account?",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    TextButton(onClick = { HateDetectionAppRouter.navigateTo(Screen.SignUp) }) {
                        Text(
                            text = "Sign Up",
                            color = primaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Snackbar host for error messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    // Handle back button
    SystemBackButtonHandler {
        HateDetectionAppRouter.navigateTo(Screen.SignUp)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}

