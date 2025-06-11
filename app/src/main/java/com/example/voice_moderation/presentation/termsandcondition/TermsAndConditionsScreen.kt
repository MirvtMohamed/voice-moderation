package com.example.voice_moderation.presentation.termsandcondition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voice_moderation.R
import com.example.voice_moderation.components.HeadingTextComponent
import com.example.voice_moderation.navigation.HateDetectionAppRouter
import com.example.voice_moderation.navigation.Screen
import com.example.voice_moderation.navigation.SystemBackButtonHandler

@Composable
fun TermsAndConditionsScreen(){
    Surface (modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)
        .padding(30.dp)){
        HeadingTextComponent(value = stringResource(id = R.string.terms_and_conditions_header))

    }
    SystemBackButtonHandler{
        HateDetectionAppRouter.navigateTo(Screen.SignUp)

    }
}

@Preview
@Composable
fun TermsAndConditionsScreenPreview(){
    TermsAndConditionsScreen()
}