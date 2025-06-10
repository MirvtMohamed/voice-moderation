package com.example.voice_moderation.domain.usecase


import com.example.voice_moderation.data.remote.ApiResult
import com.example.voice_moderation.domain.model.AudioAnalysisResult
import com.example.voice_moderation.domain.model.HatePrediction
import com.example.voice_moderation.domain.model.VoiceAnalysisResult
import com.example.voice_moderation.domain.repository.VoiceAnalysisRepository
import com.example.voice_moderation.domain.preferences.AlertThresholdProvider
import com.example.voice_moderation.domain.service.AlertSendingService
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AnalyzeVoiceUseCase @Inject constructor(
    private val voiceAnalysisRepository: VoiceAnalysisRepository,
    private val alertThresholdProvider: AlertThresholdProvider,
    private val alertSendingService: AlertSendingService
) {

    operator fun invoke(audioFile: File): Flow<ApiResult<AudioAnalysisResult>> = flow {
        emit(ApiResult.Loading)

        when (val apiResult = voiceAnalysisRepository.analyzeAudio(audioFile)) {
            is ApiResult.Success -> {
                val voiceAnalysisResult = apiResult.data
                val audioAnalysisResult = mapVoiceAnalysisToAudioAnalysis(voiceAnalysisResult)

                // Logic to send alert based on severity and hate sensitivity
                if (audioAnalysisResult.hateSpeechDetected || audioAnalysisResult.severityLevel > alertThresholdProvider.getThresholds().alertThreshold) {
                    val message = "Voice chat alert: Hate speech detected or high severity level (${audioAnalysisResult.severityLevel}). Transcription: ${audioAnalysisResult.transcription}"
                    // In a real app, the recipient email would come from user preferences or configuration
                    alertSendingService.sendAlert(message, "parent@example.com")
                }

                emit(ApiResult.Success(audioAnalysisResult))
            }
            is ApiResult.Error -> {
                emit(ApiResult.Error(apiResult.exception))
            }
            ApiResult.Loading -> {
                // Should not happen for a suspend function, but for completeness
            }
        }
    }

    private fun mapVoiceAnalysisToAudioAnalysis(voiceAnalysisResult: VoiceAnalysisResult): AudioAnalysisResult {
        val hatePrediction = voiceAnalysisResult.hatePrediction

        val hateSpeechDetected = hatePrediction.toxic ||
                hatePrediction.severeToxic ||
                hatePrediction.obscene ||
                hatePrediction.identityHate

        val threatDetected = hatePrediction.threat

        val severityLevel = calculateSeverityLevel(hatePrediction, voiceAnalysisResult.emotion)

        return AudioAnalysisResult(
            transcription = voiceAnalysisResult.transcription,
            emotion = voiceAnalysisResult.emotion,
            hateSpeechDetected = hateSpeechDetected,
            threatDetected = threatDetected,
            severityLevel = severityLevel
        )
    }

    private fun calculateSeverityLevel(hatePrediction: HatePrediction, emotion: String): Int {
        var severity = 0

        val thresholds = alertThresholdProvider.getThresholds()

        if (hatePrediction.severeToxic) severity += thresholds.severeToxicWeight
        if (hatePrediction.threat) severity += thresholds.threatWeight
        if (hatePrediction.toxic) severity += thresholds.toxicWeight
        if (hatePrediction.obscene) severity += thresholds.obsceneWeight
        if (hatePrediction.insult) severity += thresholds.insultWeight
        if (hatePrediction.identityHate) severity += thresholds.identityHateWeight

        if ( emotion == "angry") severity += thresholds.emotionWeight

        return severity
    }
}