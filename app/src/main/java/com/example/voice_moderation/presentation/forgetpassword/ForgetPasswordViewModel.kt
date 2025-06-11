package com.example.voice_moderation.presentation.forgetpassword

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class ForgetPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ForgetPasswordUIState())
    val uiState = _uiState.asStateFlow()

    // Accept a callback for UI updates (e.g., showing Toast)
    var onError: ((String) -> Unit)? = null
    var onSuccess: (() -> Unit)? = null

    fun onEvent(event: ForgetPasswordUIEvent) {
        when (event) {
            is ForgetPasswordUIEvent.EmailChanged -> {
                _uiState.value = _uiState.value.copy(
                    email = event.email,
                    emailError = !isValidEmail(event.email)
                )
            }
            ForgetPasswordUIEvent.SendButtonClicked -> {
                if (!uiState.value.emailError && uiState.value.email.isNotBlank()) {
                    sendResetEmail()
                } else {
                    onError?.invoke("Please enter a valid email address.")
                }
            }
        }
    }

    private fun sendResetEmail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                FirebaseAuth.getInstance().sendPasswordResetEmail(uiState.value.email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSuccess = true
                            )
                            onSuccess?.invoke()  // Invoke success callback
                        } else {
                            _uiState.value = _uiState.value.copy(isLoading = false)
                            onError?.invoke("Failed to send reset email. Please try again.")
                        }
                    }
                    .addOnFailureListener { exception ->
                        handleError(exception)
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                handleError(e)
            }
        }
    }

    private fun handleError(exception: Throwable) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidCredentialsException -> "Invalid credentials. Please check your email address."
            is FirebaseAuthUserCollisionException -> "An account already exists with this email."
            is FirebaseAuthInvalidUserException -> "No account found for this email."
            else -> exception.message ?: "An error occurred. Please try again."
        }
        onError?.invoke(errorMessage)  // Invoke error callback
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
