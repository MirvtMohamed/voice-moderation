package com.example.voice_moderation


import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp // <--- Import this

@HiltAndroidApp // <--- Add this annotation
class HateDetectionApp : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}
