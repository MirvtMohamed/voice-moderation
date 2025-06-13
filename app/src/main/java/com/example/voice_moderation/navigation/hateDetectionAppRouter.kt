package com.example.voice_moderation.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Screen {
    object SplashScreen : Screen()
    object SignUp : Screen()
    object TermsAndConditionsScreen : Screen()
    object LoginScreen : Screen()
    object ForgetPasswordScreen : Screen()
    object MonitorScreen : Screen()
}

object HateDetectionAppRouter {

    var currentScreen: MutableState<Screen> = mutableStateOf(Screen.SplashScreen) // Change initial screen to SplashScreen

    fun navigateTo(destination: Screen) {
        currentScreen.value = destination
    }
}