package com.example.voice_moderation.presentation.forgetpassword

data class ForgetPasswordUIState(
    val email: String = "",
    val emailError: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
