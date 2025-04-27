package com.example.voice_moderation.data

sealed class ForgetPasswordUIEvent {
    data class EmailChanged(val email: String) : ForgetPasswordUIEvent()
    object SendButtonClicked : ForgetPasswordUIEvent()
}
