package com.example.voice_moderation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun AudioRecorderScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var hasPermissions by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false
        } else true
        val storageGranted = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: true
        } else true

        hasPermissions = audioGranted && notificationGranted && storageGranted

        if (!hasPermissions) {
            Toast.makeText(context, "All permissions are required", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        val perms = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(perms.toTypedArray())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Voice Moderation",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        ElevatedButton(
            onClick = {
                if (hasPermissions) {
                    if (!isRecording) {
                        startRecordingService(context)
                        isRecording = true
                    } else {
                        stopRecordingService(context)
                        isRecording = false
                    }
                } else {
                    Toast.makeText(context, "Grant permissions first", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = if (isRecording) "Stop Recording" else "Start Recording",
                modifier = Modifier.padding(8.dp)
            )
        }

        ElevatedButton(
            onClick = { askDisableBatteryOptimization(context) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Allow Background Recording (Battery)",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}