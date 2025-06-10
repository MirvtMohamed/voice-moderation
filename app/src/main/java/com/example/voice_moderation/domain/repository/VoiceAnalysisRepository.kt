package com.example.voice_moderation.domain.repository

import com.example.voice_moderation.data.remote.ApiResult
import com.example.voice_moderation.domain.model.VoiceAnalysisResult
import java.io.File

interface VoiceAnalysisRepository {
    suspend fun analyzeAudio(audioFile: File): ApiResult<VoiceAnalysisResult>
}