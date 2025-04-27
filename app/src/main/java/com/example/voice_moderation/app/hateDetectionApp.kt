package com.example.voice_moderation.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.voice_moderation.navigation.HateDetectionAppRouter
import com.example.voice_moderation.navigation.Screen
import com.example.voice_moderation.screens.ForgetPasswordScreen
import com.example.voice_moderation.screens.HomeScreen
import com.example.voice_moderation.screens.LoginScreen
import com.example.voice_moderation.screens.Signup
import com.example.voice_moderation.screens.TermsAndConditionsScreen

@Composable
fun HateDetectionApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState =  HateDetectionAppRouter.currentScreen, label = ""){ currentState ->
            when( currentState.value) {
                is Screen.SignUp -> {
                    Signup()
                }

                is Screen.TermsAndConditionsScreen -> {
                    TermsAndConditionsScreen()

                }
                is Screen.LoginScreen ->{
                    LoginScreen()
                }
                is Screen.HomeScreen ->{
                    HomeScreen()
                }

                is Screen.ForgetPasswordScreen -> {
                    ForgetPasswordScreen()
                }

            }}
    }}
