package com.example.voice_moderation.data.model.api

import com.google.gson.annotations.SerializedName

data class PredictionResponse(
    @SerializedName("transcription")
    val transcription: String?,
    @SerializedName("emotion")
    val emotion: String?,
    @SerializedName("hate_prediction")
    val hatePrediction: HatePredictionResponse?
)