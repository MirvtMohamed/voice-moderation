package com.example.voice_moderation.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Screen {
    object SignUp : Screen()
    object TermsAndConditionsScreen : Screen()
    object LoginScreen : Screen()
    object ForgetPasswordScreen : Screen()
    object HomeScreen :Screen()
}

object HateDetectionAppRouter {


    var currentScreen: MutableState<Screen> = mutableStateOf(Screen.SignUp)

    fun navigateTo(destination: Screen) {
        currentScreen.value = destination // Update the current screen
    }
}
