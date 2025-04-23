package com.example.voice_moderation.data.network

import com.example.voice_moderation.data.Model.AudioFile
import retrofit2.http.GET

interface ApiService {
    @GET("audio-files") // Change path accordingly
    suspend fun getAudioFiles(): List<AudioFile>
}