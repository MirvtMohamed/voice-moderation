package com.example.voice_moderation.data.model.api

import com.google.gson.annotations.SerializedName

data class HatePredictionResponse(
    @SerializedName("toxic")
    val toxic: Int?,
    @SerializedName("severe_toxic")
    val severeToxic: Int?,
    @SerializedName("obscene")
    val obscene: Int?,
    @SerializedName("threat")
    val threat: Int?,
    @SerializedName("insult")
    val insult: Int?,
    @SerializedName("identity_hate")
    val identityHate: Int?
)