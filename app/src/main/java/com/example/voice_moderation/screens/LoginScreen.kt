package com.example.voice_moderation.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voice_moderation.R
import com.example.voice_moderation.components.ButtonComponent
import com.example.voice_moderation.components.ClickableLoginTextComponent
import com.example.voice_moderation.components.DividerTextComponent
import com.example.voice_moderation.components.HeadingTextComponent
import com.example.voice_moderation.components.MyTextFieldComponent
import com.example.voice_moderation.components.NormalTextComponenet
import com.example.voice_moderation.components.PasswordTextFieldComponent
import com.example.voice_moderation.components.UnderLinedTextComponenet
import com.example.voice_moderation.navigation.HateDetectionAppRouter
import com.example.voice_moderation.navigation.Screen
import com.example.voice_moderation.navigation.SystemBackButtonHandler


@Composable
fun LoginScreen() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NormalTextComponenet(value = stringResource(id = R.string.login))
            HeadingTextComponent(value = stringResource(id = R.string.welcome))
            Spacer(modifier = Modifier.height(20.dp))
            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.email), painterResource = painterResource(
                    id = R.drawable.email
                )
            )

            PasswordTextFieldComponent(
                labelValue = stringResource(id = R.string.password),
                painterResource = painterResource(
                    id = R.drawable.password
                )
            )
            Spacer(modifier = Modifier.height(40.dp))

            UnderLinedTextComponenet(value = stringResource(id = R.string.forgot_password))
            Spacer(modifier = Modifier.height(40.dp))

            ButtonComponent(value = stringResource(id = R.string.login))
            Spacer(modifier = Modifier.height(20.dp))

            DividerTextComponent()

            ClickableLoginTextComponent(tryingToLogin = false, onTextSelected = {
                HateDetectionAppRouter.navigateTo(Screen.SignUp)

            })
        }
    }

}
@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}