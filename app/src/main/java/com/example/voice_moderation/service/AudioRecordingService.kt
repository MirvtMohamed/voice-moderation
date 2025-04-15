package com.example.voice_moderation.service

import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.voice_moderation.R
import java.io.IOException

class AudioRecordingService : Service() {

    private var mediaRecorder: MediaRecorder? = null
    private val fileName: String by lazy {
        "${externalCacheDir?.absolutePath}/audiorecordtest.3gp"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        startRecording()
        return START_STICKY
    }

    private fun startRecording() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("AudioRecordingService", "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Audio Recording Service")
            .setContentText("Recording audio in the background")
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        startForeground(1, notification)
    }

    companion object {
        const val CHANNEL_ID = "AudioRecordingServiceChannel"
    }
}