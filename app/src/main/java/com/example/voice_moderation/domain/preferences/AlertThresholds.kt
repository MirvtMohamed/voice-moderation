
package com.example.voice_moderation.domain.preferences

data class AlertThresholds(
    val severeToxicWeight: Int = 10,
    val threatWeight: Int = 10,
    val toxicWeight: Int = 5,
    val obsceneWeight: Int = 3,
    val insultWeight: Int = 2,
    val identityHateWeight: Int = 5,
    val emotionWeight: Int = 5,
    val alertThreshold: Int = 15 // Default alert threshold
)