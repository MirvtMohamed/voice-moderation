package com.example.voice_moderation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voice_moderation.R
import com.example.voice_moderation.components.ButtonComponent
import com.example.voice_moderation.components.HeadingTextComponent
import com.example.voice_moderation.data.SignupViewModel


@Composable
fun HomeScreen(signupViewModel: SignupViewModel= viewModel()){
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeadingTextComponent(value = stringResource(id = R.string.home))

            ButtonComponent(value = stringResource(id = R.string.logout)
                , onButtonClicked = {
                    signupViewModel.logout()

            },
                isEnabled = true)
        }

    }
        }
@Preview
@Composable
fun HomeScreenPreview(){
    HomeScreen()
}