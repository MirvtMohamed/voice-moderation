package com.example.voice_moderation.presentation.signup

data class RegistrationUIState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val privacyPolicyAccepted: Boolean = false, // Added this field

    val firstNameError: Boolean = false,
    val lastNameError: Boolean = false,
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val privacyPolicyError: Boolean = false // Added this field
)