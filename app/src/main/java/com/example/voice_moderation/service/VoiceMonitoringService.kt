package com.example.voice_moderation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.voice_moderation.MainActivity
import com.example.voice_moderation.R
import com.example.voice_moderation.data.audio.AudioStreamController
import com.example.voice_moderation.data.audio.AudioStreamer
import com.example.voice_moderation.data.model.domain.repository.VoiceRepository
import com.example.voice_moderation.data.preferences.MonitoringPreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class VoiceMonitoringService : Service() {

    @Inject
    lateinit var audioStreamer: AudioStreamer
    @Inject
    lateinit var audioStreamController: AudioStreamController


    @Inject
    lateinit var voiceRepository: VoiceRepository

    @Inject
    lateinit var preferencesRepository: MonitoringPreferencesRepository


    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private val CHANNEL_ID = "VoiceMonitorChannel"
    private val NOTIFICATION_ID = 1
    private val ALERT_NOTIFICATION_ID = 2

    companion object {
        private const val ACTION_START = "com.example.voice_moderation.START"
        private const val ACTION_STOP = "com.example.voice_moderation.STOP"

        fun startService(context: Context) {
            val intent = Intent(context, VoiceMonitoringService::class.java).apply {
                action = ACTION_START
            }
            context.startService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, VoiceMonitoringService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("Voice Monitoring Service created")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                Timber.d("Starting voice monitoring")
                startForeground(NOTIFICATION_ID, createNotification())
                startMonitoring()
                updatePreferences(true)
            }
            ACTION_STOP -> {
                Timber.d("Stopping voice monitoring")
                updatePreferences(false)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun startMonitoring() {
        serviceScope.launch {
            try {
                audioStreamController.start()
                updatePreferences(true)
            } catch (e: Exception) {
                Timber.e(e, "Error in voice monitoring")
                updatePreferences(false)
                stopSelf()
            }
        }
    }

    private fun updatePreferences(isRecording: Boolean) {
        serviceScope.launch {
            try {
                preferencesRepository.setMonitoringState(isRecording)
                Timber.d("Updated recording state to: $isRecording")
            } catch (e: Exception) {
                Timber.e(e, "Failed to update preferences")
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Voice Monitoring"
            val descriptionText = "Voice monitoring service"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Voice Monitoring Active")
        .setContentText("Your voice is being monitored")
        .setSmallIcon(R.drawable.ic_notification) // Make sure you have this icon
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
        .setContentIntent(getMainActivityPendingIntent())
        .addAction(
            R.drawable.ic_stop, // Make sure you have this icon
            "Stop Monitoring",
            getStopServicePendingIntent()
        )
        .build()

    private fun getMainActivityPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(this, 0, intent, flags)
    }

    private fun getStopServicePendingIntent(): PendingIntent {
        val intent = Intent(this, VoiceMonitoringService::class.java).apply {
            action = ACTION_STOP
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }
        return PendingIntent.getService(this, 1, intent, flags)
    }

    override fun onDestroy() {
        Timber.d("Voice Monitoring Service destroyed")
        serviceScope.launch {
            audioStreamController.stop()
            updatePreferences(false)
        }
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}