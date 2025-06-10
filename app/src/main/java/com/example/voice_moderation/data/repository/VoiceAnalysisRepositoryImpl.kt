package com.example.voice_moderation.data.repository



import com.example.voice_moderation.data.remote.ApiResult
import com.example.voice_moderation.data.remote.RemoteDataSource
import com.example.voice_moderation.domain.model.VoiceAnalysisResult
import com.example.voice_moderation.domain.repository.VoiceAnalysisRepository
import com.example.voice_moderation.data.mapper.toDomain // Import the extension function
import java.io.File
import javax.inject.Inject

class VoiceAnalysisRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : VoiceAnalysisRepository {

    override suspend fun analyzeAudio(audioFile: File): ApiResult<VoiceAnalysisResult> {
        return when (val result = remoteDataSource.analyzeAudio(audioFile)) {
            is ApiResult.Success -> {
                ApiResult.Success(result.data.toDomain())
            }
            is ApiResult.Error -> {
                ApiResult.Error(result.exception)
            }
            ApiResult.Loading -> ApiResult.Loading // Should not happen for a suspend function, but for completeness
        }
    }
}