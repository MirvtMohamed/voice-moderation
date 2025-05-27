package com.example.voice_moderation.data.model.api

data class HatePredictionResponse(
    val toxic: Int,
    val severe_toxic: Int,
    val obscene: Int,
    val threat: Int,
    val insult: Int,
    val identity_hate: Int
)
