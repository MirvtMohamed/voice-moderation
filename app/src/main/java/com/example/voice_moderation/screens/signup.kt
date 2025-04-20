package com.example.voice_moderation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voice_moderation.components.HeadingTextComponent
import com.example.voice_moderation.components.MyTextFieldComponent
import com.example.voice_moderation.components.NormalTextComponenet
import com.example.voice_moderation.components.PasswordTextFieldComponent
import com.example.voice_moderation.R
import com.example.voice_moderation.components.ButtonComponent
import com.example.voice_moderation.components.CheckboxComponent
import com.example.voice_moderation.components.ClickableLoginTextComponent
import com.example.voice_moderation.components.DividerTextComponent
import com.example.voice_moderation.data.SignupViewModel
import com.example.voice_moderation.data.SignupUIEvent
import com.example.voice_moderation.navigation.HateDetectionAppRouter
import com.example.voice_moderation.navigation.Screen



@Composable
fun Signup(signupViewModel: SignupViewModel = viewModel()) {
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center){
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(28.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(20.dp))
                NormalTextComponenet(value = stringResource(id = R.string.hello))
                HeadingTextComponent(value = stringResource(id = R.string.create_account))

                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.first_name),
                    painterResource= painterResource(id = R.drawable.profile),
                    onTextSelected = {
                        signupViewModel.onEvent(SignupUIEvent.FirstNameChanged(it))

                    },
                    signupViewModel.registrationUIState.value.firstNameError
                )
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.last_name),
                    painterResource= painterResource(id = R.drawable.profile),
                    onTextSelected = {
                        signupViewModel.onEvent(SignupUIEvent.LastNameChanged(it))

                    },
                    errorStatus = signupViewModel.registrationUIState.value.lastNameError
                )
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.email),
                    painterResource = painterResource(id = R.drawable.email),
                    onTextSelected = {
                        signupViewModel.onEvent(SignupUIEvent.EmailChanged(it))

                    },
                    errorStatus =  signupViewModel.registrationUIState.value.emailError
                )
                PasswordTextFieldComponent(
                    labelValue = stringResource(id = R.string.password),
                    painterResource = painterResource(id = R.drawable.password),
                    onTextSelected = {
                        signupViewModel.onEvent(SignupUIEvent.PasswordChanged(it))

                    },
                    signupViewModel.registrationUIState.value.passwordError
                )
                CheckboxComponent(value = stringResource(id = R.string.terms_and_conditions),
                    onTextSelected = {
                        HateDetectionAppRouter.navigateTo(Screen.TermsAndConditionsScreen)


                    },
                    onCheckedChange ={
                        //loginViewModel.onEvent(UIEvent.PrivacyPolicyCheckBoxClicked(it))

                    }
                )

                Spacer(modifier = Modifier.height(80.dp))
                ButtonComponent(
                    value = stringResource(id = R.string.register),
                    onButtonClicked = {
                        signupViewModel.onEvent(SignupUIEvent.RegisterButtonClicked)
                    },
                    isEnabled = signupViewModel.allValidationsPassed.value
                )
                Spacer(modifier = Modifier.height(20.dp))

                DividerTextComponent()
                ClickableLoginTextComponent (tryingToLogin = true, onTextSelected ={

                    HateDetectionAppRouter.navigateTo(Screen.LoginScreen)

                })



            } }
        if(signupViewModel.signUpProgress.value) {

            CircularProgressIndicator()
        }

    }

}



@Preview
@Composable
fun DefaultPreviewOfSignup() {
    Signup()
}