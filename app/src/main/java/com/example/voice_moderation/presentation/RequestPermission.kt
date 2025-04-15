package com.example.voice_moderation.presentation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.voice_moderation.util.AudioViewModel


@Composable
    fun RequestPermissions() {
        val context = LocalContext.current
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        LaunchedEffect(Unit) {
            permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
        }
    }
@Composable
fun AudioControlScreen(viewModel: AudioViewModel) {
    val isRecording = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            if (isRecording.value) {
                viewModel.stopRecording()
            } else {
                viewModel.startRecording()
            }
            isRecording.value = !isRecording.value
        }) {
            Text(if (isRecording.value) "Stop Recording" else "Start Recording")
        }
    }
}