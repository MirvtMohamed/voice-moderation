package com.example.voice_moderation.domain.model

data class HatePrediction(
    val toxic: Boolean,
    val severeToxic: Boolean,
    val obscene: Boolean,
    val threat: Boolean,
    val insult: Boolean,
    val identityHate: Boolean
)