package com.example.voice_moderation.data.model.api

data class PredictionResponse(
    val transcription: String,
    val emotion: String,
    val hate_prediction: HatePredictionResponse
)