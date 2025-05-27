package com.example.voice_moderation.data.model.domain

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class AudioAnalysisResult(
    val transcription: String,
    val emotion: String,
    val hateSpeechDetected: Boolean,
    val threatDetected: Boolean,
    val severityLevel: Int
) : Parcelable {
    val needsAlert: Boolean
        get() = hateSpeechDetected || threatDetected || severityLevel > 1
}