package com.example.voice_moderation.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voice_moderation.R
import com.example.voice_moderation.components.ButtonComponent
import com.example.voice_moderation.components.HeadingTextComponent
import com.example.voice_moderation.components.MyTextFieldComponent
import com.example.voice_moderation.components.NormalTextComponenet
import com.example.voice_moderation.data.ForgetPasswordUIEvent
import com.example.voice_moderation.data.ForgetPasswordViewModel
import com.example.voice_moderation.navigation.HateDetectionAppRouter
import com.example.voice_moderation.navigation.Screen

@Composable
fun ForgetPasswordScreen(
    viewModel: ForgetPasswordViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Show Toast and navigate to login screen when password reset is successful
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Reset email sent successfully!", Toast.LENGTH_LONG).show()
            HateDetectionAppRouter.navigateTo(Screen.LoginScreen)
        }
    }

    // Show Toast for error messages
    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) {
            // This shows a loading state, so no specific message needed here.
            return@LaunchedEffect
        }
        if (!uiState.isSuccess && uiState.email.isNotBlank() && uiState.emailError) {
            Toast.makeText(context, "Invalid email address", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val topRightPath = Path().apply {
                moveTo(size.width * 0.65f, 0f)
                quadraticBezierTo(size.width * 0.85f, size.height * 0.05f, size.width, size.height * 0.2f)
                lineTo(size.width, 0f)
                close()
            }

            val bottomLeftPath = Path().apply {
                moveTo(0f, size.height * 0.8f)
                quadraticBezierTo(size.width * 0.15f, size.height * 0.95f, size.width * 0.35f, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = topRightPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF6B8AF2), Color(0xFF8BA4FF)),
                    start = Offset(size.width * 0.7f, 0f),
                    end = Offset(size.width, size.height * 0.2f)
                )
            )

            drawPath(
                path = bottomLeftPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF8BA4FF), Color(0xFF6B8AF2)),
                    start = Offset(0f, size.height * 0.8f),
                    end = Offset(size.width * 0.3f, size.height)
                )
            )
        }

        // Center content in a box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp)),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    NormalTextComponenet(value = "Forgot Password?")
                    HeadingTextComponent(value = "Reset Your Password")

                    MyTextFieldComponent(
                        labelValue = stringResource(id = R.string.email),
                        painterResource = painterResource(id = R.drawable.email),
                        onTextSelected = {
                            viewModel.onEvent(ForgetPasswordUIEvent.EmailChanged(it))
                        },
                        errorStatus = uiState.emailError
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    ButtonComponent(
                        value = "Send",
                        onButtonClicked = {
                            viewModel.onEvent(ForgetPasswordUIEvent.SendButtonClicked)
                        },
                        isEnabled = !uiState.emailError && uiState.email.isNotBlank()
                    )
                }
            }
        }

        // Display loading spinner if loading is true
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewForgetPasswordScreen() {
    ForgetPasswordScreen()
}
