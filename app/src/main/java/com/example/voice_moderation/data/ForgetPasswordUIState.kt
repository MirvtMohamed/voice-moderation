package com.example.voice_moderation.data

data class ForgetPasswordUIState(
    val email: String = "",
    val emailError: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
