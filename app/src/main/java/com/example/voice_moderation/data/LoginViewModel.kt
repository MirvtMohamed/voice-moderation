package com.example.voice_moderation.data



import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.voice_moderation.navigation.HateDetectionAppRouter
import com.example.voice_moderation.navigation.Screen
import com.example.voice_moderation.rules.Validator
import com.example.voice_moderation.screens.HomeScreen
import com.google.firebase.auth.FirebaseAuth


class LoginViewModel:ViewModel() {

    private val TAG = LoginViewModel::class.simpleName
    var registrationUIState = mutableStateOf(RegistrationUIState())
    var allValidationsPassed = mutableStateOf(false)
    var signUpProgress = mutableStateOf(false)

    fun onEvent(event: UIEvent){
        validateDataWithRules()
        when(event){
            is UIEvent.FirstNameChanged-> {
                registrationUIState.value = registrationUIState.value.copy(
                    firstName = event.firstName
                )
                printState()

            }

            is UIEvent.LastNameChanged ->{
                registrationUIState.value = registrationUIState.value.copy(
                    lastName = event.lastName
                )
                printState()
            }

            is UIEvent.EmailChanged ->{
                registrationUIState.value = registrationUIState.value.copy(
                    email = event.email
                )
                printState()
            }

            is UIEvent.PasswordChanged ->{
                registrationUIState.value = registrationUIState.value.copy(
                    password = event.password
                )
                printState()
            }
            is UIEvent.RegisterButtonClicked ->{
                signUp()
            }

           /* is UIEvent.PrivacyPolicyCheckBoxClicked ->{
                registrationUIState.value = registrationUIState.value.copy(
                    privacyPolicyAccepted = event.status
                )


            }*/



        }


    }

    private fun signUp() {
        Log.d(TAG,"Inside_signUp")
        printState()
        createUserInFirebase(
            email = registrationUIState.value.email,
            password = registrationUIState.value.password
        )

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

       /* val privacyPolicyResult = Validator.validatePrivacyPolicyAcceptance(
            statusValue = registrationUIState.value.privacyPolicyAccepted
        )*/
        Log.d(TAG, "Inside_validateDataWithRules")
        Log.d(TAG, "fNameResult= $fNameResult")
        Log.d(TAG, "lNameResult= $lNameResult")
        Log.d(TAG, "emailResult= $emailResult")
        Log.d(TAG, "passwordResult= $passwordResult")
       // Log.d(TAG, "privacyPolicyResult= $privacyPolicyResult")

        registrationUIState.value = registrationUIState.value.copy(
            firstNameError = fNameResult.status,
            lastNameError = lNameResult.status,
            emailError = emailResult.status,
            passwordError = passwordResult.status,
           // privacyPolicyError = privacyPolicyResult.status
        )
        allValidationsPassed.value= fNameResult.status && lNameResult.status &&
            emailResult.status && passwordResult.status
               // && privacyPolicyResult.status



        }



    private fun printState(){
        Log.d(TAG,"Inside_printState")
        Log.d(TAG,registrationUIState.value.toString())
    }

    fun createUserInFirebase(email:String, password:String){
        signUpProgress.value = true

        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                Log.d(TAG,"Inside_OnCompleteListener")
                Log.d(TAG,"isSuccessful = ${it.isSuccessful}")
                signUpProgress.value = false

                if(it.isSuccessful){
                    HateDetectionAppRouter.navigateTo(Screen.HomeScreen)
                }
            }
            .addOnFailureListener {
                Log.d(TAG,"Inside_OnFailureListener")
                Log.d(TAG,"Exception = ${it.message}")
                Log.d(TAG,"Exception = ${it.localizedMessage}")

            }


    }


}