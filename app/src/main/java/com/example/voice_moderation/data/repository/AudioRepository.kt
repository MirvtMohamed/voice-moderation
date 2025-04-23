package com.example.voice_moderation.data.repository

import com.example.voice_moderation.data.Model.AudioFile
import com.example.voice_moderation.data.network.RetrofitInstance

class AudioRepository {
    suspend fun getAudioFiles(): List<AudioFile> {
        return RetrofitInstance.api.getAudioFiles()
    }
}
