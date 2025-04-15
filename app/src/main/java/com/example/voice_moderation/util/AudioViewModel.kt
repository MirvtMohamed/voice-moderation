package com.example.voice_moderation.util
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.example.voice_moderation.service.AudioRecordingService

class AudioViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    fun startRecording() {
        val intent = Intent(context, AudioRecordingService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    fun stopRecording() {
        val intent = Intent(context, AudioRecordingService::class.java)
        context.stopService(intent)
    }
}