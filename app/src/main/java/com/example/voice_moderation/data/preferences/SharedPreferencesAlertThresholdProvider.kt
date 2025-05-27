package com.example.voice_moderation.data.preferences

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferencesAlertThresholdProvider @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : AlertThresholdProvider {
    override fun getThresholds(): AlertThresholds {
        return AlertThresholds(
            severeToxicWeight = sharedPreferences.getInt("severe_toxic_weight", 3),
            threatWeight = sharedPreferences.getInt("threat_weight", 3),
            toxicWeight = sharedPreferences.getInt("toxic_weight", 1),
            obsceneWeight = sharedPreferences.getInt("obscene_weight", 1),
            insultWeight = sharedPreferences.getInt("insult_weight", 1),
            identityHateWeight = sharedPreferences.getInt("identity_hate_weight", 2),
            emotionWeight = sharedPreferences.getInt("emotion_weight", 1)
        )
    }
}