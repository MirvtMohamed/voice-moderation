
package com.example.voice_moderation.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.voice_moderation.domain.preferences.AlertThresholds
import com.example.voice_moderation.domain.preferences.AlertThresholdProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferencesAlertThresholdProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : AlertThresholdProvider {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "alert_thresholds_prefs", Context.MODE_PRIVATE
    )

    override fun getThresholds(): AlertThresholds {
        // Retrieve values from SharedPreferences, or use defaults if not found
        val severeToxicWeight = sharedPreferences.getInt("severe_toxic_weight", 10)
        val threatWeight = sharedPreferences.getInt("threat_weight", 10)
        val toxicWeight = sharedPreferences.getInt("toxic_weight", 5)
        val obsceneWeight = sharedPreferences.getInt("obscene_weight", 3)
        val insultWeight = sharedPreferences.getInt("insult_weight", 2)
        val identityHateWeight = sharedPreferences.getInt("identity_hate_weight", 5)
        val emotionWeight = sharedPreferences.getInt("emotion_weight", 5)
        val alertThreshold = sharedPreferences.getInt("alert_threshold", 15)

        return AlertThresholds(
            severeToxicWeight = severeToxicWeight,
            threatWeight = threatWeight,
            toxicWeight = toxicWeight,
            obsceneWeight = obsceneWeight,
            insultWeight = insultWeight,
            identityHateWeight = identityHateWeight,
            emotionWeight = emotionWeight,
            alertThreshold = alertThreshold
        )
    }

    // You might also add functions to save/update these thresholds
    fun saveThresholds(thresholds: AlertThresholds) {
        sharedPreferences.edit().apply {
            putInt("severe_toxic_weight", thresholds.severeToxicWeight)
            putInt("threat_weight", thresholds.threatWeight)
            putInt("toxic_weight", thresholds.toxicWeight)
            putInt("obscene_weight", thresholds.obsceneWeight)
            putInt("insult_weight", thresholds.insultWeight)
            putInt("identity_hate_weight", thresholds.identityHateWeight)
            putInt("emotion_weight", thresholds.emotionWeight)
            putInt("alert_threshold", thresholds.alertThreshold)
            apply()
        }
    }
}