package com.example.voice_moderation.domain.service


import com.example.voice_moderation.domain.model.AudioAnalysisResult

interface AlertSendingService {

    suspend fun sendAlert(analysisResult: AudioAnalysisResult)
}