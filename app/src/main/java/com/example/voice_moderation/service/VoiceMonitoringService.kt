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
import com.example.voice_moderation.domain.audio.AudioStreamController
import com.example.voice_moderation.data.preferences.MonitoringPreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext // Added for NonCancellable context
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class VoiceMonitoringService : Service() {

    @Inject
    lateinit var audioStreamController: AudioStreamController

    @Inject
    lateinit var preferencesRepository: MonitoringPreferencesRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private val CHANNEL_ID = "VoiceMonitorChannel"
    private val NOTIFICATION_ID = 1

    companion object {
        private const val ACTION_START = "com.example.voice_moderation.START"
        private const val ACTION_STOP = "com.example.voice_moderation.STOP"

        fun startService(context: Context) {
            val intent = Intent(context, VoiceMonitoringService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
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

        audioStreamController.setStateListener { isStreaming ->
            serviceScope.launch {
                preferencesRepository.setMonitoringState(isStreaming)
                if (!isStreaming) {
                    stopSelf()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                serviceScope.launch {
                    try {
                        startForeground(NOTIFICATION_ID, createNotification())
                        audioStreamController.start()
                    } catch (e: Exception) {
                        Timber.e(e, "Error starting voice monitoring")
                        preferencesRepository.setMonitoringState(false)
                        stopSelf()
                    }
                }
            }
            ACTION_STOP -> {
                serviceScope.launch {
                    audioStreamController.stop()
                    // State update and service stop will happen via listener callback
                }
            }
        }
        return START_NOT_STICKY
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
        .setSmallIcon(R.drawable.ic_notification)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
        .setContentIntent(getMainActivityPendingIntent())
        .addAction(
            R.drawable.ic_stop,
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
        serviceScope.launch(NonCancellable) {
            try {
                audioStreamController.stop()
            } finally {
                serviceScope.cancel()
            }
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}