package com.example.voice_moderation.presentation.monitor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import android.content.Intent
import android.net.Uri
import android.provider.Settings

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MonitoringScreen(
    viewModel: VoiceMonitorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isRecording by viewModel.isRecording.collectAsState()
    val recordAudioPermission = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)
    var isProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!recordAudioPermission.status.isGranted) {
            recordAudioPermission.launchPermissionRequest()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        when {
            recordAudioPermission.status.isGranted -> {
                Text(
                    text = if (isRecording) "Monitoring in progress..." else "Monitoring stopped",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        isProcessing = true
                        if (isRecording) {
                            viewModel.stopMonitoring(context)
                        } else {
                            viewModel.startMonitoring(context)
                        }
                        isProcessing = false
                    },
                    enabled = !isProcessing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(if (isRecording) "Stop Monitoring" else "Start Monitoring")
                    }
                }
            }


                recordAudioPermission.status.shouldShowRationale -> {
                Text("The app needs microphone permission to monitor speech.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { recordAudioPermission.launchPermissionRequest() }) {
                    Text("Grant Microphone Permission")
                }
            }

            else -> {
                Text("Microphone permission permanently denied. Please enable it in app settings.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(intent)
                }) {
                    Text("Open App Settings")
                }
            }

        }
    }
}
