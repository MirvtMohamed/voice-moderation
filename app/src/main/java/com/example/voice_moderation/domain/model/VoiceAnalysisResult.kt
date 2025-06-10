package com.example.voice_moderation.domain.model



data class VoiceAnalysisResult(
    val transcription: String,
    val emotion: String,
    val hatePrediction: HatePrediction
)

//    : Parcelable {
//    val needsAlert: Boolean
//        get() = hateSpeechDetected || threatDetected || severityLevel > 1
//}