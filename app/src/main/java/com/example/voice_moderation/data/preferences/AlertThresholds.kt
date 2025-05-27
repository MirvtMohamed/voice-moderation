package com.example.voice_moderation.data.preferences

data class AlertThresholds(
    val severeToxicWeight: Int = 3,
    val threatWeight: Int = 3,
    val toxicWeight: Int = 1,
    val obsceneWeight: Int = 1,
    val insultWeight: Int = 1,
    val identityHateWeight: Int = 2,
    val emotionWeight: Int = 1
)