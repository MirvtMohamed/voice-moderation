package com.example.voice_moderation.presentation.login


sealed class LoginUIEvent {

    data class EmailChanged(val email: String) : LoginUIEvent()
    data class PasswordChanged(val password: String) : LoginUIEvent()


    object LoginButtonClicked : LoginUIEvent()
}
