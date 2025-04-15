package com.example.voice_moderation

import android.app.Application
import com.google.firebase.FirebaseApp


class HateDetectionApp:Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}