package com.example.voice_moderation.domain.service


interface AlertSendingService {
    suspend fun sendAlert(message: String, recipientEmail: String)
}