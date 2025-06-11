package com.example.voice_moderation.presentation.signup

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voice_moderation.components.HeadingTextComponent
import com.example.voice_moderation.components.MyTextFieldComponent
import com.example.voice_moderation.components.NormalTextComponenet
import com.example.voice_moderation.components.PasswordTextFieldComponent
import com.example.voice_moderation.R
import com.example.voice_moderation.components.ButtonComponent
import com.example.voice_moderation.components.CheckboxComponent
import com.example.voice_moderation.components.ClickableLoginTextComponent
import com.example.voice_moderation.components.DividerTextComponent
import com.example.voice_moderation.navigation.HateDetectionAppRouter
import com.example.voice_moderation.navigation.Screen


@Composable
fun Signup(signupViewModel: SignupViewModel = viewModel()) {
    // Main container with white background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Background decorative shapes
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f)
        ) {
            // Top right decorative shape
            val topRightPath = Path().apply {
                moveTo(size.width * 0.65f, 0f)
                quadraticBezierTo(
                    size.width * 0.85f, size.height * 0.05f,
                    size.width, size.height * 0.2f
                )
                lineTo(size.width, 0f)
                close()
            }

            // Bottom left decorative shape
            val bottomLeftPath = Path().apply {
                moveTo(0f, size.height * 0.8f)
                quadraticBezierTo(
                    size.width * 0.15f, size.height * 0.95f,
                    size.width * 0.35f, size.height
                )
                lineTo(0f, size.height)
                close()
            }

            // Draw shapes with gradients
            drawPath(
                path = topRightPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF6B8AF2),
                        Color(0xFF8BA4FF)
                    ),
                    start = Offset(size.width * 0.7f, 0f),
                    end = Offset(size.width, size.height * 0.2f)
                )
            )

            drawPath(
                path = bottomLeftPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF8BA4FF),
                        Color(0xFF6B8AF2)
                    ),
                    start = Offset(0f, size.height * 0.8f),
                    end = Offset(size.width * 0.3f, size.height)
                )
            )
        }

        // Content container with padding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .zIndex(1f),
            contentAlignment = Alignment.Center
        ) {
            // Main content surface
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp)),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                // Main content column
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    NormalTextComponenet(value = stringResource(id = R.string.hello))
                    HeadingTextComponent(value = stringResource(id = R.string.create_account))

                    MyTextFieldComponent(
                        labelValue = stringResource(id = R.string.first_name),
                        painterResource = painterResource(id = R.drawable.profile),
                        onTextSelected = {
                            signupViewModel.onEvent(SignupUIEvent.FirstNameChanged(it))
                        },
                        errorStatus = signupViewModel.registrationUIState.value.firstNameError
                    )
                    MyTextFieldComponent(
                        labelValue = stringResource(id = R.string.last_name),
                        painterResource = painterResource(id = R.drawable.profile),
                        onTextSelected = {
                            signupViewModel.onEvent(SignupUIEvent.LastNameChanged(it))
                        },
                        errorStatus = signupViewModel.registrationUIState.value.lastNameError
                    )
                    MyTextFieldComponent(
                        labelValue = stringResource(id = R.string.email),
                        painterResource = painterResource(id = R.drawable.email),
                        onTextSelected = {
                            signupViewModel.onEvent(SignupUIEvent.EmailChanged(it))
                        },
                        errorStatus = signupViewModel.registrationUIState.value.emailError
                    )
                    PasswordTextFieldComponent(
                        labelValue = stringResource(id = R.string.password),
                        painterResource = painterResource(id = R.drawable.password),
                        onTextSelected = {
                            signupViewModel.onEvent(SignupUIEvent.PasswordChanged(it))
                        },
                        errorStatus = signupViewModel.registrationUIState.value.passwordError
                    )
                    CheckboxComponent(
                        value = stringResource(id = R.string.terms_and_conditions),
                        onTextSelected = {
                            HateDetectionAppRouter.navigateTo(Screen.TermsAndConditionsScreen)
                        },
                        onCheckedChange = {
                            //loginViewModel.onEvent(UIEvent.PrivacyPolicyCheckBoxClicked(it))
                        }
                    )

                    Spacer(modifier = Modifier.height(80.dp))
                    ButtonComponent(
                        value = stringResource(id = R.string.register),
                        onButtonClicked = {
                            signupViewModel.onEvent(SignupUIEvent.RegisterButtonClicked)
                        },
                        isEnabled = signupViewModel.allValidationsPassed.value
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    DividerTextComponent()
                    ClickableLoginTextComponent(
                        tryingToLogin = true,
                        onTextSelected = {
                            HateDetectionAppRouter.navigateTo(Screen.LoginScreen)
                        }
                    )
                }
            }
        }

        // Loading indicator
        if(signupViewModel.signUpProgress.value) {
            CircularProgressIndicator()
        }
    }
}

@Preview
@Composable
fun DefaultPreviewOfSignup() {
    Signup()
}