package com.example.voice_moderation.ui.theme

import androidx.lifecycle.ViewModel
import com.example.voice_moderation.data.LoginUIEvent
import com.example.voice_moderation.data.LoginUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    private val _loginUIState = MutableStateFlow(LoginUIState())
    val loginUIState: StateFlow<LoginUIState> = _loginUIState

    val allValidationsPassed = MutableStateFlow(false)

    private val _loginInProgress = MutableStateFlow(false)
    val loginInProgress: StateFlow<Boolean> = _loginInProgress

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    // Error handling callback
    var onError: ((String) -> Unit)? = null

    fun onEvent(event: LoginUIEvent) {
        when (event) {
            is LoginUIEvent.EmailChanged -> {
                _loginUIState.value = _loginUIState.value.copy(email = event.email)
                validateData()
            }
            is LoginUIEvent.PasswordChanged -> {
                _loginUIState.value = _loginUIState.value.copy(password = event.password)
                validateData()
            }
            is LoginUIEvent.LoginButtonClicked -> {
                if (allValidationsPassed.value) {
                    performLogin()
                } else {
                    onError?.invoke("Please fill in all fields correctly.")
                }
            }
        }
    }

    private fun validateData() {
        allValidationsPassed.value =
            _loginUIState.value.email.isNotEmpty() && _loginUIState.value.password.isNotEmpty()
    }

    private fun performLogin() {
        _loginInProgress.value = true

        // Simulate login process with Firebase (or your backend)
        try {
            // Simulating a successful login
            _loginSuccess.value = true
        } catch (e: Exception) {
            _loginSuccess.value = false
            onError?.invoke("Login failed. Please check your credentials and try again.")
        } finally {
            _loginInProgress.value = false
        }
    }
}
