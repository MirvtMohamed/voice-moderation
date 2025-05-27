package com.example.voice_moderation.ui.alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.voice_moderation.R
import com.example.voice_moderation.data.model.domain.AudioAnalysisResult

class AlertBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SHOW_ALERT = "com.example.voice_moderation.SHOW_ALERT"
        const val EXTRA_ANALYSIS_RESULT = "analysis_result"

        fun sendAlert(context: Context, result: AudioAnalysisResult) {
            val intent = Intent(context, AlertBroadcastReceiver::class.java).apply {
                action = ACTION_SHOW_ALERT
                putExtra(EXTRA_ANALYSIS_RESULT, result)
            }
            context.sendBroadcast(intent)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_SHOW_ALERT) {
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_ANALYSIS_RESULT, AudioAnalysisResult::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_ANALYSIS_RESULT)
            }

            result?.let {
                showAlertNotification(context, it)
            }
        }
    }

    private fun showAlertNotification(context: Context, result: AudioAnalysisResult) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Ensure notification channel exists
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "voice_alerts",
                "Voice Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for detected voice content"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create notification
        val notificationBuilder = NotificationCompat.Builder(context, "voice_alerts")
            .setContentTitle("Voice Alert Detected")
            .setContentText(getAlertText(result))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Transcription: ${result.transcription}\nEmotion: ${result.emotion}"))
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(100, notificationBuilder.build())
    }

    private fun getAlertText(result: AudioAnalysisResult): String {
        return when {
            result.threatDetected -> "Threat detected"
            result.hateSpeechDetected -> "Hate speech detected"
            result.severityLevel > 2 -> "Concerning content detected"
            else -> "Potential issue detected"
        }
    }
}