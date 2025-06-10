package com.example.voice_moderation.domain.model



import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class AudioAnalysisResult(
    val transcription: String,
    val emotion: String,
    val hateSpeechDetected: Boolean,
    val threatDetected: Boolean,
    val severityLevel: Int
) : Parcelable
