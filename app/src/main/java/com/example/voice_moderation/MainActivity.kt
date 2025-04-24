package com.example.voice_moderation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.voice_moderation.presentation.monitor.MonitoringScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonitoringScreen()
        }
    }
}


//fun createNotificationChannel(context: Context) {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        val serviceChannel = NotificationChannel(
//            AudioRecordingService.CHANNEL_ID,
//            "Audio Recording Service Channel",
//            NotificationManager.IMPORTANCE_DEFAULT
//        )
//        val manager = context.getSystemService(NotificationManager::class.java)
//        manager.createNotificationChannel(serviceChannel)
//    }
//}
