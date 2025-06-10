package com.example.voice_moderation.domain.preferences

interface AlertThresholdProvider {
    fun getThresholds(): AlertThresholds
}
