package com.example.voice_moderation.presentation.forgetpassword

sealed class ForgetPasswordUIEvent {
    data class EmailChanged(val email: String) : ForgetPasswordUIEvent()
    object SendButtonClicked : ForgetPasswordUIEvent()
}
