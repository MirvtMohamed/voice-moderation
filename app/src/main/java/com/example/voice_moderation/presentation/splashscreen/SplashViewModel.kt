package com.example.voice_moderation.presentation.splashscreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voice_moderation.navigation.HateDetectionAppRouter
import com.example.voice_moderation.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    init {
        viewModelScope.launch {
            delay(2000) // Simulate some loading time
            if (FirebaseAuth.getInstance().currentUser != null) {
                HateDetectionAppRouter.navigateTo(Screen.MonitorScreen) // Navigate to MonitorScreen
            } else {
                HateDetectionAppRouter.navigateTo(Screen.LoginScreen)
            }
        }
    }
}
