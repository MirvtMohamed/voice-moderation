package com.example.voice_moderation.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.voice_moderation.navigation.HateDetectionAppRouter
import com.example.voice_moderation.navigation.Screen
import com.example.voice_moderation.presentation.forgetpassword.ForgetPasswordScreen
import com.example.voice_moderation.presentation.login.LoginScreen
import com.example.voice_moderation.presentation.monitor.MonitoringScreen
import com.example.voice_moderation.presentation.signup.Signup
import com.example.voice_moderation.presentation.termsandcondition.TermsAndConditionsScreen
import com.example.voice_moderation.presentation.splashscreen.SplashScreen
@Composable
fun HateDetectionApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState =  HateDetectionAppRouter.currentScreen, label = "") {
                currentState ->
            when( currentState.value) {
                is Screen.SplashScreen -> {
                    // Pass navigation lambdas to SplashScreen
                    SplashScreen(
                        onNavigateToMain = { HateDetectionAppRouter.navigateTo(Screen.MonitorScreen) },
                        onNavigateToAuth = { HateDetectionAppRouter.navigateTo(Screen.LoginScreen) }
                    )
                }
                is Screen.SignUp -> {
                    Signup()
                }
                is Screen.TermsAndConditionsScreen -> {
                    TermsAndConditionsScreen()
                }
                is Screen.LoginScreen ->{
                    LoginScreen()
                }
                is Screen.MonitorScreen ->{
                    MonitoringScreen()
                }
                is Screen.ForgetPasswordScreen -> {
                    ForgetPasswordScreen()
                }
            }
        }
    }
}