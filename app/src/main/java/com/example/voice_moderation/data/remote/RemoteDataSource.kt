package com.example.voice_moderation.data.remote

import com.example.voice_moderation.data.model.api.PredictionResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiClient: ApiClient
) {
    suspend fun analyzeAudio(audioFile: File): ApiResult<PredictionResponse> {
        return try {
            val requestFile = audioFile.asRequestBody("audio/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", audioFile.name, requestFile)

            val response = apiClient.monitoringApiService.analyzeAudio(body)
            ApiResult.Success(response)
        } catch (e: Exception) {
            Timber.e(e, "Error analyzing audio")
            ApiResult.Error(e)
        }
    }
}