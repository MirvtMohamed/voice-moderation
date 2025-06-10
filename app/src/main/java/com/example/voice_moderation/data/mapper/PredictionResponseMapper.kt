package com.example.voice_moderation.data.mapper


import com.example.voice_moderation.data.model.api.HatePredictionResponse
import com.example.voice_moderation.data.model.api.PredictionResponse
import com.example.voice_moderation.domain.model.HatePrediction
import com.example.voice_moderation.domain.model.VoiceAnalysisResult


fun PredictionResponse.toDomain(): VoiceAnalysisResult {
    return VoiceAnalysisResult(
        transcription = transcription ?: "",
        emotion = emotion ?: "",
        hatePrediction = hatePrediction?.toDomain() ?: HatePrediction(
            toxic = false,
            severeToxic = false,
            obscene = false,
            threat = false,
            insult = false,
            identityHate = false
        )
    )
}

fun HatePredictionResponse.toDomain(): HatePrediction {
    return HatePrediction(
        toxic = toxic == 1,
        severeToxic = severeToxic == 1,
        obscene = obscene == 1,
        threat = threat == 1,
        insult = insult == 1,
        identityHate = identityHate == 1
    )
}