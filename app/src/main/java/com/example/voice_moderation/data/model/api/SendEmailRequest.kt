package com.example.voice_moderation.data.model.api

import com.google.gson.annotations.SerializedName

data class SendEmailRequest(
    @SerializedName("recipient_email")
    val recipientEmail: String,
    @SerializedName("transcription")
    val transcription: String,
    @SerializedName("emotion")
    val emotion: String,
    @SerializedName("hate_speech_detected")
    val hateSpeechDetected: Boolean,
    @SerializedName("threat_detected")
    val threatDetected: Boolean,
    @SerializedName("severity_level")
    val severityLevel: Int
)