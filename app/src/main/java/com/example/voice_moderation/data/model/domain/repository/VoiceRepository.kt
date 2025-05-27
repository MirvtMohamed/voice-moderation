package com.example.voice_moderation.data.model.domain.repository


import com.example.voice_moderation.data.audio.AudioStreamController
import com.example.voice_moderation.data.model.api.HatePredictionResponse
import com.example.voice_moderation.data.model.api.PredictionResponse
import com.example.voice_moderation.data.model.domain.AudioAnalysisResult
import com.example.voice_moderation.data.preferences.AlertThresholdProvider
import com.example.voice_moderation.data.remote.ApiResult
import com.example.voice_moderation.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import java.io.File
import javax.inject.Inject


class VoiceRepository @Inject constructor(
   // private val audioStreamController: AudioStreamController,
    private val remoteDataSource: RemoteDataSource,
    private val alertThresholdProvider: AlertThresholdProvider
) {
//    fun startStreaming() {
//        audioStreamController.start()
//    }
//
//    fun stopStreaming() {
//        audioStreamController.stop()
//    }
//
//    fun hasPermission(): Boolean {
//        return audioStreamController.hasAudioPermission()
//    }


    suspend fun analyzeAudio(audioFile: File): Flow<ApiResult<AudioAnalysisResult>> = flow {
        emit(ApiResult.Loading)

        when (val apiResult = remoteDataSource.analyzeAudio(audioFile)) {
            is ApiResult.Success -> {
                val response = apiResult.data
                val result = mapResponseToDomain(response)
                emit(ApiResult.Success(result))
            }
            is ApiResult.Error -> {
                emit(ApiResult.Error(apiResult.exception))
            }
            else -> {
                // This shouldn't happen
            }
        }
    }

    private fun mapResponseToDomain(response: PredictionResponse): AudioAnalysisResult {
        val hatePrediction = response.hate_prediction

        return AudioAnalysisResult(
            transcription = response.transcription,
            emotion = response.emotion,
            hateSpeechDetected = hatePrediction.toxic > 0 ||
                    hatePrediction.severe_toxic > 0 ||
                    hatePrediction.obscene > 0 ||
                    hatePrediction.identity_hate > 0,
            threatDetected = hatePrediction.threat > 0,
            severityLevel = calculateSeverityLevel(hatePrediction, response.emotion)
        )
    }

    private fun calculateSeverityLevel(hatePrediction: HatePredictionResponse, emotion: String): Int {
        var severity = 0

        val thresholds = alertThresholdProvider.getThresholds()

        if (hatePrediction.severe_toxic > 0) severity += thresholds.severeToxicWeight
        if (hatePrediction.threat > 0) severity += thresholds.threatWeight
        if (hatePrediction.toxic > 0) severity += thresholds.toxicWeight
        if (hatePrediction.obscene > 0) severity += thresholds.obsceneWeight
        if (hatePrediction.insult > 0) severity += thresholds.insultWeight
        if (hatePrediction.identity_hate > 0) severity += thresholds.identityHateWeight

        if (emotion == "fearful" || emotion == "angry") severity += thresholds.emotionWeight

        return severity
    }
}