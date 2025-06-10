package com.example.voice_moderation.presentation.monitor

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MonitoringScreen(
    viewModel: VoiceMonitorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isRecording by viewModel.isRecording.collectAsState()
    val recordAudioPermission = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)

    // Removed 'isProcessing' local state as it's not effectively managing async operations

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
                        if (isRecording) {
                            viewModel.stopMonitoring(context)
                        } else {
                            viewModel.startMonitoring(context)
                        }
                        // No need to manage 'isProcessing' here; 'isRecording' will update asynchronously
                    },
                    // Button is always enabled if permission is granted, allowing user to toggle
                    // The UI will update when 'isRecording' state changes from preferences
                    enabled = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Removed CircularProgressIndicator based on local 'isProcessing' state
                    Text(if (isRecording) "Stop Monitoring" else "Start Monitoring")
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
