package com.example.voice_moderation.presentation.login

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.voice_moderation.data.rules.Validator
import com.example.voice_moderation.navigation.HateDetectionAppRouter
import com.example.voice_moderation.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    private val TAG = LoginViewModel::class.simpleName

    var loginUIState = mutableStateOf(LoginUIState())
    var allValidationsPassed = mutableStateOf(false)
    var loginInProgress = mutableStateOf(false)

    // New states for success and error feedback
    var loginSuccess = mutableStateOf(false)
    var loginError = mutableStateOf<String?>(null)

    fun onEvent(event: LoginUIEvent) {
        when (event) {
            is LoginUIEvent.EmailChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    email = event.email
                )
            }

            is LoginUIEvent.PasswordChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    password = event.password
                )
            }

            is LoginUIEvent.LoginButtonClicked -> {
                login()
            }
        }
        validateLoginUIDataWithRules()
    }

    private fun validateLoginUIDataWithRules() {
        val emailResult = Validator.validateEmail(
            email = loginUIState.value.email
        )
        val passwordResult = Validator.validatePassword(
            password = loginUIState.value.password
        )

        loginUIState.value = loginUIState.value.copy(
            // Fix the logical inversion - error should be true when validation fails
            emailError = !emailResult.status,
            passwordError = !passwordResult.status
        )

        allValidationsPassed.value = emailResult.status && passwordResult.status
    }

    private fun login() {
        loginInProgress.value = true
        loginError.value = null // Reset error state
        loginSuccess.value = false // Reset success state

        val email = loginUIState.value.email
        val password = loginUIState.value.password

        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                Log.d(TAG, "Inside_login_success")
                Log.d(TAG, "${task.isSuccessful}")

                loginInProgress.value = false

                if (task.isSuccessful) {
                    loginSuccess.value = true
                    // Navigation will be handled by the UI layer
                    // HateDetectionAppRouter.navigateTo(Screen.MonitorScreen)
                } else {
                    loginError.value = task.exception?.localizedMessage ?: "Login failed"
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Inside_login_failure")
                Log.d(TAG, "${exception.localizedMessage}")

                loginInProgress.value = false
                loginError.value = exception.localizedMessage ?: "Login failed"
            }
    }
}

