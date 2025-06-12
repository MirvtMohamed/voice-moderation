package com.example.voice_moderation.data.remote

import com.example.voice_moderation.data.model.api.PredictionResponse
import com.example.voice_moderation.data.model.api.SendEmailRequest
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MonitoringApiService {
    @Multipart
    @POST("predict")
    suspend fun analyzeAudio(
        @Part audioFile: MultipartBody.Part
    ): PredictionResponse

    @POST("send-alert-email") // New endpoint for sending email alerts
    suspend fun sendAlertEmail(
        @Body request: SendEmailRequest // Send the SendEmailRequest as the request body
    ): Map<String, String> // Assuming the FastAPI returns a simple JSON response like {"message": "Email sent successfully"}

}
