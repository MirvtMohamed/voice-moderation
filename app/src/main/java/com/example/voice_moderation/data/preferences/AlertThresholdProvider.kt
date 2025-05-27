package com.example.voice_moderation.data.preferences

interface AlertThresholdProvider {
    fun getThresholds(): AlertThresholds
}
