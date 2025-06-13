package com.example.voice_moderation.presentation.signup

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.voice_moderation.navigation.HateDetectionAppRouter
import com.example.voice_moderation.navigation.Screen
import com.example.voice_moderation.data.rules.Validator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener


class SignupViewModel:ViewModel() {

    private val TAG = SignupViewModel::class.simpleName
    var registrationUIState = mutableStateOf(RegistrationUIState())
    var allValidationsPassed = mutableStateOf(false)
    var signUpProgress = mutableStateOf(false)
    var signupSuccess = mutableStateOf(false) // New state for success feedback
    var signupError = mutableStateOf<String?>(null) // New state for error feedback

    fun onEvent(event: SignupUIEvent){
        when(event){
            is SignupUIEvent.FirstNameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    firstName = event.firstName
                )
            }

            is SignupUIEvent.LastNameChanged ->{
                registrationUIState.value = registrationUIState.value.copy(
                    lastName = event.lastName
                )
            }

            is SignupUIEvent.EmailChanged ->{
                registrationUIState.value = registrationUIState.value.copy(
                    email = event.email
                )
            }

            is SignupUIEvent.PasswordChanged ->{
                registrationUIState.value = registrationUIState.value.copy(
                    password = event.password
                )
            }
            is SignupUIEvent.PrivacyPolicyCheckBoxClicked -> {
                registrationUIState.value = registrationUIState.value.copy(
                    privacyPolicyAccepted = event.status
                )
            }
            is SignupUIEvent.RegisterButtonClicked ->{
                validateDataWithRules()
                if (allValidationsPassed.value) {
                    signUp()
                }
            }
        }
        // Always validate after any event to update validation status
        validateDataWithRules()
        printState()
    }

    private fun signUp() {
        Log.d(TAG,"Inside_signUp")
        printState()
        signUpProgress.value = true
        signupError.value = null // Clear previous errors
        signupSuccess.value = false // Reset success state

        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(
                registrationUIState.value.email,
                registrationUIState.value.password
            )
            .addOnCompleteListener {
                Log.d(TAG,"Inside_OnCompleteListener")
                Log.d(TAG,"isSuccessful = ${it.isSuccessful}")
                signUpProgress.value = false

                if(it.isSuccessful){
                    signupSuccess.value = true
                    // Navigate after a short delay to show success feedback
                    // HateDetectionAppRouter.navigateTo(Screen.MonitorScreen) // This navigation will be handled by UI after success feedback
                } else {
                    signupError.value = it.exception?.localizedMessage ?: "An unknown error occurred."
                }
            }
            .addOnFailureListener {
                Log.d(TAG,"Inside_OnFailureListener")
                Log.d(TAG,"Exception = ${it.message}")
                Log.d(TAG,"Exception = ${it.localizedMessage}")

                signUpProgress.value = false
                signupError.value = it.localizedMessage ?: "An unknown error occurred."
            }
    }

    private fun validateDataWithRules() {
        val fNameResult = Validator.validateFirstName(
            fName = registrationUIState.value.firstName
        )
        val lNameResult = Validator.validateLastName(
            lName = registrationUIState.value.lastName
        )
        val emailResult = Validator.validateEmail(
            email = registrationUIState.value.email
        )
        val passwordResult = Validator.validatePassword(
            password = registrationUIState.value.password
        )
        val privacyPolicyResult = Validator.validatePrivacyPolicyAcceptance(
            statusValue = registrationUIState.value.privacyPolicyAccepted
        )

        Log.d(TAG, "Inside_validateDataWithRules")
        Log.d(TAG, "fNameResult= $fNameResult")
        Log.d(TAG, "lNameResult= $lNameResult")
        Log.d(TAG, "emailResult= $emailResult")
        Log.d(TAG, "passwordResult= $passwordResult")
        Log.d(TAG, "privacyPolicyResult= $privacyPolicyResult")

        registrationUIState.value = registrationUIState.value.copy(
            firstNameError = !fNameResult.status, // Inverted logic
            lastNameError = !lNameResult.status,   // Inverted logic
            emailError = !emailResult.status,     // Inverted logic
            passwordError = !passwordResult.status, // Inverted logic
            privacyPolicyError = !privacyPolicyResult.status // Inverted logic
        )
        allValidationsPassed.value= fNameResult.status && lNameResult.status &&
                emailResult.status && passwordResult.status && privacyPolicyResult.status
    }

    private fun printState(){
        Log.d(TAG,"Inside_printState")
        Log.d(TAG,registrationUIState.value.toString())
    }

    fun logout(){
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()
        val authStateListener = AuthStateListener{
            if(it.currentUser == null){
                Log.d(TAG,"Inside sign out outsuccess")
                HateDetectionAppRouter.navigateTo(Screen.LoginScreen)

            }else{
                Log.d(TAG,"Inside sign out is not complete")
            }

        }
        firebaseAuth.addAuthStateListener (authStateListener)
    }

}
