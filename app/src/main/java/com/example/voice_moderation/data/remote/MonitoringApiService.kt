package com.example.voice_moderation.data.remote

import com.example.voice_moderation.data.model.api.PredictionResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MonitoringApiService {
    @Multipart
    @POST("predict")
    suspend fun analyzeAudio(
        @Part audioFile: MultipartBody.Part
    ): PredictionResponse
}
